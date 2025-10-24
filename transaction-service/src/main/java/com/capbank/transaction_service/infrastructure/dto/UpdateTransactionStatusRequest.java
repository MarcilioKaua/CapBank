package com.capbank.transaction_service.infrastructure.dto;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionStatusRequest(
        @JsonProperty("status")
        @NotNull(message = "Status cannot be null")
        TransactionStatus status,

        @JsonProperty("reason")
        String reason
) {}