package com.capbank.transaction_service.infrastructure.adapter.in.web;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionHistoryUseCase;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.FindTransactionHistoryQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.TransactionHistoryPage;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.infrastructure.dto.CreateTransactionHistoryRequest;
import com.capbank.transaction_service.infrastructure.dto.TransactionHistoryPageResponse;
import com.capbank.transaction_service.infrastructure.dto.TransactionHistoryResponse;
import com.capbank.transaction_service.infrastructure.mapper.TransactionHistoryMapper;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction-history")
@Tag(name = "Transaction History", description = "APIs para gerenciamento de histórico de transações")
public class TransactionHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryController.class);

    private final CreateTransactionHistoryUseCase createTransactionHistoryUseCase;
    private final FindTransactionHistoryUseCase findTransactionHistoryUseCase;
    private final TransactionHistoryMapper mapper;

    public TransactionHistoryController(
            CreateTransactionHistoryUseCase createTransactionHistoryUseCase,
            FindTransactionHistoryUseCase findTransactionHistoryUseCase,
            TransactionHistoryMapper mapper) {
        this.createTransactionHistoryUseCase = createTransactionHistoryUseCase;
        this.findTransactionHistoryUseCase = findTransactionHistoryUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Criar histórico de transação",
               description = "Cria um novo registro no histórico de transações")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Histórico criado com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionHistoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Histórico já existe para esta transação")
    })
    @PostMapping
    public ResponseEntity<TransactionHistoryResponse> createTransactionHistory(
            @Valid @RequestBody CreateTransactionHistoryRequest request) {

        logger.info("Creating transaction history for transaction: {}", request.transactionId());

        try {
            TransactionHistory created = createTransactionHistoryUseCase.createTransactionHistory(
                    mapper.toCommand(request));

            TransactionHistoryResponse response = mapper.toResponse(created);

            logger.info("Transaction history created successfully with ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error creating transaction history: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Buscar histórico por ID",
               description = "Busca um registro específico do histórico por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico encontrado",
                    content = @Content(schema = @Schema(implementation = TransactionHistoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Histórico não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionHistoryResponse> findById(
            @Parameter(description = "ID do histórico de transação")
            @PathVariable UUID id) {

        logger.info("Finding transaction history by ID: {}", id);

        return findTransactionHistoryUseCase.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar histórico de uma conta",
               description = "Busca o histórico de transações de uma conta com filtros e paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico encontrado",
                    content = @Content(schema = @Schema(implementation = TransactionHistoryPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<TransactionHistoryPageResponse> findByAccountId(
            @Parameter(description = "ID da conta")
            @PathVariable String accountId,

            @Parameter(description = "Tipo de transação para filtrar")
            @RequestParam(required = false) TransactionType transactionType,

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
            @RequestParam(defaultValue = "recordDate") String sortBy,

            @Parameter(description = "Direção da ordenação (ASC ou DESC)")
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        logger.info("Finding transaction history for account: {} with filters - type: {}, period: {} to {}",
                   accountId, transactionType, startDate, endDate);

        FindTransactionHistoryQuery query = new FindTransactionHistoryQuery(
                new AccountId(accountId),
                transactionType,
                startDate,
                endDate,
                page,
                size,
                sortBy,
                sortDirection
        );

        TransactionHistoryPage result = findTransactionHistoryUseCase.findByAccountId(query);
        TransactionHistoryPageResponse response = mapper.toPageResponse(result);

        logger.info("Found {} transaction histories for account: {}",
                   result.content().size(), accountId);

        return ResponseEntity.ok(response);
    }
}