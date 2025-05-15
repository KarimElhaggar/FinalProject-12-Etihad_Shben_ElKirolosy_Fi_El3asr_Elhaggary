package com.example.reviews.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(name = "movie-service", url = "http://localhost:8080/movies")
public interface MoviesClient {

    @GetMapping("movieExists/{id}")
    boolean movieExists(@PathVariable long id);

    @GetMapping("getMovieAverageRating/{id}")
    Double getMovieAverageRating(@PathVariable Long id);

    @GetMapping("/updateMovieRating/{id}/{rating}")
    String updateMovie(@PathVariable Long id, @PathVariable Double rating);
}