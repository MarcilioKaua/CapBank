package com.capbank.auth_service.core.application.ports.in;

import com.capbank.auth_service.infra.dto.AuthRequest;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;

public interface AuthUseCase {
    AuthResponseDTO login(AuthRequest request);
}
