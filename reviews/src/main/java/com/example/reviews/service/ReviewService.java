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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    private final RabbitMQProducer rabbitMQProducer;
    private final UsersClient usersClient;
    private final MoviesClient moviesClient;

    public List<Review> viewReviewsByUser(Long userId) {
        log.info("fetching reviews for user with id: {}", userId);

        if (userId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid user id");
        }
        if(!usersClient.userExists(userId)) {
            throw new InvalidDataAccessApiUsageException("User does not exist");
        }

        log.info("reviews fetched for user with id: {} and will be returned as a list of reviews.", userId);

        return reviewRepository.findReviewsByUserId(userId);
    }

    public Review toggleLike(Long userId, String reviewId) {
        log.info("Toggling like for review with id: {} and user with id: {}", reviewId, userId);

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

        log.info("Review with id: {} and user with id: {} has been updated.", reviewId, userId);

        return reviewRepository.save(reviewToBeChanged);
    }

    public List<Review> viewReviewsForCertainMovie(Long movieId) {
        log.info("fetching reviews for movie with id: " + movieId);

        if(movieId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid movie id");
        }
        if(!moviesClient.movieExists(movieId))
            throw new InvalidDataAccessApiUsageException("Movie does not exist");

        log.info("reviews fetched for movie with id: {} and will be returned as a list of reviews.", movieId);

        return reviewRepository.findReviewsByMovieId(movieId);
    }

    public List<Review> viewPendingReviews() {
        log.info("fetching pending reviews");
        log.info("pending reviews fetched and will be returned as a list of reviews.");
        return reviewRepository.findReviewsByStatus(ReviewStatus.PENDING);
    }

    public Review createReview(ReviewRequest reviewDto) {
        log.info("Creating review for user with id: {} and movie with id: {}", reviewDto.getUserId(), reviewDto.getMovieId());

        if (reviewDto == null)
            throw new InvalidDataAccessApiUsageException("Review cannot be null");

        Review review = convertDtoToReview(reviewDto);

        if (review == null)
            throw new InvalidDataAccessApiUsageException("Review cannot be null");

        if(!usersClient.userExists(review.getUserId()))
            throw new InvalidDataAccessApiUsageException("User does not exist");

        if(!moviesClient.movieExists(review.getMovieId()))
            throw new InvalidDataAccessApiUsageException("Movie does not exist");

        List<Long> followers = usersClient.getUserFollowersById(review.getUserId());

        rabbitMQProducer.sendToNotifications(followers, NotificationType.NEWREVIEW);

        Double movieAVGRating = moviesClient.getMovieAverageRating(review.getMovieId());
        Integer reviewsCountByMovieID = reviewRepository.findReviewsByMovieId(review.getMovieId()).size();
        Double sum = movieAVGRating * reviewsCountByMovieID + review.getRating();
        Double newAverage = sum / (reviewsCountByMovieID + 1);
        moviesClient.updateMovie(review.getMovieId(), newAverage);

        log.info("Review created for user with id: {} and movie with id: {}", review.getUserId(), review.getMovieId());

        return reviewRepository.save(review);
    }

    public Review getReviewById(String reviewId) {
        log.info("fetching review with id: {}", reviewId);

        if (reviewId == null) {
            throw new InvalidDataAccessApiUsageException("Please provide a valid review id");
        }

        log.info("review fetched with id: {} and will be returned as a review object.", reviewId);

        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new InvalidDataAccessApiUsageException("Review not found"));
    }

    public Review updateReview(String reviewId, ReviewRequest updatedReviewDto) {
        log.info("Updating review with id: {} and data: {}", reviewId, updatedReviewDto);

        Review updatedReview = convertDtoToReview(updatedReviewDto);
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

        log.info("Review with id: {} and data: {} has been updated.", reviewId, updatedReview);

        return reviewRepository.save(existing);
    }

    public void deleteReview(String reviewId) {
        log.info("Deleting review with id: {}", reviewId);

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

        log.info("Review with id: {} has been deleted.", reviewId);
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
}