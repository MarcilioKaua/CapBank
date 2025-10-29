package com.capbank.auth_service.core.application.service;

import com.capbank.auth_service.core.application.ports.in.AuthUseCase;
import com.capbank.auth_service.infra.config.JwtService;
import com.capbank.auth_service.infra.dto.AuthRequest;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;
import com.capbank.auth_service.infra.dto.RefreshTokenRequest;
import com.capbank.auth_service.infra.exception.AuthException;
import com.capbank.auth_service.infra.metrics.AuthMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtService jwtService;
    private final AuthMetrics authMetrics;

    public AuthServiceImpl(JwtService jwtService, AuthMetrics authMetrics) {
        this.jwtService = jwtService;
        this.authMetrics = authMetrics;
    }

    @Override
    public AuthResponseDTO login(AuthRequest request) {
        final String normalizedCpf = request.getCpf().replaceAll("\\D+", "");
        LOG.info("Generating tokens for cpf hash={}", Integer.toHexString(normalizedCpf.hashCode()));

        try {
            String access = jwtService.generateAccessToken(normalizedCpf);
            String refresh = jwtService.generateRefreshToken(normalizedCpf);
            long accessExpiresIn = jwtService.getAccessExpirationSeconds();
            long refreshExpiresIn = jwtService.getRefreshExpirationSeconds();
            authMetrics.incrementLoginSuccess();
            LOG.info("Tokens generated successfully for cpf hash={}", Integer.toHexString(normalizedCpf.hashCode()));
            return new AuthResponseDTO(access, accessExpiresIn, refresh, refreshExpiresIn);
        } catch (Exception e) {
            authMetrics.incrementLoginFailure();
            LOG.error("Token generation failed: {}", e.getMessage());
            throw new AuthException("Error while generating authentication tokens.");
        }
    }

    @Override
    public AuthResponseDTO refresh(RefreshTokenRequest request) {
        try {
            String cpf = jwtService.validateAndGetSubject(request.getRefreshToken(), "refresh");
            String newAccess = jwtService.generateAccessToken(cpf);
            String newRefresh = jwtService.generateRefreshToken(cpf);
            authMetrics.incrementTokenRefreshed();
            return new AuthResponseDTO(newAccess, jwtService.getAccessExpirationSeconds(), newRefresh, jwtService.getRefreshExpirationSeconds());
        } catch (Exception e) {
            LOG.error("Refresh token invalid: {}", e.getMessage());
            throw new AuthException("Invalid refresh token.");
        }
    }
}
