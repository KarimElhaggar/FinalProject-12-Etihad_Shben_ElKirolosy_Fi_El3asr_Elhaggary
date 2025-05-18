package com.example.users.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "movies-service")
public interface MoviesClient {

    @PutMapping("/addUserToInterestedUserIds/{movieId}/{userId}")
    String addUserToInterestedUserIds(@PathVariable Long movieId, @PathVariable Long userId);

    @GetMapping("movieExists/{id}")
    boolean movieExists(@PathVariable long id);
}
