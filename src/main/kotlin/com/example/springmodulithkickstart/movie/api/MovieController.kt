package com.example.springmodulithkickstart.movie.api

import com.example.springmodulithkickstart.movie.api.dto.MovieDTO
import com.example.springmodulithkickstart.movie.domain.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/movie")
class MovieController(private val movieService: MovieService, service: MovieService, movieService1: MovieService) {
    @PostMapping("/new")
    fun registerMovie(@RequestBody movieDTO: MovieDTO): ResponseEntity<String> {
        movieService.registerNewMovie(movieDTO.title, movieDTO.description)
        return ResponseEntity.ok("Successfully registered new movie: '${movieDTO.title}'");
    }

    @GetMapping("/all")
    fun getAllMovies(): List<MovieDTO> {
        return movieService.retrieveAllMovies();
    }
}