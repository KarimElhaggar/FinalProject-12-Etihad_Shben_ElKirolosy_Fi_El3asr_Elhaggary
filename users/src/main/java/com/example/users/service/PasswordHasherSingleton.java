package com.example.users.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasherSingleton {

    private static volatile PasswordHasherSingleton instance;
    private final BCryptPasswordEncoder encoder;

    private PasswordHasherSingleton() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public static PasswordHasherSingleton getInstance() {
        if (instance == null) {
            synchronized (PasswordHasherSingleton.class) {
                if (instance == null) {
                    instance = new PasswordHasherSingleton();
                }
            }
        }
        return instance;
    }

    public String hash(String password) {
        return encoder.encode(password);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}