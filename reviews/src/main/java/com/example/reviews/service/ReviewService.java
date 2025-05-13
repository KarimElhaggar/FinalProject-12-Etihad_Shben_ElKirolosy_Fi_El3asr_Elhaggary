package com.example.reviews.service;

import com.example.reviews.constants.ReviewStatus;
import com.example.reviews.model.Review;
import com.example.reviews.repository.ReviewRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    public List<Review> viewReviewsByUser(Long userId) {
        if (userId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid user id");
        }
        //TODO check if user exists
        return reviewRepository.findReviewsByUserId(userId);
    }

    public Review toggleLike(Long userId, String reviewId) {

        if(reviewId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid review id");
        }

        Review reviewToBeChanged = reviewRepository.findById(reviewId).orElse(null);
        if (reviewToBeChanged == null) {
            return null;
        }

        //TODO check if user exists
        if(userId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid user id");
        }

        if(reviewToBeChanged.getLikedUsers().contains(userId)) {
            reviewToBeChanged.setLikesCount(reviewToBeChanged.getLikesCount() - 1);
            reviewToBeChanged.getLikedUsers().remove(userId);
        }

        else{
            reviewToBeChanged.setLikesCount(reviewToBeChanged.getLikesCount() + 1);
            reviewToBeChanged.getLikedUsers().add(userId);
        }
        //TODO notify owner
        return reviewRepository.save(reviewToBeChanged);
    }

    public List<Review> viewReviewsForCertainMovie(Long movieId) {
        //TODO add validation for movie exists using rabbitMQ

        if(movieId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid movie id");
        }
        return reviewRepository.findReviewsByMovieId(movieId);
    }

    public List<Review> viewPendingReviews() {
        return reviewRepository.findReviewsByStatus(ReviewStatus.PENDING);
    }

    public Review createReview(Review review) {
        if (review == null) {
            throw new InvalidDataAccessApiUsageException("Review cannot be null");
        }
        //TODO notify users
        //TODO update movie rating
        return reviewRepository.save(review);
    }

    public Review getReviewById(String reviewId) {
        if (reviewId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid review id");
        }

        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new InvalidDataAccessApiUsageException("Review not found"));
    }

    public Review updateReview(String reviewId, Review updatedReview) {
        if (reviewId == null || updatedReview == null) {
            throw new InvalidDataAccessApiUsageException("Review ID or data cannot be null");
        }

        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new InvalidDataAccessApiUsageException("Review not found"));

        existing.setRating(updatedReview.getRating() != null ? updatedReview.getRating() : existing.getRating());
        existing.setLikesCount(updatedReview.getLikesCount() != null ? updatedReview.getLikesCount() : existing.getLikesCount());
        existing.setStatus(updatedReview.getStatus() != null ? updatedReview.getStatus() : existing.getStatus());
        existing.setReviewDescription(updatedReview.getReviewDescription() != null ? updatedReview.getReviewDescription() : existing.getReviewDescription());
        existing.setUserId(updatedReview.getUserId() != null ? updatedReview.getUserId() : existing.getUserId());
        existing.setMovieId(updatedReview.getMovieId() != null ? updatedReview.getMovieId() : existing.getMovieId());

        //TODO update movie rating
        return reviewRepository.save(existing);
    }

    public void deleteReview(String reviewId) {
        if (reviewId == null) {
            throw new InvalidDataAccessApiUsageException("Review ID cannot be null");
        }

        if(!reviewRepository.existsById(reviewId)) {
            throw new InvalidDataAccessApiUsageException("Review does not exist");
        }

        //Todo update movie rating
        reviewRepository.deleteById(reviewId);
    }
}
