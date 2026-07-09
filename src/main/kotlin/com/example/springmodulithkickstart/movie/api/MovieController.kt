package com.example.springmodulithkickstart.movie.api

import com.example.springmodulithkickstart.movie.api.dto.MovieDTO
import com.example.springmodulithkickstart.movie.domain.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/movie")
class MovieController(private val movieService: MovieService) {
    @PostMapping("/new")
    fun registerMovie(@RequestBody movieDTO: MovieDTO): ResponseEntity<MovieDTO> {
        movieService.registerNewMovie(movieDTO.movieTitle, movieDTO.movieDescription)
        return ResponseEntity.ok(movieDTO);
    }
}