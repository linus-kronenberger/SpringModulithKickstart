package com.example.springmodulithkickstart.user.api

data class RegisterUserDto(
    val email: String = "",
    val password: String = "",
    val fullName: String = ""
)
