package com.capbank.user_service.core.application.ports.in;

import com.capbank.user_service.infra.dto.UserDTO;

import java.util.UUID;

public interface GetUserUseCase {
    UserDTO getById(UUID id);
}
