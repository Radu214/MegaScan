package com.example.megascan.model;

public class User {
    private String email;
    private String password;

    // Constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and optional setters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
