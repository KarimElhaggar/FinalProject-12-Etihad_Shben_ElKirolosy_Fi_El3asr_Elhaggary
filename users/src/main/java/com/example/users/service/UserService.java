package com.example.users.service;

import com.example.contracts.ReviewRequest;
import com.example.users.clients.MoviesClient;
import com.example.users.model.User;
import com.example.users.rabbitmq.RabbitMQProducer;
import com.example.users.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final AuthService authService;
    UserRepository userRepository;
    private final MoviesClient moviesClient;
    private final RabbitMQProducer rabbitMQProducer;

    public UserService(UserRepository userRepository, AuthService authService, MoviesClient moviesClient, RabbitMQProducer rabbitMQProducer) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.moviesClient = moviesClient;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @CacheEvict(value = "user_cache", key = "#userId")
    public void banUser(Long userId) {
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

    @CacheEvict(value = "user_cache", key = "#userId")
    public void unBanUser(Long userId) {
        User admin = authService.getLoggedInUser();

        if (!admin.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can unban users.");
        }

        if (admin.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot unban yourself.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setBanned(false);
        userRepository.save(user);
    }

    @CachePut(value = "user_cache", key = "#user.id")
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        String hashed = PasswordHasherSingleton.getInstance().hash(user.getPassword());
        user.setPassword(hashed);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "user_cache", key = "#id")
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Detach Hibernate proxies by creating a clean copy
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setName(user.getName());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setEmail(user.getEmail());
        cleanUser.setPassword(user.getPassword());
        cleanUser.setAdmin(user.isAdmin());
        cleanUser.setBanned(user.isBanned());

        // Convert followers and following into regular List<User> or List<Long> as needed
        cleanUser.setFollowers(new ArrayList<>(user.getFollowers()));
        cleanUser.setFollowing(new ArrayList<>(user.getFollowing()));

        return cleanUser;
    }

    public List<Long> getUserFollowersById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return user.getFollowers();
    }

    @CachePut(value = "user_cache", key = "#id")
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        User admin = authService.getLoggedInUser();

        if (!admin.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update users.");
        }

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

    @CacheEvict(value = "user_cache", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        User admin = authService.getLoggedInUser();

        if (!admin.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete users.");
        }

        if (admin.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete yourself.");
        }

        userRepository.deleteById(id);
    }

    @CacheEvict(value = "user_cache", key = "#followUserId")
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

    @CacheEvict(value = "user_cache", key = "#followUserId")
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

    public void subscribeToNotification(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        moviesClient.addUserToInterestedUserIds(movieId, userId);
    }

    public void addReview(Long userId, Long movieId, String reviewDescription, Double rating) {
        ReviewRequest reviewRequest = new ReviewRequest(rating, 0L, "pending", reviewDescription, userId, movieId);
        if (userId == null || movieId == null || reviewDescription == null || rating == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required.");
        }
        if(!moviesClient.movieExists(movieId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found.");
        }
        if(!userRepository.existsById(userId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        rabbitMQProducer.sendReviewRequest(reviewRequest);
    }
}
