package com.example.springmodulithkickstart.user.api.dto

data class TokenResponseDto(
    val token: String = "",
    val expiresIn: Long = 0
)
