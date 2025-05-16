package com.example.contracts;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating must be at most 5.0")
    private Double rating;

    @Min(value = 0, message = "Likes count cannot be negative")
    private Long likesCount;

    private String status;

    @NotBlank(message = "Review description is required")
    private String reviewDescription;

    private Long userId;

    private Long movieId;

    private List<Long> likedUsers = new ArrayList<>();

    public ReviewRequest(Double rating, Long likesCount, String status, String reviewDescription, Long userId, Long movieId) {
        this.rating = rating;
        this.likesCount = likesCount;
        this.status = status;
        this.reviewDescription = reviewDescription;
        this.userId = userId;
        this.movieId = movieId;
    }
}
