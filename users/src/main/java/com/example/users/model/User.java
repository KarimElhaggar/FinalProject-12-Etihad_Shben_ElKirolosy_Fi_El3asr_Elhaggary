package com.example.users.model;

import jakarta.persistence.*;

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

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private List<User> following = new ArrayList<>();

    @ManyToMany(mappedBy = "following")
    private List<User> followers = new ArrayList<>();

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
    public List<User> getFollowing() { return following; }
    public List<User> getFollowers() { return followers; }
    public boolean isAdmin() { return admin; }
    public boolean isBanned() { return banned; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFollowing(List<User> following) { this.following = following; }
    public void setFollowers(List<User> followers) { this.followers = followers; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public void setBanned(boolean banned) { this.banned = banned; }


    // Builder inner class
    public static class Builder {
        private Long id;
        private String name;
        private String username;
        private String email;
        private String password;
        private List<User> following;
        private List<User> followers;
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

        public Builder following(List<User> following) {
            this.following = following; return this;
        }

        public Builder followers(List<User> followers) {
            this.followers = followers; return this;
        }

        public Builder admin(boolean admin) {
            this.admin = admin; return this;
        }

        public Builder banned(boolean banned) {
            this.banned = banned; return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
