package com.example.springmodulithkickstart.user.infrastructure

import com.example.springmodulithkickstart.user.api.dto.LoginUserDto
import com.example.springmodulithkickstart.user.api.dto.RegisterUserDto
import com.example.springmodulithkickstart.user.domain.Role
import com.example.springmodulithkickstart.user.domain.UserService
import com.example.springmodulithkickstart.user.infrastructure.db.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Implementation of [UserService] for user registration and authentication.
 */
/**
 * Implementation of [UserService] for user registration and authentication.
 * Uses Spring Security's [AuthenticationManager] and [PasswordEncoder]
 * to handle secure credential validation and storage.
 */
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    /**
     * Registers a new user with the provided registration data.
     * Encodes the password before persisting and assigns the default [Role.USER] role.
     *
     * @param input the registration data containing email, password, and fullName
     * @return the persisted [User] entity
     */
    override fun signup(input: RegisterUserDto): User {
        val user = User()
        user.fullName = input.fullName
        user.email = input.email
        user.password = passwordEncoder.encode(input.password)!!
        user.role = Role.USER
        return userRepository.save(user)
    }

    /**
     * Authenticates a user with the given login credentials.
     * Delegates to [AuthenticationManager] for credential validation.
     *
     * @param input the login data containing email and password
     * @return the authenticated [User] entity
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    override fun authenticate(input: LoginUserDto): User {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(input.email, input.password)
        )
        return userRepository.findByEmail(input.email)
            .orElseThrow()
    }
}
