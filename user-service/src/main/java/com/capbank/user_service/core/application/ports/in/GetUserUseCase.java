package com.capbank.user_service.core.application.ports.in;

import com.capbank.user_service.infra.dto.UserResponse;

public interface GetUserUseCase {
    UserResponse getByCpf(String cpf);
}
