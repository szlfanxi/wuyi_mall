package com.wuyimall.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}