package com.example.crypto.dto

data class AuthRequestDto (
    val username: String,
    val password: String
)

data class AuthResponseDto (
    val token: String
)