package com.capbank.gateway_service.core.application.ports.out;

public interface AuthClientPort {
    String generateToken(String cpf, String password);
    String refreshAccessToken(String refreshToken);
}
