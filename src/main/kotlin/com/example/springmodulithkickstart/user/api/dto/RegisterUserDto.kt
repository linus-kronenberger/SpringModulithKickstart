package com.example.springmodulithkickstart.user.api.dto

data class RegisterUserDto(
    val email: String = "",
    val password: String = "",
    val fullName: String = ""
)
