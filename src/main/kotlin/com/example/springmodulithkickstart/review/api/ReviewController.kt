package com.example.springmodulithkickstart.review.api

import com.example.springmodulithkickstart.review.api.dto.CreateReviewRequest
import com.example.springmodulithkickstart.review.api.dto.ReviewResponse
import com.example.springmodulithkickstart.review.domain.ReviewService
import com.example.springmodulithkickstart.user.infrastructure.db.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/review")
class ReviewController(
    private val reviewService: ReviewService
) {
    @PostMapping
    fun createReview(
        @RequestBody request: CreateReviewRequest,
        authentication: Authentication
    ): ResponseEntity<ReviewResponse> {
        val user = authentication.principal as User
        val response = reviewService.createReview(request, user.id!!)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}