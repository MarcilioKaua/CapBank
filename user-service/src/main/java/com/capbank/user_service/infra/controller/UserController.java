package com.capbank.user_service.infra.controller;

import com.capbank.user_service.core.application.ports.in.CreateUserUseCase;
import com.capbank.user_service.core.application.ports.in.GetUserUseCase;
import com.capbank.user_service.core.application.ports.in.ValidateCredentialsUseCase;
import com.capbank.user_service.infra.dto.UserCreateDTO;
import com.capbank.user_service.infra.dto.UserDTO;
import com.capbank.user_service.infra.dto.ValidateCredentialsRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final CreateUserUseCase createUser;
    private final GetUserUseCase getUser;
    private final ValidateCredentialsUseCase validateCredentials;

    public UserController(CreateUserUseCase createUser, GetUserUseCase getUser, ValidateCredentialsUseCase validateCredentials) {
        this.createUser = createUser;
        this.getUser = getUser;
        this.validateCredentials = validateCredentials;
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(createUser.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(getUser.getById(id));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody ValidateCredentialsRequestDTO dto) {
        return ResponseEntity.ok(validateCredentials.validate(dto));
    }
}
