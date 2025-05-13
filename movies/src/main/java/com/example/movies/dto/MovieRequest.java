package com.example.movies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MovieRequest {

    @NotBlank(message = "Movie name cannot be blank")
    private String movieName;
    @NotBlank(message = "Author cannot be blank")
    private String author;
    private Integer yearReleased; // nullable
    private Double rating;        // nullable
    private String genre;
    private Boolean released;
    private List<Long> interestedUserIds;

    public MovieRequest() {}

    public MovieRequest(String movieName, String author, Integer yearReleased, Double rating, String genre){
        this.movieName = movieName;
        this.author = author;
        this.yearReleased = yearReleased;
        this.rating = rating;
        this.genre = genre;
        this.released = false;
        this.interestedUserIds = new ArrayList<>();
    }


    public List<Long> getInterestedUserIds() {
        return interestedUserIds;
    }

    public void setInterestedUserIds(List<Long> interestedUserIds) {
        this.interestedUserIds = interestedUserIds;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYearReleased() {
        return yearReleased;
    }

    public void setYearReleased(Integer yearReleased) {
        this.yearReleased = yearReleased;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

