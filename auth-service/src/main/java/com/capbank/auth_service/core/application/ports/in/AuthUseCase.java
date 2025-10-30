package com.capbank.auth_service.core.application.ports.in;

import com.capbank.auth_service.infra.dto.AuthRequest;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;
import com.capbank.auth_service.infra.dto.RefreshTokenRequest;

public interface AuthUseCase {
    AuthResponseDTO login(AuthRequest request);
    AuthResponseDTO refresh(RefreshTokenRequest request);
}
