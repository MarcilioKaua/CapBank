package com.capbank.gateway_service.infra.controller;

import com.capbank.gateway_service.infra.client.HttpClientBaseAdapter;
import com.capbank.gateway_service.infra.client.dto.AuthResponseDTO;
import com.capbank.gateway_service.infra.dto.BankAccountCreateRequest;
import com.capbank.gateway_service.infra.dto.UserLoggedEvent;
import com.capbank.gateway_service.infra.dto.UserRegisteredEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/gateway")
public class GatewayOrchestratorController {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayOrchestratorController.class);

    private final HttpClientBaseAdapter httpClient;
    private final String accountBaseUrl;
    private final String authBaseUrl;
    private final String defaultAgency;

    public GatewayOrchestratorController(
            HttpClientBaseAdapter httpClient,
            @Value("${services.account.base-url:http://localhost:8084}") String accountBaseUrl,
            @Value("${services.auth.base-url:http://localhost:8082}") String authBaseUrl,
            @Value("${bank.account.default-agency:0001}") String defaultAgency) {
        this.httpClient = httpClient;
        this.accountBaseUrl = accountBaseUrl;
        this.authBaseUrl = authBaseUrl;
        this.defaultAgency = defaultAgency;
    }

    @Operation(
            summary = "Cria automaticamente uma conta bancária para um novo usuário registrado",
            description = """
                    Esse endpoint é acionado quando o evento **USER_REGISTERED** é recebido.
                    Ele gera um número de conta aleatório e envia uma requisição ao serviço de contas (`account-service`)
                    para criar uma nova conta bancária vinculada ao usuário recém-cadastrado.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Conta criada com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida (faltando userId ou accountType)",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erro interno durante a orquestração",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @PostMapping("/user-registered")
    public ResponseEntity<Void> onUserRegistered(@RequestBody UserRegisteredEvent event) {
        if (event.getUserId() == null || event.getAccountType() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LOG.info("Gateway received USER_REGISTERED for userId={}, accountType={}", event.getUserId(), event.getAccountType());

        String generatedAccountNumber = generateAccountNumber();
        String agencyToUse = defaultAgency;

        BankAccountCreateRequest request = new BankAccountCreateRequest();
        request.setUserId(event.getUserId());
        request.setAccountType(event.getAccountType());
        request.setBalance(BigDecimal.ZERO);
        request.setAccountNumber(generatedAccountNumber);
        request.setAgency(agencyToUse);

        String url = accountBaseUrl + "/api/bankaccount";
        httpClient.post(url, request, Void.class);

        LOG.info("Bank account created for userId={} with agency={} and accountNumber=****{}",
                event.getUserId(), agencyToUse, generatedAccountNumber.substring(Math.max(0, generatedAccountNumber.length() - 4)));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Gera token JWT via auth-service",
            description = "Chamado quando o usuário faz login. O gateway repassa as credenciais ao auth-service e devolve o token JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token gerado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    @PostMapping("/user-logged")
    public ResponseEntity<AuthResponseDTO> onUserLogged(@RequestBody UserLoggedEvent event) {
        LOG.info("Gateway received USER_LOGGED for cpf={}", event.getCpf());

        String url = authBaseUrl + "/api/auth/login";

        try {
            AuthResponseDTO tokenResponse = httpClient.post(url, event, AuthResponseDTO.class);
            LOG.info("Token generated for cpf={}", event.getCpf());
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception ex) {
            LOG.error("Failed to generate token for cpf={}: {}", event.getCpf(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String generateAccountNumber() {
        int number = ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
        return String.valueOf(number);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        LOG.error("Gateway orchestration failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gateway orchestration failed");
    }
}
