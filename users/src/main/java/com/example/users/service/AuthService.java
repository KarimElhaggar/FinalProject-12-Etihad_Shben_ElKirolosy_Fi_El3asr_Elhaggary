//package com.example.users.service;
//
//import com.example.users.model.User;
//import com.example.users.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//@Service
//public class AuthService {
//
//    @Autowired private PasswordService passwordService;
//    @Autowired private SessionService sessionService;
//    @Autowired private TokenService tokenService;
//    @Autowired private UserRepository userRepository;
//
//    public void register(User user) {
//        if (user.getUsername() == null || user.getPassword() == null) {
//            throw new IllegalArgumentException("Username and password are required.");
//        }
//        if (userRepository.findByUsername(user.getUsername()) != null) {
//            throw new RuntimeException("Username already exists.");
//        }
//
//        String hashed = passwordService.hash(user.getPassword());
//        user.setPassword(hashed);
//        userRepository.save(user);
//    }
//
//    public String login(String username, String password) {
//        if (username == null || password == null) {
//            throw new IllegalArgumentException("Username and password are required.");
//        }
//
//        User user = userRepository.findByUsername(username);
//        if (user == null || !passwordService.matches(password, user.getPassword())) {
//            throw new RuntimeException("Invalid username or password.");
//        }
//
//        String token = tokenService.generate(user.getId());
//        sessionService.save(token);
//
//        return token;
//    }
//
//    public User getUserBySession(String token) {
//        if (token == null || token.isEmpty()) {
//            throw new IllegalArgumentException("Token is required.");
//        }
//
//        String userId = tokenService.decode(token);
//        Long id = Long.parseLong(userId);
//        return userRepository.findById(id).orElseThrow(() ->
//                 new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
//        );
//    }
//
//    public void logout(String token) {
//        if (token == null || token.isEmpty()) {
//            throw new IllegalArgumentException("Token is required.");
//        }
//
//        sessionService.delete(token);
//    }
//}