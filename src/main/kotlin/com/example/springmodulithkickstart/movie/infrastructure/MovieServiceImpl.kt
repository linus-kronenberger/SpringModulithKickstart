package com.example.springmodulithkickstart.movie.infrastructure

import com.example.springmodulithkickstart.movie.domain.MovieService
import com.example.springmodulithkickstart.movie.infrastructure.db.Movie
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl : MovieService {

    private val movieRepository : MovieRepository;

    constructor(movieRepository: MovieRepository) {
        this.movieRepository = movieRepository;
    }

    override fun registerNewMovie(movieTitle: String?, movieDescription: String?) {
        val newMovie: Movie = Movie();
        newMovie.title = movieTitle;
        newMovie.description = movieDescription;
        movieRepository.save(newMovie);
    }

}