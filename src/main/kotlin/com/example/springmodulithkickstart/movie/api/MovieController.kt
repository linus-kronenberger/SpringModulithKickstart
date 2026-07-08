package com.example.springmodulithkickstart.movie.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/movie")
class MovieController {
    @GetMapping("/new")
    fun registerMovie(movieName : String, movieDescription : String) {

    }
}