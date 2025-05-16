package com.example.users.controller;

import com.example.users.dto.UserRequest;
import com.example.users.model.User;
import com.example.users.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/followers/{id}")
    public List<Long> getUserFollowersById(@PathVariable Long id) {
        return userService.getUserFollowersById(id);
    }

    // is this needed? also probably need to remove the try catch
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        User.Builder builder = new User.Builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword());
        // Optional fields — only set if meaningful
        if(request.getName() != null) {
            builder.name(request.getName());
        }
        builder.admin(request.isAdmin());
        builder.banned(request.isBanned());
        builder.following(new ArrayList<>());
        builder.followers(new ArrayList<>());

        try {
            User user = builder.build();
            return ResponseEntity.ok(userService.createUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }

    @PostMapping("/follow/{followUserId}")
    public String followUser(@PathVariable Long followUserId) {
        userService.followUser(followUserId);
        return "Followed user " + followUserId;
    }

    @PostMapping("/unfollow/{unfollowUserId}")
    public String unfollowUser(@PathVariable Long unfollowUserId) {
        userService.unfollowUser(unfollowUserId);
        return "Unfollowed user " + unfollowUserId;
    }

    @PutMapping("/ban/{id}")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("User with ID " + id + " has been banned.");// wa law 3ayzin by name easy bardo
    }

    @PutMapping("/unban/{id}")
    public ResponseEntity<String> unBanUser(@PathVariable Long id) {
        userService.unBanUser(id);
        return ResponseEntity.ok("User with ID " + id + " has been unbanned.");// wa law 3ayzin by name easy bardo
    }

    @GetMapping("/userExists/{id}")
    public boolean userExists(@PathVariable Long id){
        return userService.getUserById(id) != null;
    }

    @PutMapping("/subscribeToNotification/{userId}/{movieId}")
    public String subscribeToNotification(@PathVariable Long userId, @PathVariable Long movieId) {
        userService.subscribeToNotification(userId, movieId);
        return "Subscribed to the movie notifications successfully!";
    }

    @PostMapping("/addReview/{userId}/{movieId}")
    public String addReview(@PathVariable Long userId, @PathVariable Long movieId, @RequestBody String reviewDescription, @RequestBody Double rating) {
        userService.addReview(userId, movieId, reviewDescription, rating);
        return "Review added successfully!";
    }

}