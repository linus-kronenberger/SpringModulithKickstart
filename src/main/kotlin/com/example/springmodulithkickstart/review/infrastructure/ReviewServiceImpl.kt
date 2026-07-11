package com.example.springmodulithkickstart.review.infrastructure

import com.example.springmodulithkickstart.review.api.dto.CreateReviewRequest
import com.example.springmodulithkickstart.review.api.dto.ReviewResponse
import com.example.springmodulithkickstart.review.domain.ReviewService
import com.example.springmodulithkickstart.review.infrastructure.db.Review
import com.example.springmodulithkickstart.shared.event.MovieCreatedEvent
import com.example.springmodulithkickstart.user.infrastructure.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : ReviewService {

    private val log = LoggerFactory.getLogger(ReviewServiceImpl::class.java)

    @ApplicationModuleListener
    override fun onMovieCreated(event: MovieCreatedEvent) {
        log.info("New movie created: '{}' (ID: {}). Creating reviews for all users.", event.title, event.movieId)

        val users = userRepository.findAll()
        users.forEach { user ->
            val review = Review()
            review.movieId = event.movieId
            review.reviewer = user
            review.reviewText = null
            review.rating = null
            reviewRepository.save(review)
        }

        log.info("Reviews created for all {} users for movie '{}'.", users.count(), event.title)
    }

    override fun createReview(request: CreateReviewRequest, userId: String): ReviewResponse {
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val existingReview = reviewRepository.findByMovieIdAndReviewer_Id(request.movieId, userId)
        val review = existingReview.orElseGet { Review() }
        review.movieId = request.movieId
        review.reviewer = user
        review.reviewText = request.reviewText
        review.rating = request.rating
        val saved = reviewRepository.save(review)
        return ReviewResponse(
            id = saved.id,
            movieId = saved.movieId,
            reviewerId = saved.reviewer?.id,
            reviewerName = saved.reviewer?.fullName,
            reviewText = saved.reviewText,
            rating = saved.rating
        )
    }
}
