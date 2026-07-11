package com.example.springmodulithkickstart.user.domain

import org.springframework.security.core.userdetails.UserDetails

/**
 * Service interface for JWT token operations.
 * Provides methods for token generation, validation, and username extraction.
 */
interface JwtService {

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the JWT token
     * @return the username embedded in the token
     */
    fun extractUsername(token: String): String

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details to encode into the token
     * @return a signed JWT token string
     */
    fun generateToken(userDetails: UserDetails): String

    /**
     * Validates whether the given JWT token is valid for the specified user details.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to check against
     * @return true if the token is valid and not expired, false otherwise
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean

    /**
     * The configured expiration time for generated tokens in milliseconds.
     */
    val expirationTime: Long
}
