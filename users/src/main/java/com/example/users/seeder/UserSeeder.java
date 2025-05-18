package com.example.users.seeder;

import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepo;

    public UserSeeder(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
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

            userRepo.saveAll(users);
        }
    }
}
