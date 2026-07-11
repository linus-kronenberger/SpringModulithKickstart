package com.example.springmodulithkickstart.user.domain

import com.example.springmodulithkickstart.user.api.dto.LoginUserDto
import com.example.springmodulithkickstart.user.api.dto.RegisterUserDto
import com.example.springmodulithkickstart.user.infrastructure.db.User

/**
 * Service interface for user-related operations.
 * Handles user registration and authentication.
 */
interface UserService {

    /**
     * Registers a new user account.
     *
     * @param input the registration data containing email, password, and fullName
     * @return the created User entity
     */
    fun signup(input: RegisterUserDto): User

    /**
     * Authenticates an existing user with the given credentials.
     *
     * @param input the login data containing email and password
     * @return the authenticated User entity
     */
    fun authenticate(input: LoginUserDto): User
}
