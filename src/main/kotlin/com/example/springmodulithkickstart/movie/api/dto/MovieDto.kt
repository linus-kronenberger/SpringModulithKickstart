package com.example.springmodulithkickstart.movie.api.dto

import org.jetbrains.annotations.NotNull

class MovieDto {
    var movieId: String? = null
    @NotNull
    var title: String? = null
    @NotNull
    var description: String? = null
}