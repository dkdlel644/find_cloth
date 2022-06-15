package com.example.clothfinder;

public class User {
    private String name;
    private String gender;
    private String googleToken;

    public User(String name, String gender, String token) {
        this.name = name;
        this.gender = gender;
        this.googleToken = token;
    }
}
