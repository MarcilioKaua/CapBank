package com.capbank.bankaccount_service.infra.controller;

import com.capbank.bankaccount_service.core.application.ports.in.BankAccountUseCase;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.dto.BankAccountRequestDTO;
import com.capbank.bankaccount_service.infra.dto.BankAccountResponseDTO;
import com.capbank.bankaccount_service.infra.mapper.BankAccountRequestMapper;
import com.capbank.bankaccount_service.infra.mapper.BankAccountResponseMapper;
import com.capbank.bankaccount_service.infra.metrics.BankAccountMetrics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bankaccount")
public class BankAccountController {

    private final BankAccountUseCase bankAccountUseCase;
    private final BankAccountRequestMapper bankAccountRequestMapper;
    private final BankAccountResponseMapper bankAccountResponseMapper;
    private final BankAccountMetrics bankAccountMetrics;

    public BankAccountController(
            BankAccountUseCase bankAccountUseCase,
            BankAccountRequestMapper bankAccountRequestMapper,
            BankAccountResponseMapper bankAccountResponseMapper,
            BankAccountMetrics bankAccountMetrics
    ) {
        this.bankAccountUseCase = bankAccountUseCase;
        this.bankAccountRequestMapper = bankAccountRequestMapper;
        this.bankAccountResponseMapper = bankAccountResponseMapper;
        this.bankAccountMetrics = bankAccountMetrics;
    }

    @Operation(
            summary = "Criar nova conta bancária",
            description = "Cria uma nova conta bancária do usuário.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar uma nova conta",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BankAccountRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping
    public BankAccountResponseDTO create(@RequestBody BankAccountRequestDTO request) {
        BankAccount account = bankAccountRequestMapper.toDomain(request);
        var created = bankAccountUseCase.create(account);
        bankAccountMetrics.incrementAccountCreated();
        return bankAccountResponseMapper.toResponse(created);
    }

    @Operation(
            summary = "Listar todas as contas bancárias",
            description = "Retorna todas as contas bancárias dos usuários no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class)))
    })
    @GetMapping
    public List<BankAccountResponseDTO> findAll() {
        return bankAccountUseCase.findAll().stream().map(bankAccountResponseMapper::toResponse).toList();
    }

    @Operation(
            summary = "Buscar conta por ID",
            description = "Busca os dados da conta bancária com base no ID da conta."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso",
            content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/{id}")
    public BankAccountResponseDTO findById(
            @Parameter(description = "ID da conta bancária", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return bankAccountResponseMapper.toResponse(bankAccountUseCase.findById(id));
    }

    @Operation(
            summary = "Buscar conta pelo número",
            description = "Recupera as informações de uma conta a partir de seu número de conta."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<BankAccountResponseDTO> getByAccountNumber(
            @Parameter(description = "Número da conta bancária", example = "123456789")
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(bankAccountUseCase.findByAccountNumber(accountNumber));
    }

    @Operation(
            summary = "Consultar saldo",
            description = "Retorna o saldo atual de uma conta bancária específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo obtido com sucesso",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "Número da conta bancária", example = "123456789")
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(bankAccountUseCase.getBalance(accountNumber));
    }

    @Operation(
            summary = "Atualizar saldo da conta",
            description = "Atualiza o saldo de uma conta bancária existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<BankAccountResponseDTO> updateBalance(
            @Parameter(description = "Número da conta bancária", example = "123456789")
            @PathVariable String accountNumber,
            @Parameter(description = "Novo saldo da conta", example = "1500.00")
            @RequestParam BigDecimal newBalance
    ) {
        return ResponseEntity.ok(bankAccountUseCase.updateBalance(accountNumber, newBalance));
    }

    @Operation(
            summary = "Atualizar status da conta",
            description = "Altera o status de uma conta bancária (ex: ACTIVE, BLOCKED, CLOSED)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<BankAccountResponseDTO> updateStatus(
            @Parameter(description = "Número da conta bancária", example = "123456789")
            @PathVariable String accountNumber,
            @Parameter(description = "Novo status da conta", example = "ACTIVE")
            @RequestParam String status
    ) {
        return ResponseEntity.ok(bankAccountUseCase.updateStatus(accountNumber, status));
    }

    @Operation(
            summary = "Atualizar dados da conta",
            description = "Atualiza as informações de uma conta bancária com base no seu ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados da conta bancária",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BankAccountRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = BankAccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PutMapping("/{id}")
    public BankAccountResponseDTO update(
            @Parameter(description = "ID da conta bancária", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody BankAccountRequestDTO request) {
        BankAccount account = bankAccountRequestMapper.toDomain(request);
        var updated = bankAccountUseCase.update(id, account);
        return bankAccountResponseMapper.toResponse(updated);
    }

    @Operation(
            summary = "Excluir conta bancária",
            description = "Remove permanentemente uma conta bancária do sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID da conta bancária", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        bankAccountUseCase.delete(id);
    }
}
