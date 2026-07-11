package com.example.springmodulithkickstart.user.infrastructure

import com.example.springmodulithkickstart.user.domain.Role
import com.example.springmodulithkickstart.user.infrastructure.db.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    @Value("\${admin.email}")
    private val adminEmail: String = ""

    @Value("\${admin.password}")
    private val adminPassword: String = ""

    @Value("\${admin.full-name}")
    private val adminFullName: String = ""

    override fun run(vararg args: String) {
        if (userRepository.findByEmail(adminEmail).isEmpty) {
            val admin = User()
            admin.fullName = adminFullName
            admin.email = adminEmail
            admin.password = passwordEncoder.encode(adminPassword)!!
            admin.role = Role.ADMIN
            userRepository.save(admin)
        }
    }
}
