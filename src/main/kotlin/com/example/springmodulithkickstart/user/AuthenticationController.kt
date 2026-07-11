package com.example.springmodulithkickstart.user

import com.example.springmodulithkickstart.user.api.dto.LoginUserDto
import com.example.springmodulithkickstart.user.api.dto.RegisterUserDto
import com.example.springmodulithkickstart.user.api.dto.TokenResponseDto
import com.example.springmodulithkickstart.user.domain.JwtService
import com.example.springmodulithkickstart.user.domain.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auth")
@RestController
class AuthenticationController(
    private val jwtService: JwtService,
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun register(@RequestBody registerUserDto: RegisterUserDto): ResponseEntity<TokenResponseDto> {
        userService.signup(registerUserDto)
        val loginUserDto = LoginUserDto(registerUserDto.email, registerUserDto.password);
        return authenticateWithLoginUserDto(loginUserDto);
    }

    @PostMapping("/login")
    fun authenticate(@RequestBody loginUserDto: LoginUserDto): ResponseEntity<TokenResponseDto> {
        return authenticateWithLoginUserDto(loginUserDto);
    }

    fun authenticateWithLoginUserDto(loginUserDto: LoginUserDto): ResponseEntity<TokenResponseDto> {
        val authenticatedUser = userService.authenticate(loginUserDto)
        val jwtToken = jwtService.generateToken(authenticatedUser)
        val loginResponse = TokenResponseDto(token = jwtToken, expiresIn = jwtService.expirationTime)
        return ResponseEntity.ok(loginResponse)
    }
}
