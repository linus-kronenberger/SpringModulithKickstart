package com.example.springmodulithkickstart.user.infrastructure.security

import com.example.springmodulithkickstart.user.infrastructure.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class ApplicationConfiguration(private val userRepository: UserRepository) {
    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            userRepository.findByEmail(username)
                .orElseThrow { UsernameNotFoundException("User not found") }
        }
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.getAuthenticationManager()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
}
