package com.capbank.transaction_service.infrastructure.dto;

public record TransactionResultResponse(
        TransactionResponse transaction,
        String message,
        boolean notificationSent
) {}
