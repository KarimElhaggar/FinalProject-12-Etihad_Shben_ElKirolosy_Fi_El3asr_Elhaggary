package com.example.users.controller;

import com.example.users.model.User;
import com.example.users.service.UserService;
import org.springframework.web.bind.annotation.*;

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
     public User createUser(@RequestBody User user) {
         return userService.createUser(user);
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
}
