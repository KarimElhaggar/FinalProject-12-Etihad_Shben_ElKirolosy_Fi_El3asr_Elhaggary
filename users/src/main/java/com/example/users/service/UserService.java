package com.example.users.service;

import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
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
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if(user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if(user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if(user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        if(user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if(user.isAdmin() != existingUser.isAdmin()) {
            existingUser.setAdmin(user.isAdmin());
        }
        if(user.isBanned() != existingUser.isBanned()) {
            existingUser.setBanned(user.isBanned());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        userRepository.deleteById(id);
    }

    public void followUser(Long userId, Long followUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        User followUser = userRepository.findById(followUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to follow not found."));

        if (!user.getFollowing().contains(followUser.getId())) {
            user.getFollowing().add(followUser.getId());
            followUser.getFollowers().add(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already following this user.");
        }
    }

    @FeignClient(name = "notification-service")
    public interface NotificationClient {
        @PostMapping("/notifications/subscribe")
        void subscribe(@RequestParam("userId") Long userId, @RequestParam("topicId") Long topicId);
    }
    public void unfollowUser(Long userId, Long followUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        User followUser = userRepository.findById(followUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to unfollow not found."));

        if (user.getFollowing().contains(followUser.getId())) {
            user.getFollowing().remove(followUser.getId());
            followUser.getFollowers().remove(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not following this user.");
        }
    }
}

