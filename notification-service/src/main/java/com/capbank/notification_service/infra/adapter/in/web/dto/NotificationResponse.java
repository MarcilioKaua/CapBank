package com.capbank.notification_service.infra.adapter.in.web.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        String id,
        String status,
        String message,
        LocalDateTime timestamp
) {
    public static NotificationResponse success(String id, String message) {
        return new NotificationResponse(id, "SUCCESS", message, LocalDateTime.now());
    }

    public static NotificationResponse error(String message) {
        return new NotificationResponse(null, "ERROR", message, LocalDateTime.now());
    }
}
