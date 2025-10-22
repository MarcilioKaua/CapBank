package com.capbank.user_service.infra.controller;

import com.capbank.user_service.core.application.ports.in.RegisterUserUseCase;
import com.capbank.user_service.core.application.ports.in.ValidateUserUseCase;
import com.capbank.user_service.infra.dto.RegisterUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.dto.ValidateUserRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final RegisterUserUseCase registerUser;
    private final ValidateUserUseCase validateUser;

    public UserController(RegisterUserUseCase registerUser, ValidateUserUseCase validateUser) {
        this.registerUser = registerUser;
        this.validateUser = validateUser;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        LOG.info("POST /api/user/register (cpfHash={})", Integer.toHexString(request.getCpf().hashCode()));
        return ResponseEntity.ok(registerUser.register(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@Valid @RequestBody ValidateUserRequest request) {
        LOG.info("POST /api/user/validate (cpfHash={})", Integer.toHexString(request.getCpf().hashCode()));
        return ResponseEntity.ok(validateUser.validate(request));
    }
}
