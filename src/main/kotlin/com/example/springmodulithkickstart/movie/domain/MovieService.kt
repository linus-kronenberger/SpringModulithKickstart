package com.example.springmodulithkickstart.movie.domain

import com.example.springmodulithkickstart.movie.api.dto.MovieDto

/**
 * Service interface for movie-related operations.
 * Handles communication between the presentation layer and the persistence layer
 * for the movie domain.
 */
interface MovieService {
    /**
     * Registers a new movie with the given title and description.
     *
     * @param movieTitle the title of the new movie, may be null
     * @param movieDescription a description of the new movie, may be null
     */
    fun registerNewMovie(movieTitle: String?, movieDescription: String?);

    /**
     * Retrieves all movies from the persistence layer.
     *
     * @return a list of all movies as [MovieDto] objects
     */
    fun retrieveAllMovies() : List<MovieDto>;
}