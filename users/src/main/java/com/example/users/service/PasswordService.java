package com.example.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    @Autowired private BCryptPasswordEncoder encoder;

    public String hash(String password) {
        return encoder.encode(password);
    }

    public boolean matches(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }
}