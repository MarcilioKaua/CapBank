package com.capbank.notification_service.infra.exception;

public class NotificationException extends RuntimeException {
    public NotificationException(String message) {
        super(message);
    }
}
