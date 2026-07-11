package com.example.springmodulithkickstart.review.domain

import com.example.springmodulithkickstart.review.api.dto.CreateReviewRequest
import com.example.springmodulithkickstart.review.api.dto.ReviewResponse
import com.example.springmodulithkickstart.shared.event.MovieCreatedEvent

interface ReviewService {
    fun onMovieCreated(event: MovieCreatedEvent)
    fun createReview(request: CreateReviewRequest, userId: String): ReviewResponse
}
