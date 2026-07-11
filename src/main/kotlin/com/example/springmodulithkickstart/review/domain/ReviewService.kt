package com.example.springmodulithkickstart.review.domain

import com.example.springmodulithkickstart.review.api.dto.CreateReviewRequestDto
import com.example.springmodulithkickstart.review.api.dto.ReviewResponseDto
import com.example.springmodulithkickstart.shared.event.MovieCreatedEvent

interface ReviewService {
    fun onMovieCreated(event: MovieCreatedEvent)
    fun createReview(request: CreateReviewRequestDto, userId: String): ReviewResponseDto
}
