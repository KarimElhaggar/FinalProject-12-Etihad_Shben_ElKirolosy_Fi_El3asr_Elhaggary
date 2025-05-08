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
import java.util.Objects;

@Service
public class UserService {
    private final AuthService authService;
    UserRepository userRepository;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public void banUser(Long userId) {
        // Logic to ban a user
        User admin = authService.getLoggedInUser();

        if (!admin.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can ban users.");
        }

        if (admin.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot ban yourself.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

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

    public void followUser(Long followUserId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User user = authService.getLoggedInUser();

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No logged in user found.");
        }

        User followUser = userRepository.findById(followUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to follow not found."));

        if (Objects.equals(user.getId(), followUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself.");
        }

        if (user.isBanned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are banned and cannot follow users.");
        }

        if (followUser.isBanned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot follow a banned user.");
        }

        if (!user.getFollowing().contains(followUser.getId())) {
            user.getFollowing().add(followUser.getId());
            followUser.getFollowers().add(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already following this user.");
        }
    }

    public void unfollowUser(Long followUserId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User user = authService.getLoggedInUser();

        User followUser = userRepository.findById(followUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to unfollow not found."));

        if (Objects.equals(user.getId(), followUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot unfollow yourself.");
        }

        if (user.getFollowing().contains(followUser.getId())) {
            user.getFollowing().remove(followUser.getId());
            followUser.getFollowers().remove(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not following this user.");
        }
    }

    @FeignClient(name = "notification-service")
    public interface NotificationClient {
        @PostMapping("/notifications/subscribe")
        void subscribe(@RequestParam("userId") Long userId, @RequestParam("topicId") Long topicId);
    }
}
