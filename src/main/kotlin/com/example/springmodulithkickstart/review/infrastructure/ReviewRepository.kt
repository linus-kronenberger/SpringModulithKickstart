package com.example.springmodulithkickstart.review.infrastructure

import com.example.springmodulithkickstart.review.infrastructure.db.Review
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ReviewRepository : CrudRepository<Review, String> {
    fun findByMovieIdAndReviewer_Id(movieId: String, reviewerId: String): Optional<Review>
}
