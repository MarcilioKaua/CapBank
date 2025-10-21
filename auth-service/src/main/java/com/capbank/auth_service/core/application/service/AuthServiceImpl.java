package com.capbank.auth_service.core.application.service;

import com.capbank.auth_service.core.application.ports.in.AuthUseCase;
import com.capbank.auth_service.core.application.ports.out.UserClientPort;
import com.capbank.auth_service.infra.config.JwtService;
import com.capbank.auth_service.infra.dto.AuthRequestDTO;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;
import com.capbank.auth_service.infra.exception.AuthException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthUseCase {

    private final UserClientPort userClientPort;
    private final JwtService jwtService;

    public AuthServiceImpl(UserClientPort userClientPort, JwtService jwtService) {
        this.userClientPort = userClientPort;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        boolean valid = userClientPort.validateCredentials(request.getEmail(), request.getPassword());

        if (!valid) {
            throw new AuthException("Credenciais inválidas.");
        }

        String token = jwtService.generateToken(request.getEmail());
        return new AuthResponseDTO(token);
    }
}
