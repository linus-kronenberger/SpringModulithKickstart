package com.example.springmodulithkickstart.shared.event

data class MovieCreatedEvent(
    val movieId: String,
    val title: String?,
    val description: String?
)
