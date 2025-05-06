package com.example.users.service;

import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void banUser(Long userId) {
        // Logic to ban a user
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
    }
    //me7tain review wa notification yezwado functions fa service wa controller 3ashan a call it wa na integrate
    @FeignClient(name = "review-service")
    public interface ReviewClient {
        @GetMapping("/reviews/by-movie/{movieId}")
        List<String> getReviewsByMovie(@PathVariable("movieId") Long movieId); // hia strin for now wa change in integration
    }

    @FeignClient(name = "notification-service")
    public interface NotificationClient {
        @PostMapping("/notifications/subscribe")
        void subscribe(@RequestParam("userId") Long userId, @RequestParam("topicId") Long topicId);
    }
    // will be added when added in review service

//    @FeignClient(name = "review-service")
//    public interface ReviewClient {
//        @PostMapping("/reviews/add")
//        ResponseEntity<String> addReview(@RequestBody ReviewRequest request);
//    }



}
