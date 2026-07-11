package com.example.springmodulithkickstart.review.infrastructure.db

import com.example.springmodulithkickstart.user.infrastructure.db.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.io.Serializable

@Entity
class Review : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    @Column(nullable = false)
    var movieId: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    var reviewer: User? = null

    var reviewText: String? = null

    var rating: Int? = null
}