package com.example.users.service;

import com.example.contracts.ReviewRequest;
import com.example.users.clients.MoviesClient;
import com.example.users.model.User;
import com.example.users.rabbitmq.RabbitMQProducer;
import com.example.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Service
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final MoviesClient moviesClient;
    private final RabbitMQProducer rabbitMQProducer;

    @Autowired
    public UserService(UserRepository userRepository, AuthService authService, MoviesClient moviesClient, RabbitMQProducer rabbitMQProducer) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.moviesClient = moviesClient;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @CacheEvict(value = "user_cache", key = "#userId")
    public void banUser(Long userId) {
        log.info("Banning user with id: {}", userId);
        User admin = authService.getLoggedInUser();

        if (!admin.isAdmin()) {
            log.warn("User {} is not admin. Cannot ban user.", admin.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can ban users.");
        }

        if (admin.getId().equals(userId)) {
            log.warn("Admin {} attempted to ban themselves.", admin.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot ban yourself.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found for banning.", userId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        if (user.isBanned()) {
            log.warn("User with id {} is already banned.", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already banned.");
        }

        user.setBanned(true);
        userRepository.save(user);
        log.info("User with id: {} has been banned.", userId);
    }

    @CacheEvict(value = "user_cache", key = "#userId")
    public void unBanUser(Long userId) {
        log.info("Unbanning user with id: {}", userId);
        User admin = authService.getLoggedInUser();

        if (!admin.isAdmin()) {
            log.warn("User {} is not admin. Cannot unban user.", admin.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can unban users.");
        }

        if (admin.getId().equals(userId)) {
            log.warn("Admin {} attempted to unban themselves.", admin.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot unban yourself.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found for unbanning.", userId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        if (!user.isBanned()) {
            log.warn("User with id {} is not banned.", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not banned.");
        }

        user.setBanned(false);
        userRepository.save(user);
        log.info("User with id: {} has been unbanned.", userId);
    }

    @CachePut(value = "user_cache", key = "#user.id")
    public User createUser(User user) {
        log.info("Creating user with email: {} and username: {}", user.getEmail(), user.getUsername());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Email {} is already taken.", user.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Username {} is already taken.", user.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        String hashed = PasswordHasherSingleton.getInstance().hash(user.getPassword());
        user.setPassword(hashed);
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());
        return savedUser;
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Cacheable(value = "user_cache", key = "#id")
    public User getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found.", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });

        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setName(user.getName());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setEmail(user.getEmail());
        cleanUser.setPassword(user.getPassword());
        cleanUser.setAdmin(user.isAdmin());
        cleanUser.setBanned(user.isBanned());
        cleanUser.setFollowers(new ArrayList<>(user.getFollowers()));
        cleanUser.setFollowing(new ArrayList<>(user.getFollowing()));

        log.info("Returning clean user object for user id: {}", id);
        return cleanUser;
    }

    public boolean userExists(Long id) {
        return this.userRepository.existsById(id);
    }

    public List<Long> getUserFollowersById(Long id) {
        log.info("Getting followers for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found for fetching followers.", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                });

        return user.getFollowers();
    }

    @CachePut(value = "user_cache", key = "#id")
    public User updateUser(Long id, User user) {
        log.info("Updating user with id: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found for update.", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
                });

        User admin = authService.getLoggedInUser();
        if (!admin.isAdmin()) {
            log.warn("User {} is not admin. Cannot update user {}", admin.getId(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update users.");
        }

        if(user.getName() != null) existingUser.setName(user.getName());
        if(user.getUsername() != null) existingUser.setUsername(user.getUsername());
        if(user.getPassword() != null) existingUser.setPassword(user.getPassword());
        if(user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if(user.isAdmin() != existingUser.isAdmin()) existingUser.setAdmin(user.isAdmin());
        if(user.isBanned() != existingUser.isBanned()) existingUser.setBanned(user.isBanned());

        log.info("User with id {} updated successfully.", id);
        return userRepository.save(existingUser);
    }

    @CacheEvict(value = "user_cache", key = "#id")
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User with id {} not found for deletion.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        User admin = authService.getLoggedInUser();
        if (!admin.isAdmin()) {
            log.warn("User {} is not admin. Cannot delete user {}.", admin.getId(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete users.");
        }

        if (admin.getId().equals(id)) {
            log.warn("Admin {} attempted to delete themselves.", admin.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete yourself.");
        }

        userRepository.deleteById(id);
        log.info("User with id {} deleted successfully.", id);
    }

    @CacheEvict(value = "user_cache", key = "#followUserId")
    public void followUser(Long followUserId) {
        log.info("User attempting to follow user with id: {}", followUserId);

        User user = authService.getLoggedInUser();
        if (user == null) {
            log.warn("No logged in user found for follow.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No logged in user found.");
        }

        User followUser = userRepository.findById(followUserId)
                .orElseThrow(() -> {
                    log.warn("User to follow with id {} not found.", followUserId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User to follow not found.");
                });

        if (Objects.equals(user.getId(), followUser.getId())) {
            log.warn("User {} attempted to follow themselves.", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself.");
        }

        if (user.isBanned() || followUser.isBanned()) {
            log.warn("Follow blocked due to ban. Follower: {}, Followee: {}", user.getId(), followUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Banned users cannot follow/be followed.");
        }

        if (!user.getFollowing().contains(followUser.getId())) {
            user.addFollowing(followUser.getId());
            followUser.addFollower(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);
            log.info("User {} now follows user {}", user.getId(), followUser.getId());
        } else {
            log.warn("User {} already follows user {}", user.getId(), followUser.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already following this user.");
        }
    }

    @CacheEvict(value = "user_cache", key = "#followUserId")
    public void unfollowUser(Long followUserId) {
        log.info("Unfollow request for user id: {}", followUserId);

        User user = authService.getLoggedInUser();
        User followUser = userRepository.findById(followUserId)
                .orElseThrow(() -> {
                    log.warn("User to unfollow with id {} not found.", followUserId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User to unfollow not found.");
                });

        if (Objects.equals(user.getId(), followUser.getId())) {
            log.warn("User {} attempted to unfollow themselves.", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot unfollow yourself.");
        }

        if (user.getFollowing().contains(followUser.getId())) {
            user.removeFollowing(followUser.getId());
            followUser.removeFollower(user.getId());
            userRepository.save(user);
            userRepository.save(followUser);
            log.info("User {} unfollowed user {}", user.getId(), followUser.getId());
        } else {
            log.warn("User {} is not following user {}", user.getId(), followUser.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not following this user.");
        }
    }

    public void subscribeToNotification(Long movieId) {
        User loggedInUser = authService.getLoggedInUser();
        Long userId = loggedInUser.getId();
        log.info("User {} subscribing to movie {} notifications.", userId, movieId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User {} not found for subscribing.", userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
                });

        moviesClient.addUserToInterestedUserIds(movieId, userId);
        log.info("User {} subscribed to movie {}.", userId, movieId);
    }

    public void addReview(Long movieId, String reviewDescription, Double rating) {
        User user = authService.getLoggedInUser();
        Long userId = user.getId();
        log.info("User {} adding review to movie {}.", userId, movieId);

        if (userId == null || movieId == null || reviewDescription == null || rating == null) {
            log.warn("Review request missing data.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required.");
        }

        if (!moviesClient.movieExists(movieId)) {
            log.warn("Movie {} does not exist.", movieId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found.");
        }

        if (!userRepository.existsById(userId)) {
            log.warn("User {} does not exist for review.", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        ReviewRequest reviewRequest = new ReviewRequest(rating, 0L, "pending", reviewDescription, userId, movieId);
        rabbitMQProducer.sendReviewRequest(reviewRequest);
        log.info("Review request sent by user {} for movie {}.", userId, movieId);
    }
}
