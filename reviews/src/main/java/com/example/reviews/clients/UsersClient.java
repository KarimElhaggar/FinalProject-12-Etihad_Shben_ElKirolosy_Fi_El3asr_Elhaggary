package com.example.reviews.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// TODO(elgharieb, 13-05-2025) update
@FeignClient(name = "user-service", url = "http://localhost:8080/users")
public interface UsersClient {
    @GetMapping("/followers/{id}")
    List<Long> getUserFollowersById(@PathVariable long id);
}