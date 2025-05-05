package com.example.reviews.repository;

import com.example.reviews.constants.ReviewStatus;
import com.example.reviews.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findReviewsByUserId(Long userId);

    List<Review> findReviewsByMovieId(Long movieId);

    List<Review> findReviewsByStatus(ReviewStatus status);

    Review findReviewsById(String id);
}
