package com.capbank.user_service.core.application.ports.in;

import com.capbank.user_service.infra.dto.ValidateCredentialsRequestDTO;

public interface ValidateCredentialsUseCase {
    boolean validate(ValidateCredentialsRequestDTO request);
}
