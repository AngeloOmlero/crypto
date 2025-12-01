package com.example.crypto.dto

data class UserRegistrationRequest(
    val email: String,
    val username: String,
    val password: String
)
