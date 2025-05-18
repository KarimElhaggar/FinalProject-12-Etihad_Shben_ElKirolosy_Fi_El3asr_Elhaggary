package com.example.users.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    private Key SECRET_KEY;

    public TokenService(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generate(Long userId) {
        String userIdString = Long.toString(userId);
        return Jwts.builder()
                .setSubject(userIdString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String decode(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
    }
}