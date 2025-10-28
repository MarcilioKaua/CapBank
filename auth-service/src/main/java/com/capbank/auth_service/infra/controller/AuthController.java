package com.capbank.auth_service.infra.controller;

import com.capbank.auth_service.core.application.ports.in.AuthUseCase;
import com.capbank.auth_service.infra.dto.AuthRequest;
import com.capbank.auth_service.infra.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @Operation(
            summary = "Realizar login",
            description = "Autentica um usuário com base em seu CPF e senha, retornando um token JWT",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais do usuário para autenticação",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou requisição malformada"),
            @ApiResponse(responseCode = "401", description = "Autenticação falhou — CPF ou senha incorretos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequest request) {
        LOG.info("POST /api/auth/login (cpfHash={})", Integer.toHexString(request.getCpf().hashCode()));
        return ResponseEntity.ok(authUseCase.login(request));
    }
}
