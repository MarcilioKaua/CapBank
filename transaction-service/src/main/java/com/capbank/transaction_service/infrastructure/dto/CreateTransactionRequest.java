package com.capbank.transaction_service.infrastructure.dto;

import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @JsonProperty("source_account_id")
        String sourceAccountId,

        @JsonProperty("target_account_id")
        String targetAccountId,

        @JsonProperty("transaction_type")
        @NotNull(message = "Tipo de transação não pode ser nulo")
        TransactionType transactionType,

        @JsonProperty("amount")
        @NotNull(message = "Valor não pode ser nulo")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal amount,

        @JsonProperty("description")
        String description
) {
    public CreateTransactionRequest {
       
        switch (transactionType) {
            case DEPOSIT -> {
                if (targetAccountId == null || targetAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Conta de destino é obrigatória para depósitos");
                }
                if (sourceAccountId != null && !sourceAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Conta de origem deve ser nula para depósitos");
                }
            }
            case WITHDRAWAL -> {
                if (sourceAccountId == null || sourceAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Conta de origem é obrigatória para saques");
                }
                if (targetAccountId != null && !targetAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Conta de destino deve ser nula para saques");
                }
            }
            case TRANSFER -> {
                if (sourceAccountId == null || sourceAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Conta de origem é obrigatória para transferências");
                }
                if (targetAccountId == null || targetAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Conta de destino é obrigatória para transferências");
                }
                if (sourceAccountId.equals(targetAccountId)) {
                    throw new IllegalArgumentException("Contas de origem e destino não podem ser as mesmas");
                }
            }
        }
    }
}