package com.capbank.auth_service.infra.dto;

public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn; // seconds

    public AuthResponseDTO(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
}
