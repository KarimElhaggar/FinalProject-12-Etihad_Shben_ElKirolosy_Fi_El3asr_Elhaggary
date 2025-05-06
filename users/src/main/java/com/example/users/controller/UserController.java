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
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}
