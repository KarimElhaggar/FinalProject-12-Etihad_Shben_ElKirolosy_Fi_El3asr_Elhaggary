package com.example.users.repository;

import com.example.users.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    void save(User user);
}
