//package com.example.users.service;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//
//@Service
//public class TokenService {
//
//    private Key SECRET_KEY;
//
//    public TokenService() {
//        // This is where the secret key is initialized after the value is injected
//        SECRET_KEY = Keys.hmacShaKeyFor("blablablablablablablablablablablablablablablablablabla".getBytes());
//    }
//
//    public String generate(Long userId) {
//        String userIdString = Long.toString(userId);
//        return "token";
////        return Jwts.builder()
////                .setSubject(userIdString)
////                .setIssuedAt(new Date())
////                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
////                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
////                .compact();
//    }
//
//    public String decode(String token) {
//        try {
//            return "1";
////            return Jwts.parserBuilder()
////                    .setSigningKey(SECRET_KEY)  // Use your secret key for verification
////                    .build()
////                    .parseClaimsJws(token)
////                    .getBody()
////                    .getSubject(); // The subject is the user ID
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid token", e);
//        }
//    }
//}