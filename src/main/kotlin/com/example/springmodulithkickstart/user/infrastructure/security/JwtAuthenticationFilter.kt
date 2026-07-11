package com.example.springmodulithkickstart.user.infrastructure.security

import com.example.springmodulithkickstart.user.domain.JwtService
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : Filter {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val alreadyFiltered = httpRequest.getAttribute(FILTERED_ATTRIBUTE) != null
        if (alreadyFiltered) {
            chain.doFilter(request, response)
            return
        }
        httpRequest.setAttribute(FILTERED_ATTRIBUTE, true)

        val authHeader = httpRequest.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response)
            return
        }

        try {
            val jwt = authHeader.substring(7)
            val userEmail = jwtService.extractUsername(jwt)
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(userEmail)

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(httpRequest)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

            chain.doFilter(request, response)
        } catch (exception: Exception) {
            logger.error("Error on processing bearer token: e={}", exception.message);
            httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResponse.writer.write("The token is either invalid or expired.")
            httpResponse.writer.flush()
        }
    }

    companion object {
        private const val FILTERED_ATTRIBUTE = "com.example.springmodulithkickstart.user.infrastructure.security.JwtAuthenticationFilter.FILTERED"
    }
}
