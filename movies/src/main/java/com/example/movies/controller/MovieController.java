package com.example.movies.controller;

import com.example.movies.dto.MovieRequest;
import com.example.movies.model.Movie;
import com.example.movies.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/")
    public List<Movie> getMovies() {
        return movieService.getMovies();
    }

    @PostMapping("/addMovie")
    public Movie addMovie(@Valid @RequestBody MovieRequest request) {
        System.out.println(request.getAuthor());
        System.out.println(request.getMovieName());

        Movie.Builder builder = new Movie.Builder()
                .movieName(request.getMovieName())
                .author(request.getAuthor())
                .released(false);

        // Optional fields — only set if meaningful
        if(request.getYearReleased() != null) {
            builder.yearReleased(request.getYearReleased());
        }
        if (request.getRating() != null) {
            builder.rating(request.getRating());
        }
        else{
            builder.rating(0.0);
        }
        if (request.getGenre() != null && !request.getGenre().isBlank()) {
            builder.genre(request.getGenre());
        }
        if(request.getInterestedUserIds() != null) {
            builder.interestedUserIds(request.getInterestedUserIds());
        }
        else{
            builder.interestedUserIds(new java.util.ArrayList<>());}
        Movie movie = builder.build();
        return movieService.addMovie(movie);
    }

    @DeleteMapping("/{id}")
    public String deleteMovie(@PathVariable Long id) {
        return movieService.deleteMovie(id);
    }

    @PutMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        Movie.Builder builder = new Movie.Builder().id(id)
                .movieName(request.getMovieName())
                .author(request.getAuthor())
                .released(request.isReleased());

        // Optional fields — only set if meaningful
        if(request.getYearReleased() != null) {
            builder.yearReleased(request.getYearReleased());
        }
        if (request.getRating() != null) {
            builder.rating(request.getRating());
        }
        if (request.getGenre() != null && !request.getGenre().isBlank()) {
            builder.genre(request.getGenre());
        }
        if(request.getInterestedUserIds() != null) {
            builder.interestedUserIds(request.getInterestedUserIds());
        }
        else{
            builder.interestedUserIds(new java.util.ArrayList<>());}
        Movie updatedMovieNew = builder.build();
        return movieService.updateMovie(id, updatedMovieNew);
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/ViewMoviesAboveCertainRating")
    public List<Movie> getMoviesAboveCertainRating(@RequestParam Double rating) {
        return movieService.getMoviesAboveCertainRating(rating);
    }

    @GetMapping("/ViewMoviesByGenre")
    public List<Movie> getMoviesByGenre(@RequestParam String genre) {
        return movieService.getMoviesByGenre(genre);
    }
    @GetMapping("/ViewMoviesByAuthor")
    public List<Movie> getMoviesByAuthor(@RequestParam String author) {
        return movieService.getMoviesByAuthor(author);
    }

    @GetMapping("/ViewMoviesByName")
    public List<Movie> getMovieByName(@RequestParam String name) {
        return movieService.getMovieByName(name);
    }

    @GetMapping("/RandomMovie")
    public List<Movie> getRandomMovie(@RequestParam int limit) {
        return movieService.getRandomMovies(limit);
    }

    @GetMapping("/movieExists/{id}")
    public boolean movieExists(@PathVariable Long id) {
        return movieService.movieExists(id);
    }

    @GetMapping("/getMovieAverageRating/{id}")
    public Double getAverageRating(@PathVariable Long id){
        return movieService.getMovieById(id).getRating();
    }

    @PutMapping("/updateMovieRating/{id}/{rating}")
    public String updateMovieRating(@PathVariable Long id, @PathVariable Double rating){
        movieService.updateMovieRating(id, rating);
        return "Movie rating updated successfully!";
    }

    @PutMapping("/addUserToInterestedUserIds/{movieId}/{userId}")
    public String addUserToInterestedUserIds(@PathVariable Long movieId, @PathVariable Long userId) {
        movieService.addUserToInterestedUserIds(movieId, userId);
        return "User added to interested user IDs successfully!";
    }
}