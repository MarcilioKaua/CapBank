package com.capbank.user_service.infra.controller;

import com.capbank.user_service.core.application.ports.in.DeleteUserUseCase;
import com.capbank.user_service.core.application.ports.in.GetUserUseCase;
import com.capbank.user_service.core.application.ports.in.RegisterUserUseCase;
import com.capbank.user_service.core.application.ports.in.UpdateUserUseCase;
import com.capbank.user_service.core.application.ports.in.ValidateUserUseCase;
import com.capbank.user_service.infra.dto.RegisterUserRequest;
import com.capbank.user_service.infra.dto.UpdateUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.dto.ValidateUserRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final RegisterUserUseCase registerUser;
    private final ValidateUserUseCase validateUser;
    private final GetUserUseCase getUser;
    private final UpdateUserUseCase updateUser;
    private final DeleteUserUseCase deleteUser;

    public UserController(RegisterUserUseCase registerUser, ValidateUserUseCase validateUser,
                          GetUserUseCase getUser, UpdateUserUseCase updateUser, DeleteUserUseCase deleteUser) {
        this.registerUser = registerUser;
        this.validateUser = validateUser;
        this.getUser = getUser;
        this.updateUser = updateUser;
        this.deleteUser = deleteUser;
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

    @GetMapping("/{cpf}")
    public ResponseEntity<UserResponse> get(@PathVariable String cpf) {
        LOG.info("GET /api/user/{}", Integer.toHexString(cpf == null ? 0 : cpf.hashCode()));
        return ResponseEntity.ok(getUser.getByCpf(cpf));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<UserResponse> update(@PathVariable String cpf, @Valid @RequestBody UpdateUserRequest request) {
        LOG.info("PUT /api/user/{}", Integer.toHexString(cpf == null ? 0 : cpf.hashCode()));
        return ResponseEntity.ok(updateUser.update(cpf, request));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(@PathVariable String cpf) {
        LOG.info("DELETE /api/user/{}", Integer.toHexString(cpf == null ? 0 : cpf.hashCode()));
        deleteUser.delete(cpf);
        return ResponseEntity.noContent().build();
    }
}
