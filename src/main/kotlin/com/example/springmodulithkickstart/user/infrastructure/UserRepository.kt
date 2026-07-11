package com.example.springmodulithkickstart.user.infrastructure

import com.example.springmodulithkickstart.user.infrastructure.db.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : CrudRepository<User, String> {
    fun findByEmail(email: String): Optional<User>
}
