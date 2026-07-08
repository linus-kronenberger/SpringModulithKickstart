package com.example.springmodulithkickstart.user.infrastructure

import com.example.springmodulithkickstart.user.api.LoginUserDto
import com.example.springmodulithkickstart.user.api.RegisterUserDto
import com.example.springmodulithkickstart.user.infrastructure.db.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
) {
    fun signup(input: RegisterUserDto): User {
        val user = User()
        user.fullName = input.fullName
        user.email = input.email
        user.password = passwordEncoder.encode(input.password)!!
        return userRepository.save(user)
    }

    fun authenticate(input: LoginUserDto): User {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(input.email, input.password)
        )
        return userRepository.findByEmail(input.email)
            .orElseThrow()
    }
}
