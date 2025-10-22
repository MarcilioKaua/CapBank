package com.capbank.transaction_service.infrastructure.dto;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryResponse(
        @JsonProperty("id")
        String id,

        @JsonProperty("account_id")
        String accountId,

        @JsonProperty("transaction_id")
        String transactionId,

        @JsonProperty("balance_before")
        BigDecimal balanceBefore,

        @JsonProperty("balance_after")
        BigDecimal balanceAfter,

        @JsonProperty("transaction_amount")
        BigDecimal transactionAmount,

        @JsonProperty("transaction_type")
        TransactionType transactionType,

        @JsonProperty("status")
        TransactionStatus status,

        @JsonProperty("description")
        String description,

        @JsonProperty("record_date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime recordDate
) {}