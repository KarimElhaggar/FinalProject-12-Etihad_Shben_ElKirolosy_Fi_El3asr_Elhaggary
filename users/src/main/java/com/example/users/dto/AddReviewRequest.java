package com.example.users.dto;

public class AddReviewRequest {
    private String reviewDescription; // To hold the review description
    private Double rating;            // To hold the rating

    // Getters and setters
    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}