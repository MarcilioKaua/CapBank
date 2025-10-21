package com.capbank.user_service.core.application.ports.in;

import com.capbank.user_service.infra.dto.UserCreateDTO;
import com.capbank.user_service.infra.dto.UserDTO;

public interface CreateUserUseCase {
    UserDTO create(UserCreateDTO dto);
}
