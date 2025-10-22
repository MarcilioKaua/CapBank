package com.capbank.user_service.core.application.ports.in;

import com.capbank.user_service.infra.dto.ValidateUserRequest;

public interface ValidateUserUseCase {
    boolean validate(ValidateUserRequest request);
}
