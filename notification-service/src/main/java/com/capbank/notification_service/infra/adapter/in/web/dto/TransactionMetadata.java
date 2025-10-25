package com.capbank.notification_service.infra.adapter.in.web.dto;

public record TransactionMetadata(
        String transactionId,
        String transactionType,
        String amount,
        String status,
        String description,
        String timestamp
) {}
