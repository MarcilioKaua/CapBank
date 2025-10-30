package com.capbank.user_service.infra.dto;

import com.capbank.user_service.infra.client.dto.AuthResponseDTO;

public record UserLoginResponse(UserResponse user, AuthResponseDTO token) { }
