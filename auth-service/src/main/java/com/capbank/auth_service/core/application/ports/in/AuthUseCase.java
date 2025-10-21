package com.capbank.auth_service.core.application.ports.in;

import com.capbank.auth_service.infra.dto.AuthRequestDTO;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;

public interface AuthUseCase {
    AuthResponseDTO login(AuthRequestDTO request);
}