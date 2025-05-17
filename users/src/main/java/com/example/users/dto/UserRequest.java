package com.example.users.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRequest {

    private String name;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private boolean admin;
    private boolean banned;

    public UserRequest() {}

    public UserRequest(String name, String username, String email, String password, boolean admin, boolean banned) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.banned = banned;
    }

    public UserRequest(Long id, String email) {
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }
}
