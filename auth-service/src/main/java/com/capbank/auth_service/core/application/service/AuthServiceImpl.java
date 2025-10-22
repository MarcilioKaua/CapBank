package com.capbank.auth_service.core.application.service;

import com.capbank.auth_service.core.application.ports.in.AuthUseCase;
import com.capbank.auth_service.infra.config.JwtService;
import com.capbank.auth_service.infra.dto.AuthRequest;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;
import com.capbank.auth_service.infra.exception.AuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtService jwtService;

    public AuthServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDTO login(AuthRequest request) {
        final String normalizedCpf = request.getCpf().replaceAll("\\D+", "");
        LOG.info("Generating JWT for cpf hash={}", Integer.toHexString(normalizedCpf.hashCode()));

        try {
            String token = jwtService.generateToken(normalizedCpf);
            long expiresIn = jwtService.getExpirationSeconds();
            LOG.info("Token generated successfully for cpf hash={}", Integer.toHexString(normalizedCpf.hashCode()));
            return new AuthResponseDTO(token, expiresIn);
        } catch (Exception e) {
            LOG.error("Token generation failed: {}", e.getMessage());
            throw new AuthException("Error while generating authentication token.");
        }
    }
}
