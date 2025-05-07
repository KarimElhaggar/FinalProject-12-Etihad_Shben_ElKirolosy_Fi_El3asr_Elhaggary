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
        try {
            userService.deleteUser(id);
            return "User deleted successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/{userId}/follow/{followUserId}")
    public String followUser(@PathVariable Long userId, @PathVariable Long followUserId) {
        try {
            userService.followUser(userId, followUserId);
            return "User " + userId + " is now following user " + followUserId;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/{userId}/unfollow/{unfollowUserId}")
    public String unfollowUser(@PathVariable Long userId, @PathVariable Long unfollowUserId) {
        try {
            userService.unfollowUser(userId, unfollowUserId);
            return "User " + userId + " has unfollowed user " + unfollowUserId;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/ban/{id}")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("User with ID " + id + " has been banned.");// wa law 3ayzin by name easy bardo
    }
}
