package com.example.springmodulithkickstart.review.api.dto

data class CreateReviewRequestDto(
    val movieId: String,
    val reviewText: String? = null,
    val rating: Int? = null
)
