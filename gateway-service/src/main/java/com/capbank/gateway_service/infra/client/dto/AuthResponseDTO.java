package com.capbank.gateway_service.infra.client.dto;

public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn; // seconds
    private long refreshExpiresIn; // seconds

    public AuthResponseDTO(String accessToken, long expiresIn, String refreshToken, long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public long getRefreshExpiresIn() { return refreshExpiresIn; }
}
