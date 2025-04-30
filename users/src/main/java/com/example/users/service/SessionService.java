package com.example.users.service;

import com.example.users.model.User;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static SessionService instance;

    private User currentUser;

    private SessionService() {}

    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void clearSession() {
        this.currentUser = null;
    }
}

//
//public class SessionManager {
//
//    // Step 1: Singleton instance
//    private static SessionManager instance;
//    private RedisTemplate<String, Object> redisTemplate;
//
//    // Step 2: Private constructor to prevent instantiation
//    private SessionManager(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    // Step 3: Get the Singleton instance (Lazy-loaded)
//    public static synchronized SessionManager getInstance(RedisTemplate<String, Object> redisTemplate) {
//        if (instance == null) {
//            instance = new SessionManager(redisTemplate);
//        }
//        return instance;
//    }
//
//    // Step 4: Store session in Redis
//    public void storeSession(String sessionId, User user) {
//        redisTemplate.opsForValue().set(sessionId, user);
//    }
//
//    // Step 5: Retrieve session from Redis
//    public User getSession(String sessionId) {
//        return (User) redisTemplate.opsForValue().get(sessionId);
//    }
//
//    // Step 6: Remove session from Redis
//    public void removeSession(String sessionId) {
//        redisTemplate.delete(sessionId);
//    }
//}


