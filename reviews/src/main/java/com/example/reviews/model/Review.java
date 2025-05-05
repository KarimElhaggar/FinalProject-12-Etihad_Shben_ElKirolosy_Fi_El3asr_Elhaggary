package com.example.reviews.model;

import com.example.reviews.constants.ReviewStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("reviews")
public class Review {
    @Id
    private String id;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating must be at most 5.0")
    private Double rating;

    @NotNull(message = "Likes count is required")
    @Min(value = 0, message = "Likes count cannot be negative")
    private Long likesCount;

    @NotNull(message = "Status is required")
    private ReviewStatus status;

    @NotBlank(message = "Review description is required")
    private String reviewDescription;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    private List<Long> likedUsers = new ArrayList<>();

    public Review(Double rating, Long likesCount, ReviewStatus status, String reviewDescription, Long userId, Long movieId, List<Long> likedUsers) {
        this.rating = rating;
        this.likesCount = likesCount;
        this.status = status;
        this.reviewDescription = reviewDescription;
        this.userId = userId;
        this.movieId = movieId;
        this.likedUsers = likedUsers;
    }

    public Review(Double rating, Long likesCount, ReviewStatus status, String reviewDescription, Long userId, Long movieId) {
        this.rating = rating;
        this.likesCount = likesCount;
        this.status = status;
        this.reviewDescription = reviewDescription;
        this.userId = userId;
        this.movieId = movieId;
    }
}
