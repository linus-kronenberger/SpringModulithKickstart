package com.example.springmodulithkickstart.movie.domain

import com.example.springmodulithkickstart.movie.api.dto.MovieDto

/*
    This service carries all functionality for dealing with the movie domain.
    It communicates between the presentation layer and the persistence layer.
*/
interface MovieService {
    /*
    This method takes the movieTitle and movieDescription
    for a new movie in order to store it.
     */
    fun registerNewMovie(movieTitle: String?, movieDescription: String?);

    /*
   This method returns all movies from the persistence layer.
    */
    fun retrieveAllMovies() : List<MovieDto>;
}