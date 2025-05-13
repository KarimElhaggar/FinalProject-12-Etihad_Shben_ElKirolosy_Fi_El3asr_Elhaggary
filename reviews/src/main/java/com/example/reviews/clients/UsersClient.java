package com.example.reviews.clients;

import com.example.users.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// TODO(elgharieb, 13-05-2025)
@FeignClient(name = "user-service", url = "http://localhost:8080/users")
public interface UsersClient {
    @GetMapping("/{id}")
    User getUserById(@PathVariable long id);
}