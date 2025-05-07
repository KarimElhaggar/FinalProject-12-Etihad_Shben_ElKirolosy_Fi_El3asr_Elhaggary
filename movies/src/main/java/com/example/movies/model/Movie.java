package com.example.movies.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String movieName;
    private String author;
    private int yearReleased;
    private double rating;
    private String genre;
    private boolean released;

    @ElementCollection
    private List<Long> interestedUserIds;


    public Movie() {}

    // Constructor for Builder
    private Movie(Builder builder) {
        this.id = builder.id;
        this.movieName = builder.movieName;
        this.author = builder.author;
        this.yearReleased = builder.yearReleased;
        this.rating = builder.rating;
        this.genre = builder.genre;
        this.interestedUserIds = builder.interestedUserIds;
        this.released =builder.released;
    }

    //   public void setId(int id) { this.id = id; }
    //   public void setMovieName(String movieName) { this.movieName = movieName; }
    //   public void setAuthor(String author) { this.author = author; }
    //  public void setYearReleased(int yearReleased) { this.yearReleased = yearReleased; }

    public static class Builder {
        private Long id;
        private String movieName;
        private String author;
        private int yearReleased;
        private double rating;
        private String genre;
        private List<Long> interestedUserIds;
        private boolean released;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder movieName(String movieName) {
            this.movieName = movieName;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder yearReleased(int yearReleased) {
            this.yearReleased = yearReleased;
            return this;
        }

        public Builder rating(double rating) {
            this.rating = rating;
            return this;
        }

        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder interestedUserIds(List<Long> interestedUserIds) {
            this.interestedUserIds = interestedUserIds;
            return this;
        }
        public Builder released(boolean released) {
            this.released = released;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

}
