package com.capbank.transaction_service.infrastructure.dto;

import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransactionHistoryRequest(
        @JsonProperty("account_id")
        @NotBlank(message = "Account ID cannot be blank")
        String accountId,

        @JsonProperty("transaction_id")
        @NotBlank(message = "Transaction ID cannot be blank")
        String transactionId,

        @JsonProperty("balance_before")
        @NotNull(message = "Balance before cannot be null")
        @Positive(message = "Balance before must be positive")
        BigDecimal balanceBefore,

        @JsonProperty("transaction_amount")
        @NotNull(message = "Transaction amount cannot be null")
        @Positive(message = "Transaction amount must be positive")
        BigDecimal transactionAmount,

        @JsonProperty("transaction_type")
        @NotNull(message = "Transaction type cannot be null")
        TransactionType transactionType,

        @JsonProperty("description")
        String description
) {}