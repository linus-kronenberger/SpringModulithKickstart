package com.example.springmodulithkickstart.user.api.dto

data class LoginResponse(
    val token: String = "",
    val expiresIn: Long = 0
)
