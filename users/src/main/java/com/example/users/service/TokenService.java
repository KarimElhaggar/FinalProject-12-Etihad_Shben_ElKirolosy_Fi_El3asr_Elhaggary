package com.example.users.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    static private String secret;

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(
            secret.getBytes()
    );

    public String generate(Long userId) {
        String userIdString = Long.toString(userId);
        return Jwts.builder()
                .setSubject(userIdString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String decode(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  // Use your secret key for verification
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // The subject is the user ID
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}