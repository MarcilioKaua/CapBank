package com.capbank.transaction_service.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;


public record ErrorResponse(
        @JsonProperty("timestamp")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @JsonProperty("status")
        int status,

        @JsonProperty("error")
        String error,

        @JsonProperty("message")
        String message,

        @JsonProperty("path")
        String path,

        @JsonProperty("validation_errors")
        List<ValidationError> validationErrors
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    public ErrorResponse(int status, String error, String message, String path, List<ValidationError> validationErrors) {
        this(LocalDateTime.now(), status, error, message, path, validationErrors);
    }

    public record ValidationError(String field, String message) {}
}