package com.example.reviews.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movies-service", url = "http://movies-service:8086")
public interface MoviesClient {

    @GetMapping("/movies/movieExists/{id}")
    boolean movieExists(@PathVariable long id);

    @GetMapping("/movies/getMovieAverageRating/{id}")
    Double getMovieAverageRating(@PathVariable Long id);

    @GetMapping("/movies/updateMovieRating/{id}/{rating}")
    String updateMovie(@PathVariable Long id, @PathVariable Double rating);
}