package com.example.reviews.controller;

import com.example.contracts.ReviewRequest;
import com.example.reviews.model.Review;
import com.example.reviews.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/seed")
    public String seed() {
        reviewService.seed();
        return "reviews seeded successfully";
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        List<Review> reviews = reviewService.viewReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    } //

    @PostMapping("/{reviewId}/toggle-like/{userId}")
    public ResponseEntity<Review> toggleLike(
            @PathVariable String reviewId,
            @PathVariable Long userId) {

        Review updatedReview = reviewService.toggleLike(userId, reviewId);
        return ResponseEntity.ok(updatedReview);
    } //

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Review>> getReviewsByMovie(@PathVariable Long movieId) {
        List<Review> reviews = reviewService.viewReviewsForCertainMovie(movieId);
        return ResponseEntity.ok(reviews);
    } //

    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> reviews = reviewService.viewPendingReviews();
        return ResponseEntity.ok(reviews);
    } //

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody @Valid ReviewRequest reviewDto) {
        Review savedReview = reviewService.createReview(reviewDto);
        return ResponseEntity.ok(savedReview);
    } //

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable String reviewId) {
        Review review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable String reviewId,
            @RequestBody ReviewRequest updatedReviewDto) {
        Review review = reviewService.updateReview(reviewId, updatedReviewDto);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable String reviewId) {
        Review review = reviewService.approveReview(reviewId);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

}
