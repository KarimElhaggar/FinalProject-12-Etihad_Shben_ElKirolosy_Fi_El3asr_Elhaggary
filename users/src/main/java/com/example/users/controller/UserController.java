package com.example.users.controller;

import com.example.users.model.User;
import com.example.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class UserController {

    UserService userService;

    @PutMapping("/ban/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id) {
    userService.banUser(id);
    return ResponseEntity.ok("User with ID " + id + " has been banned.");// wa law 3ayzin by name easy bardo
    }
}
