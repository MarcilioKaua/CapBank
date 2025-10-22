package com.capbank.transaction_service.infrastructure.adapter.in.web;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionUseCase;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.FindTransactionQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.TransactionPage;
import com.capbank.transaction_service.core.application.port.in.UpdateTransactionStatusUseCase;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import com.capbank.transaction_service.infrastructure.dto.*;
import com.capbank.transaction_service.infrastructure.mapper.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "APIs para gerenciamento de transações bancárias")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final CreateTransactionUseCase createTransactionUseCase;
    private final FindTransactionUseCase findTransactionUseCase;
    private final UpdateTransactionStatusUseCase updateTransactionStatusUseCase;
    private final TransactionMapper mapper;

    public TransactionController(
            CreateTransactionUseCase createTransactionUseCase,
            FindTransactionUseCase findTransactionUseCase,
            UpdateTransactionStatusUseCase updateTransactionStatusUseCase,
            TransactionMapper mapper) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.findTransactionUseCase = findTransactionUseCase;
        this.updateTransactionStatusUseCase = updateTransactionStatusUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Criar transação",
               description = "Processa uma nova transação bancária e gera histórico automaticamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transação criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResultResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno no processamento")
    })
    @PostMapping
    public ResponseEntity<TransactionResultResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        logger.info("Creating transaction: type={}, amount={}", request.transactionType(), request.amount());

        try {
            CreateTransactionUseCase.TransactionResult result = createTransactionUseCase.processTransaction(
                    mapper.toCommand(request));

            TransactionResultResponse response = new TransactionResultResponse(
                    mapper.toResponse(result.transaction()),
                    result.message(),
                    result.notificationSent()
            );

            logger.info("Transaction created successfully with ID: {}", result.transaction().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.error("Business rule violation creating transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating transaction: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Buscar transação por ID",
               description = "Busca uma transação específica por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transação encontrada",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(
            @Parameter(description = "ID da transação")
            @PathVariable String id) {

        logger.info("Finding transaction by ID: {}", id);

        return findTransactionUseCase.findById(new TransactionId(id))
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar transações de uma conta",
               description = "Lista transações de uma conta específica com filtros e paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transações encontradas",
                    content = @Content(schema = @Schema(implementation = TransactionPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<TransactionPageResponse> findByAccount(
            @Parameter(description = "ID da conta")
            @PathVariable String accountId,

            @Parameter(description = "Tipo de transação para filtrar")
            @RequestParam(required = false) TransactionType transactionType,

            @Parameter(description = "Status da transação para filtrar")
            @RequestParam(required = false) TransactionStatus transactionStatus,

            @Parameter(description = "Data de início do período (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "Data de fim do período (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamanho da página (máximo 100)")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "transactionDate") String sortBy,

            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        logger.info("Finding transactions for account: {} with filters - type: {}, status: {}, period: {} to {}",
                   accountId, transactionType, transactionStatus, startDate, endDate);

        FindTransactionQuery query = new FindTransactionQuery(
                new AccountId(accountId),
                transactionType,
                transactionStatus,
                startDate,
                endDate,
                page,
                size,
                sortBy,
                sortDirection
        );

        TransactionPage result = findTransactionUseCase.findByAccount(query);
        TransactionPageResponse response = mapper.toPageResponse(result);

        logger.info("Found {} transactions for account: {}", result.content().size(), accountId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar status da transação",
               description = "Atualiza o status de uma transação existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Status inválido")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<TransactionResponse> updateStatus(
            @Parameter(description = "ID da transação")
            @PathVariable String id,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {

        logger.info("Updating transaction status: id={}, newStatus={}", id, request.status());

        try {
            Transaction updatedTransaction = updateTransactionStatusUseCase.updateStatus(
                    mapper.toUpdateCommand(id, request));

            TransactionResponse response = mapper.toResponse(updatedTransaction);

            logger.info("Transaction status updated successfully: {}", id);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error updating transaction status: {}", e.getMessage());
            throw e;
        }
    }


    public record TransactionResultResponse(
            TransactionResponse transaction,
            String message,
            boolean notificationSent
    ) {}
}