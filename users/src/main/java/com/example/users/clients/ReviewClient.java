package com.example.users.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//me7tain review wa notification yezwado functions fa service wa controller 3ashan a call it wa na integrate
@FeignClient(name = "reviews-service")
public interface ReviewClient {
    @GetMapping("/movie/{movieId}")
    ResponseEntity<List<Long>> getReviewsByMovie(@PathVariable("movieId") Long movieId); // hia strin for now wa change in integration
}
