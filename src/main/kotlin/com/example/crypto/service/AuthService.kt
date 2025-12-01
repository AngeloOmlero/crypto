package com.example.crypto.service

import com.example.crypto.dto.AuthRequestDto
import com.example.crypto.dto.AuthResponseDto
import com.example.crypto.dto.CreateUserDto
import com.example.crypto.dto.UserDto
import com.example.crypto.model.User
import com.example.crypto.repository.UserRepository
import com.example.crypto.util.JWTUtil
import com.example.crypto.util.UserDetailsServiceImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JWTUtil,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsServiceImpl
) {
    fun register(createUserDto: CreateUserDto): Mono<UserDto> {
        return Mono.fromCallable {
            userRepository.findByUsername(createUserDto.username)
        }.subscribeOn(Schedulers.boundedElastic())
            .flatMap { existingUser ->
                // If existingUser is not null, it means the username is taken
                Mono.error<UserDto>(IllegalArgumentException("Username ${createUserDto.username} is already taken"))
            }
            .switchIfEmpty(
                // If existingUser was null (Mono was empty), then proceed to create the user
                Mono.fromCallable {
                    val user = User(
                        email = createUserDto.email,
                        username = createUserDto.username,
                        password = passwordEncoder.encode(createUserDto.password)
                    )
                    userRepository.save(user)
                }.subscribeOn(Schedulers.boundedElastic())
                    .map { savedUser -> UserDto(savedUser.id, savedUser.email, savedUser.username) }
            )
    }

    fun login(request: AuthRequestDto): Mono<AuthResponseDto> {
        return Mono.fromCallable {
            userDetailsService.loadUserByUsername(request.username)
        }.subscribeOn(Schedulers.boundedElastic())
            .flatMap { userDetails ->
                Mono.fromCallable {
                    authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken(request.username, request.password)
                    )
                }.subscribeOn(Schedulers.boundedElastic())
                    .map { authentication ->
                        SecurityContextHolder.getContext().authentication = authentication
                        val authenticatedUserDetails = authentication.principal as UserDetails
                        val token = jwtUtil.generateToken(authenticatedUserDetails)
                        AuthResponseDto(token)
                    }
            }
            .onErrorResume { ex ->
                when (ex) {
                    is BadCredentialsException -> Mono.error(BadCredentialsException("Invalid username or password"))
                    is UsernameNotFoundException -> Mono.error(UsernameNotFoundException("Invalid username: ${request.username}"))
                    else -> Mono.error(RuntimeException("Authentication failed: ${ex.message}"))
                }
            }
    }

    fun getCurrentUser(username: String): Mono<UserDto> {
        return Mono.fromCallable {
            userRepository.findByUsername(username)
        }.subscribeOn(Schedulers.boundedElastic())
            .flatMap { user ->
                user?.let { Mono.just(UserDto(it.id, it.email, it.username)) }
                    ?: Mono.error(UsernameNotFoundException("User not found: $username"))
            }
    }
}
