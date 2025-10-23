package com.capbank.user_service.core.application.ports.in;

import com.capbank.user_service.infra.dto.UpdateUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;

public interface UpdateUserUseCase {
    UserResponse update(String cpf, UpdateUserRequest request);
}
