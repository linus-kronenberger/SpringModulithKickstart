package com.example.springmodulithkickstart.movie.domain

import com.example.springmodulithkickstart.movie.infrastructure.db.Movie

/*
    This service carries all functionality for dealing with the movie domain.
    It communicates between the presentation layer and the persistence layer.
*/
interface MovieService {
    /*
    This method takes the movieTitle and movieDescription
    for a new movie in order to store it.
     */
    fun registerNewMovie(movieTitle: String?, movieDescription: String?){};
}