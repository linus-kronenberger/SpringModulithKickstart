package com.example.springmodulithkickstart.user.infrastructure.jwt

import com.example.springmodulithkickstart.user.domain.JwtService
import com.example.springmodulithkickstart.user.infrastructure.db.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date

/**
 * Implementation of [JwtService] using the jjwt library.
 * Handles JWT token creation, parsing, and validation with HMAC-SHA256 signing.
 * Secret key and expiration time are configured via application properties.
 */
@Service
class JwtServiceImpl : JwtService {

    @Value("\${security.jwt.secret-key}")
    private val secretKey: String = ""

    @Value("\${security.jwt.expiration-time}")
    override val expirationTime: Long = 0

    private val signInKey: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the JWT token
     * @return the username embedded in the token
     */
    override fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    /**
     * Extracts a specific claim from the token using the given claims resolver function.
     *
     * @param T the type of the claim value
     * @param token the JWT token
     * @param claimsResolver a function to extract the desired claim from the claims object
     * @return the extracted claim value
     */
    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    /**
     * Generates a JWT token for the given user details, including the user's role
     * as an additional claim when the user details are a [User] instance.
     *
     * @param userDetails the user details to encode into the token
     * @return a signed JWT token string
     */
    override fun generateToken(userDetails: UserDetails): String {
        val extraClaims = if (userDetails is User) {
            mapOf("role" to userDetails.role.name)
        } else {
            emptyMap<String, Any>()
        }
        return generateToken(extraClaims, userDetails)
    }

    /**
     * Generates a JWT token with additional claims for the given user details.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details to encode
     * @return a signed JWT token string
     */
    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return buildToken(extraClaims, userDetails, expirationTime)
    }

    /**
     * Builds a signed JWT token with the given claims, subject, and expiration.
     *
     * @param extraClaims additional claims to include
     * @param userDetails the user details providing the subject
     * @param expiration the token expiration duration in milliseconds
     * @return a compact, signed JWT token string
     */
    private fun buildToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        expiration: Long
    ): String {
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(signInKey, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * Validates whether the given JWT token is valid for the specified user details.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to check against
     * @return true if the token's subject matches the username and the token is not expired
     */
    override fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    /**
     * Checks whether the given token has expired.
     *
     * @param token the JWT token
     * @return true if the token's expiration date is before the current time
     */
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    /**
     * Extracts the expiration date from the given token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    /**
     * Parses the given token and extracts all claims.
     *
     * @param token the JWT token
     * @return the claims body of the token
     */
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(signInKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
