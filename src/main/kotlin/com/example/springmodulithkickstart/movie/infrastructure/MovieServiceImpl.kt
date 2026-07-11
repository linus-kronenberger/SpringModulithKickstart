package com.example.springmodulithkickstart.movie.infrastructure

import com.example.springmodulithkickstart.movie.api.dto.MovieDto
import com.example.springmodulithkickstart.movie.domain.MovieService
import com.example.springmodulithkickstart.shared.event.MovieCreatedEvent
import com.example.springmodulithkickstart.movie.infrastructure.db.Movie
import com.example.springmodulithkickstart.movie.infrastructure.mapper.MovieDtoMapper
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

/**
 * Implementation of [MovieService] for managing movies.
 * Persists movies to the database, publishes domain events on creation,
 * and caches retrieval results.
 */
@Service
class MovieServiceImpl : MovieService {

    private val movieRepository : MovieRepository;
    private val movieDTOMapper : MovieDtoMapper;
    private val eventPublisher : ApplicationEventPublisher;

    constructor(movieRepository: MovieRepository, movieDTOMapper: MovieDtoMapper, eventPublisher: ApplicationEventPublisher) {
        this.movieRepository = movieRepository;
        this.movieDTOMapper = movieDTOMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new movie entity, persists it, and publishes a [MovieCreatedEvent].
     *
     * @param movieTitle the title of the new movie, may be null
     * @param movieDescription a description of the new movie, may be null
     */
    override fun registerNewMovie(movieTitle: String?, movieDescription: String?) {
        val newMovie: Movie = Movie();
        newMovie.title = movieTitle;
        newMovie.description = movieDescription;
        val savedMovie = movieRepository.save(newMovie);
        eventPublisher.publishEvent(MovieCreatedEvent(savedMovie.movieId!!, savedMovie.title, savedMovie.description));
    }

    /**
     * Retrieves all movies from the persistence layer.
     * The result is cached under the cache name "movies".
     *
     * @return a list of all movies as [MovieDto] objects
     */
    @Cacheable("movies")
    override fun retrieveAllMovies() : List<MovieDto> {
        val movies : List<Movie> = movieRepository.findAll().toList();
        return movies.map {
            movieDTOMapper.mapToMovieDTO(it)
        }.toList()
    }
}