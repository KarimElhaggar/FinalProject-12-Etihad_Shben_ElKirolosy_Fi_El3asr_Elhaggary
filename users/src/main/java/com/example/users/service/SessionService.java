package com.example.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String SESSION_PREFIX = "session:";

    public void save(String token, Long userId) {
        redisTemplate.opsForValue().set("session:" + token, userId.toString(), Duration.ofDays(1));
    }

    public String get(String token) {
        return redisTemplate.opsForValue().get(SESSION_PREFIX + token);
    }

    public void delete(String token) {
        System.out.println("Deleting session for token: " + SESSION_PREFIX + token);
        redisTemplate.delete(SESSION_PREFIX + token);
    }
}