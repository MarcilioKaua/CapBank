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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria um novo usuário no sistema a partir dos dados fornecidos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuário a ser registrado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterUserRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        LOG.info("POST /api/user/register (cpfHash={})", Integer.toHexString(request.getCpf().hashCode()));
        return ResponseEntity.ok(registerUser.register(request));
    }

    @Operation(
            summary = "Validar credenciais do usuário",
            description = "Valida CPF e senha do usuário para autenticação.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CPF e senha do usuário",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ValidateUserRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas")
    })
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@Valid @RequestBody ValidateUserRequest request) {
        LOG.info("POST /api/user/validate (cpfHash={})", Integer.toHexString(request.getCpf().hashCode()));
        return ResponseEntity.ok(validateUser.validate(request));
    }

    @Operation(
            summary = "Buscar usuário por CPF",
            description = "Recupera os dados de um usuário existente a partir do CPF."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<UserResponse> get(
            @Parameter(description = "CPF do usuário", example = "12345678900")
            @PathVariable String cpf) {
        LOG.info("GET /api/user/{}", Integer.toHexString(cpf == null ? 0 : cpf.hashCode()));
        return ResponseEntity.ok(getUser.getByCpf(cpf));
    }

    @Operation(
            summary = "Atualizar dados do usuário",
            description = "Atualiza as informações cadastrais de um usuário com base no CPF."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{cpf}")
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "CPF do usuário a ser atualizado", example = "12345678900")
            @PathVariable String cpf,
            @Valid @RequestBody UpdateUserRequest request) {
        LOG.info("PUT /api/user/{}", Integer.toHexString(cpf == null ? 0 : cpf.hashCode()));
        return ResponseEntity.ok(updateUser.update(cpf, request));
    }

    @Operation(
            summary = "Excluir usuário",
            description = "Remove permanentemente um usuário do sistema com base no CPF."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "CPF do usuário a ser removido", example = "12345678900")
            @PathVariable String cpf) {
        LOG.info("DELETE /api/user/{}", Integer.toHexString(cpf == null ? 0 : cpf.hashCode()));
        deleteUser.delete(cpf);
        return ResponseEntity.noContent().build();
    }
}
