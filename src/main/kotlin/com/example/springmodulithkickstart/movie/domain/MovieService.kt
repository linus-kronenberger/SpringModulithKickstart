package com.example.springmodulithkickstart.movie.domain

interface MovieService {
    fun registerNewMovie(movieName : String, movieDescription : String){};
}