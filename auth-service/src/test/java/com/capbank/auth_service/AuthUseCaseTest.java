package com.capbank.auth_service;

import com.capbank.auth_service.core.application.service.AuthServiceImpl;
import com.capbank.auth_service.infra.config.JwtService;
import com.capbank.auth_service.infra.dto.AuthRequest;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;
import com.capbank.auth_service.infra.dto.RefreshTokenRequest;
import com.capbank.auth_service.infra.exception.AuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthUseCaseTest {

    @Mock private JwtService jwtService;

    @InjectMocks private AuthServiceImpl authService;

    @Test
    @DisplayName("Deve gerar tokens com sucesso no login")
    void shouldGenerateTokensSuccessfully() {
        AuthRequest request = new AuthRequest();
        request.setCpf("123.456.789-10");

        when(jwtService.generateAccessToken(anyString())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh_token");
        when(jwtService.getAccessExpirationSeconds()).thenReturn(3600L);
        when(jwtService.getRefreshExpirationSeconds()).thenReturn(86400L);

        AuthResponseDTO response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getRefreshExpiresIn()).isEqualTo(86400L);

        verify(jwtService).generateAccessToken("12345678910");
        verify(jwtService).generateRefreshToken("12345678910");
    }

    @Test
    @DisplayName("Deve lançar exceção quando falhar a geração dos tokens")
    void shouldThrowExceptionWhenTokenGenerationFails() {
        AuthRequest request = new AuthRequest();
        request.setCpf("11122233344");

        when(jwtService.generateAccessToken(anyString())).thenThrow(new RuntimeException("Error generating"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessage("Error while generating authentication tokens.");
    }

    @Test
    @DisplayName("Deve gerar novos tokens ao atualizar o refresh token")
    void shouldRefreshTokensSuccessfully() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("valid_refresh_token");

        when(jwtService.validateAndGetSubject("valid_refresh_token", "refresh"))
                .thenReturn("12345678910");
        when(jwtService.generateAccessToken("12345678910")).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken("12345678910")).thenReturn("new_refresh_token");
        when(jwtService.getAccessExpirationSeconds()).thenReturn(3600L);
        when(jwtService.getRefreshExpirationSeconds()).thenReturn(86400L);

        AuthResponseDTO response = authService.refresh(refreshRequest);

        assertThat(response.getAccessToken()).isEqualTo("new_access_token");
        assertThat(response.getRefreshToken()).isEqualTo("new_refresh_token");
    }

    @Test
    @DisplayName("Deve lançar exceção quando refresh token for inválido")
    void shouldThrowExceptionWhenRefreshTokenInvalid() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("invalid_token");

        when(jwtService.validateAndGetSubject("invalid_token", "refresh"))
                .thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() -> authService.refresh(refreshRequest))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid refresh token.");
    }
}
