package com.example.reviews.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "users-service")
public interface UsersClient {
    @GetMapping("/followers/{id}")
    List<Long> getUserFollowersById(@PathVariable long id);

    @GetMapping("/userExists/{id}")
    boolean userExists(@PathVariable long id);
}