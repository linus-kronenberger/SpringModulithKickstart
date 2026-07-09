package com.example.springmodulithkickstart.movie.infrastructure

import com.example.springmodulithkickstart.movie.infrastructure.db.Movie
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : CrudRepository<Movie, String> {
    fun findByTitle(title: String?) : List<Movie>
}