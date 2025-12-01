package com.example.crypto

import com.example.crypto.model.User
import com.example.crypto.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.password.PasswordEncoder

@EnableScheduling
@SpringBootApplication
class CryptocurrenctApplication {

    @Bean
    fun init(userRepository: UserRepository, passwordEncoder: PasswordEncoder) = CommandLineRunner {
        // Check if any user exists
        if (userRepository.count() == 0L) {
            // Create a default user if no users exist
            val defaultUser = User(
                email = "user@example.com",
                username = "defaultuser",
                password = passwordEncoder.encode("password123") // Encode a default password
            )
            userRepository.save(defaultUser)
            println("Created default user with ID: ${defaultUser.id}")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CryptocurrenctApplication>(*args)
}
