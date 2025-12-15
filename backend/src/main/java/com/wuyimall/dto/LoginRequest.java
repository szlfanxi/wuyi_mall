package com.wuyimall.dto;



public class LoginRequest {
    private String username;
    private String password;

    // getter 方法
    public String getUsername() {
        return username;
    }

    // setter 方法
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}