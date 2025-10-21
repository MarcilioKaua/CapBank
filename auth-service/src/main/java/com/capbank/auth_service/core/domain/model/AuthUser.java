package com.capbank.auth_service.core.domain.model;

public class AuthUser {
    private String email;
    private String password;

    public AuthUser() {}

    public AuthUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
