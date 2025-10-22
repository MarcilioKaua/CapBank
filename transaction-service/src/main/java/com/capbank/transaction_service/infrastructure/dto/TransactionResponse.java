package com.capbank.transaction_service.infrastructure.dto;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        @JsonProperty("id")
        String id,

        @JsonProperty("source_account_id")
        String sourceAccountId,

        @JsonProperty("target_account_id")
        String targetAccountId,

        @JsonProperty("transaction_type")
        TransactionType transactionType,

        @JsonProperty("amount")
        BigDecimal amount,

        @JsonProperty("description")
        String description,

        @JsonProperty("status")
        TransactionStatus status,

        @JsonProperty("transaction_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime transactionDate
) {}