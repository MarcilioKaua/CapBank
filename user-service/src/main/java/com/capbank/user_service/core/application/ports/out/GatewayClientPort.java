package com.capbank.user_service.core.application.ports.out;

import com.capbank.user_service.infra.client.dto.AuthResponseDTO;

import java.util.UUID;

public interface GatewayClientPort {
    void createForUser(UUID userId, String accountType);
    AuthResponseDTO loginForUser(String cpf, String password);
}
