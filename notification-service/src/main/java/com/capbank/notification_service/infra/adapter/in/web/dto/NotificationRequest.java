package com.capbank.notification_service.infra.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotBlank(message = "userId is required")
        String userId,

        String recipientEmail,

        @NotBlank(message = "accountId is required")
        String accountId,

        @NotBlank(message = "type is required")
        String type,

        @NotBlank(message = "channel is required")
        String channel,

        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "message is required")
        String message,

        @NotNull(message = "transactionData is required")
        TransactionMetadata transactionData
) {}
