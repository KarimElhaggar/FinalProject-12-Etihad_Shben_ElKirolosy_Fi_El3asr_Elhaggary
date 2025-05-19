package com.example.users.config;

import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UsersSeederConfig {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void seedOnStartup(){
        if (userRepository.count() == 0) {
            List<User> users = List.of(
                    new User.Builder()
                            .id(1L)
                            .name("Alice")
                            .username("alice01")
                            .email("alice@example.com")
                            .password("pass123")
                            .admin(false)
                            .banned(false)
                            .build(),
                    new User.Builder()
                            .id(2L)
                            .name("Bob")
                            .username("bobby")
                            .email("bob@example.com")
                            .password("pass456")
                            .admin(false)
                            .banned(false)
                            .build(),
                    new User.Builder()
                            .id(3L)
                            .name("Charlie")
                            .username("charlie")
                            .email("charlie@example.com")
                            .password("pass789")
                            .admin(false)
                            .banned(false)
                            .build()
            );

            userRepository.saveAll(users);
        }
    }
}
