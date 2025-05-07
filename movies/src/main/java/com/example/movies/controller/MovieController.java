package com.example.movies.controller;

import com.example.movies.dto.MovieRequest;
import com.example.movies.model.Movie;
import com.example.movies.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public String getMovies() {
        return movieService.getMovies();
    }

    @PostMapping("/addMovie")
    public String addMovie(@RequestBody MovieRequest request) {
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
    public String updateMovie(@PathVariable Long id, @RequestBody Movie updatedMovie) {
        return movieService.updateMovie(id, updatedMovie);
    }

    @GetMapping("/{id}")
    public String getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/ViewMoviesAboveCertainRating")
    public String getMoviesAboveCertainRating(@RequestParam double rating) {
        return movieService.getMoviesAboveCertainRating(rating);
    }

    @GetMapping("/ViewMoviesByGenre")
    public String getMoviesByGenre(@RequestParam String genre) {
        return movieService.getMoviesByGenre(genre);
    }
    @GetMapping("/ViewMoviesByAuthor")
    public String getMoviesByAuthor(@RequestParam String author) {
        return movieService.getMoviesByAuthor(author);
    }

    @GetMapping("/ViewMoviesByName")
    public String getMovieByName(@RequestParam String name) {
        return movieService.getMovieByName(name);
    }

    @GetMapping("/RandomMovie")
    public String getRandomMovie(@RequestParam int limit) {
        return movieService.getRandomMovies(limit);
    }

}
