package com.example.reviews.service;

import com.example.contracts.ReviewRequest;
import com.example.reviews.clients.MoviesClient;
import com.example.reviews.clients.UsersClient;
import com.example.reviews.constants.ReviewStatus;
import com.example.reviews.constants.NotificationType;
import com.example.reviews.model.Review;
import com.example.reviews.rabbitmq.RabbitMQProducer;
import com.example.reviews.repository.ReviewRepository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    private final RabbitMQProducer rabbitMQProducer;
    private final UsersClient usersClient;
    private final MoviesClient moviesClient;

    public List<Review> viewReviewsByUser(Long userId) {
        if (userId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid user id");
        }
        if(!usersClient.userExists(userId)) {
            throw new InvalidDataAccessApiUsageException("User does not exist");
        }

        return reviewRepository.findReviewsByUserId(userId);
    }

    public Review toggleLike(Long userId, String reviewId) {
        if(userId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid user id");
        }

        if(reviewId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid review id");
        }

        Review reviewToBeChanged = reviewRepository.findById(reviewId).orElse(null);
        if (reviewToBeChanged == null) {
            return null;
        }
        if(!usersClient.userExists(userId)) {
            throw new InvalidDataAccessApiUsageException("User does not exist");
        }

        if(reviewToBeChanged.getLikedUsers().contains(userId)) {
            reviewToBeChanged.setLikesCount(reviewToBeChanged.getLikesCount() - 1);
            reviewToBeChanged.getLikedUsers().remove(userId);
        }

        else{
            reviewToBeChanged.setLikesCount(reviewToBeChanged.getLikesCount() + 1);
            reviewToBeChanged.getLikedUsers().add(userId);

            List<Long> usersToBeNotified = new ArrayList<>();
            usersToBeNotified.add(reviewToBeChanged.getUserId());

            rabbitMQProducer.sendToNotifications(usersToBeNotified, NotificationType.LIKEDREVIEW);
        }
        return reviewRepository.save(reviewToBeChanged);
    }

    public List<Review> viewReviewsForCertainMovie(Long movieId) {
        if(movieId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid movie id");
        }
        if(!moviesClient.movieExists(movieId))
            throw new InvalidDataAccessApiUsageException("Please provide a valid movie id");

        return reviewRepository.findReviewsByMovieId(movieId);
    }

    public List<Review> viewPendingReviews() {
        return reviewRepository.findReviewsByStatus(ReviewStatus.PENDING);
    }

    public Review createReview(Review review) {
        if (review == null) {
            throw new InvalidDataAccessApiUsageException("Review cannot be null");
        }

        List<Long> followers = usersClient.getUserFollowersById(review.getUserId());

        rabbitMQProducer.sendToNotifications(followers, NotificationType.NEWREVIEW);

        Double movieAVGRating = moviesClient.getMovieAverageRating(review.getMovieId());
        Integer reviewsCountByMovieID = reviewRepository.findReviewsByMovieId(review.getMovieId()).size();
        Double sum = movieAVGRating * reviewsCountByMovieID + review.getRating();
        Double newAverage = sum / (reviewsCountByMovieID + 1);
        moviesClient.updateMovie(review.getMovieId(), newAverage);
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

        Double oldRating = existing.getRating();

        existing.setRating(updatedReview.getRating() != null ? updatedReview.getRating() : existing.getRating());
        existing.setLikesCount(updatedReview.getLikesCount() != null ? updatedReview.getLikesCount() : existing.getLikesCount());
        existing.setStatus(updatedReview.getStatus() != null ? updatedReview.getStatus() : existing.getStatus());
        existing.setReviewDescription(updatedReview.getReviewDescription() != null ? updatedReview.getReviewDescription() : existing.getReviewDescription());
        existing.setUserId(updatedReview.getUserId() != null ? updatedReview.getUserId() : existing.getUserId());
        existing.setMovieId(updatedReview.getMovieId() != null ? updatedReview.getMovieId() : existing.getMovieId());

        //calculates a new average rating for the movie by subtracting the old rating and adding the new rating
        Double movieAVGRating = moviesClient.getMovieAverageRating(existing.getMovieId());
        Integer reviewsCountByMovieID = reviewRepository.findReviewsByMovieId(existing.getMovieId()).size();
        Double sum = movieAVGRating * reviewsCountByMovieID - oldRating + existing.getRating();
        Double newAverage = sum / reviewsCountByMovieID;
        moviesClient.updateMovie(existing.getMovieId(), newAverage);

        return reviewRepository.save(existing);
    }

    public void deleteReview(String reviewId) {

        if (reviewId == null) {
            throw new InvalidDataAccessApiUsageException("Review ID cannot be null");
        }

        Review reviewToBeDeleted = reviewRepository.findById(reviewId).orElse(null);

        if(reviewToBeDeleted == null) {
            throw new InvalidDataAccessApiUsageException("Review does not exist");
        }

        Double movieAVGRating = moviesClient.getMovieAverageRating(reviewToBeDeleted.getMovieId());
        Integer reviewsCountByMovieID = reviewRepository.findReviewsByMovieId(reviewToBeDeleted.getMovieId()).size();
        Double sum = movieAVGRating * reviewsCountByMovieID - reviewToBeDeleted.getRating();
        Double newAverage = 0.0;
        if(reviewsCountByMovieID != 1)
            newAverage = sum / (reviewsCountByMovieID - 1);
        moviesClient.updateMovie(reviewToBeDeleted.getMovieId(), newAverage);

        reviewRepository.deleteById(reviewId);
    }

    public Review convertDtoToReview(ReviewRequest reviewDto){
        Review review = new Review();

        review.setRating(reviewDto.getRating());
        review.setLikesCount(reviewDto.getLikesCount());
        review.setStatus(ReviewStatus.valueOf(reviewDto.getStatus().toUpperCase()));
        review.setReviewDescription(reviewDto.getReviewDescription());
        review.setUserId(reviewDto.getUserId());
        review.setMovieId(reviewDto.getMovieId());
        review.setLikedUsers(reviewDto.getLikedUsers() != null ? reviewDto.getLikedUsers() : new ArrayList<>());

        return review;
    }

    public static void main(String[] args) {
        ReviewRequest r = new ReviewRequest();

        System.out.println(r.getStatus());
        System.out.println(r.getReviewDescription());
        System.out.println(r.getLikedUsers());
        System.out.println(r.getMovieId());
        System.out.println(r.getUserId());
        System.out.println(r.getLikesCount());
        System.out.println(r.getRating());
    }
}



