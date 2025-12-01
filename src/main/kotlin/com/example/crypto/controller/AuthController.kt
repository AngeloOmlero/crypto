package com.example.crypto.controller

import com.example.crypto.dto.AuthRequestDto
import com.example.crypto.dto.AuthResponseDto
import com.example.crypto.dto.CreateUserDto
import com.example.crypto.dto.UserDto
import com.example.crypto.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/register")
    fun register(@RequestBody createUserDto: CreateUserDto): Mono<ResponseEntity<UserDto>> {
        return authService.register(createUserDto)
            .map { ResponseEntity.ok(it) }
    }

    @PostMapping("/login")
    fun login(@RequestBody authRequestDto: AuthRequestDto): Mono<ResponseEntity<AuthResponseDto>> {
        return authService.login(authRequestDto)
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): Mono<ResponseEntity<UserDto>> {
        return authService.getCurrentUser(userDetails.username)
            .map { ResponseEntity.ok(it) }
    }

}