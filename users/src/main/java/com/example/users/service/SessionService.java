//package com.example.users.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SessionService {
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    private static final String SESSION_PREFIX = "session:";
//
//    public void save(String token) {
//        redisTemplate.opsForValue().set(SESSION_PREFIX + token, token);
//    }
//
//    public String get(String token) {
//        return redisTemplate.opsForValue().get(SESSION_PREFIX + token);
//    }
//
//    public void delete(String token) {
//        redisTemplate.delete(SESSION_PREFIX + token);
//    }
//}