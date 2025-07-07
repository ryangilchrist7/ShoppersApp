package com.shoppersapp.dto;

public class LoginRequestDTO {
    private String identifier;
    private String password;

    public LoginRequestDTO() {

    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}