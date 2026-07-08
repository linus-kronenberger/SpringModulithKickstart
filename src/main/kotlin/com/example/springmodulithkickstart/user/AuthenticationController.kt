package com.example.springmodulithkickstart.user

import com.example.springmodulithkickstart.user.api.LoginUserDto
import com.example.springmodulithkickstart.user.api.RegisterUserDto
import com.example.springmodulithkickstart.user.api.dto.LoginResponse
import com.example.springmodulithkickstart.user.infrastructure.AuthenticationService
import com.example.springmodulithkickstart.user.infrastructure.db.User
import com.example.springmodulithkickstart.user.security.jwt.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auth")
@RestController
class AuthenticationController(
    private val jwtService: JwtService,
    private val authenticationService: AuthenticationService
) {
    @PostMapping("/signup")
    fun register(@RequestBody registerUserDto: RegisterUserDto): ResponseEntity<User> {
        val registeredUser = authenticationService.signup(registerUserDto)
        return ResponseEntity.ok(registeredUser)
    }

    @PostMapping("/login")
    fun authenticate(@RequestBody loginUserDto: LoginUserDto): ResponseEntity<LoginResponse> {
        val authenticatedUser = authenticationService.authenticate(loginUserDto)
        val jwtToken = jwtService.generateToken(authenticatedUser)
        val loginResponse = LoginResponse(token = jwtToken, expiresIn = jwtService.expirationTime)
        return ResponseEntity.ok(loginResponse)
    }
}
