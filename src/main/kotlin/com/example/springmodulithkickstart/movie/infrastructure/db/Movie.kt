package com.example.springmodulithkickstart.movie.infrastructure.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    var movieId : String? = null;

    @Column(nullable = false, unique = true)
    var title : String? = null;

    @Column(nullable = false)
    var description : String? = null;
}