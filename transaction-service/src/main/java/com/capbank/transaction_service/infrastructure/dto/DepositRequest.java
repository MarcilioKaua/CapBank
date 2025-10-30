package com.capbank.transaction_service.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequest(
        @JsonProperty("target_account_id")
        @NotBlank(message = "Target account ID cannot be blank")
        String targetAccountId,

        @JsonProperty("amount")
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @JsonProperty("description")
        String description
) {
    
}
