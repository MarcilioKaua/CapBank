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
        @NotNull(message = "Transaction type cannot be null")
        TransactionType transactionType,

        @JsonProperty("amount")
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @JsonProperty("description")
        String description
) {
    public CreateTransactionRequest {
       
        switch (transactionType) {
            case DEPOSIT -> {
                if (targetAccountId == null || targetAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Target account is required for deposits");
                }
                if (sourceAccountId != null && !sourceAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Source account should be null for deposits");
                }
            }
            case WITHDRAWAL -> {
                if (sourceAccountId == null || sourceAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Source account is required for withdrawals");
                }
                if (targetAccountId != null && !targetAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Target account should be null for withdrawals");
                }
            }
            case TRANSFER -> {
                if (sourceAccountId == null || sourceAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Source account is required for transfers");
                }
                if (targetAccountId == null || targetAccountId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Target account is required for transfers");
                }
                if (sourceAccountId.equals(targetAccountId)) {
                    throw new IllegalArgumentException("Source and target accounts cannot be the same");
                }
            }
        }
    }
}