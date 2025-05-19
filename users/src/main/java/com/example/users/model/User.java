package com.example.users.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String email;
    private String password;
    private boolean admin;
    private boolean banned;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_following_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "following_id")
    private List<Long> following = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_follower_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "follower_id")
    private List<Long> followers = new ArrayList<>();

    public User() {}

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.following = builder.following;
        this.followers = builder.followers;
        this.admin = builder.admin;
        this.banned = builder.banned;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Long> getFollowing() { return new ArrayList<>(this.following); }
    public List<Long> getFollowers() { return new ArrayList<>(this.followers); }
    public boolean isAdmin() { return admin; }
    public boolean isBanned() { return banned; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFollowing(List<Long> following) { this.following = following; }
    public void setFollowers(List<Long> followers) { this.followers = followers; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public void setBanned(boolean banned) { this.banned = banned; }

    // Add a follower
    public void addFollower(Long followerId) {
        if (!followers.contains(followerId)) {
            followers.add(followerId);
        }
    }

    // Remove a follower
    public void addFollowing(Long followingId) {
        if (!following.contains(followingId)) {
            following.add(followingId);
        }
    }

    // Remove a follower
    public void removeFollower(Long followerId) {
        followers.remove(followerId);
    }

    public void removeFollowing(Long followingId) {
        following.remove(followingId);
    }


    // Builder inner class
    public static class Builder {
        private Long id;
        private String name;
        private String username;
        private String email;
        private String password;
        private List<Long> following;
        private List<Long> followers;
        private boolean admin;
        private boolean banned;

        public Builder id(Long id) {
            this.id = id; return this;
        }

        public Builder name(String name) {
            this.name = name; return this;
        }

        public Builder username(String username) {
            this.username = username; return this;
        }

        public Builder email(String email) {
            this.email = email; return this;
        }

        public Builder password(String password) {
            this.password = password; return this;
        }

        public Builder following(List<Long> following) {
            this.following = following; return this;
        }

        public Builder followers(List<Long> followers) {
            this.followers = followers; return this;
        }

        public Builder admin(boolean admin) {
            this.admin = admin; return this;
        }

        public Builder banned(boolean banned) {
            this.banned = banned; return this;
        }

        public User build() {
            if (this.username == null || this.username.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null or empty");
            }
            if (this.email == null || this.email.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null or empty");
            }
            if (this.password == null || this.password.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null or empty");
            }
            return new User(this);
        }
    }
}
