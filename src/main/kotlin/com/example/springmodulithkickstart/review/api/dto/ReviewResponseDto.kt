package com.example.springmodulithkickstart.review.api.dto

data class ReviewResponseDto(
    val id: String? = null,
    val movieId: String? = null,
    val reviewerId: String? = null,
    val reviewerName: String? = null,
    val reviewText: String? = null,
    val rating: Int? = null
)
