package com.example.users.service;

import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired private PasswordService passwordService;
    @Autowired private SessionService sessionService;
    @Autowired private TokenService tokenService;
    @Autowired private UserRepository userRepository;

    public void register(User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        String hashed = passwordService.hash(user.getPassword());
        user.setPassword(hashed);
        userRepository.save(user);
    }

    public String login(String username, String password) {
        try {
            if (username == null || password == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required.");
            }

            User user = userRepository.findByUsername(username);

            if (user == null || !passwordService.matches(password, user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }

            if (user.isBanned()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is banned");
            }

            String token = tokenService.generate(user.getId());
            sessionService.save(token, user.getId());

            return token;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error: " + e.getMessage(), e);
        }
    }

    public User getUserBySession(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorizationHeader");
        }

        String token = authorizationHeader.substring(7);

        try {
            String userId = tokenService.decode(token);

            String sessionValue = sessionService.get(token);
            if (sessionValue == null || !sessionValue.equals(userId)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired or invalid");
            }

            Long id = Long.parseLong(userId);
            return userRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    public User getLoggedInUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }

        return getUserBySession(authorizationHeader);
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new IllegalArgumentException("Token is required.");
        }

        String token = authorizationHeader.substring(7);

        sessionService.delete(token);
    }
}
