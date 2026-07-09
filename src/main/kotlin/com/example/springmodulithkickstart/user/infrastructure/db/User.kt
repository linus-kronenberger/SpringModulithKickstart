package com.example.springmodulithkickstart.user.infrastructure.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date


@Table(name = "users")
@Entity
class User : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    var id: String? = null

    @Column(nullable = false)
    var fullName: String = ""

    @Column(unique = true, length = 100, nullable = false)
    var email: String = ""

    @Column(name = "password", nullable = false)
    private var _password: String = ""

    override fun getPassword(): String = _password

    fun setPassword(value: String) {
        _password = value
    }

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    var createdAt: Date? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Date? = null

    override fun getUsername(): String = email
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
