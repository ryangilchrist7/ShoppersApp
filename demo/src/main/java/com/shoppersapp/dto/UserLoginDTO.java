package com.shoppersapp.dto;

public class UserLoginDTO {
    private Integer userId;
    private String message;

    public UserLoginDTO(Integer userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}