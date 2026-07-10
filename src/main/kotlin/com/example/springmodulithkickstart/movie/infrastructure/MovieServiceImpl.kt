package com.example.springmodulithkickstart.movie.infrastructure

import com.example.springmodulithkickstart.movie.api.dto.MovieDto
import com.example.springmodulithkickstart.movie.domain.MovieService
import com.example.springmodulithkickstart.movie.infrastructure.db.Movie
import com.example.springmodulithkickstart.movie.infrastructure.mapper.MovieDTOMapper
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl : MovieService {

    private val movieRepository : MovieRepository;
    private val movieDTOMapper : MovieDTOMapper;

    constructor(movieRepository: MovieRepository, movieDTOMapper: MovieDTOMapper) {
        this.movieRepository = movieRepository;
        this.movieDTOMapper = movieDTOMapper;
    }

    override fun registerNewMovie(movieTitle: String?, movieDescription: String?) {
        val newMovie: Movie = Movie();
        newMovie.title = movieTitle;
        newMovie.description = movieDescription;
        movieRepository.save(newMovie);
    }

    override fun retrieveAllMovies() : List<MovieDto> {
        val movies : List<Movie> = movieRepository.findAll().toList();
        return movies.map {
            movieDTOMapper.mapToMovieDTO(it)
        }.toList()
    }
}