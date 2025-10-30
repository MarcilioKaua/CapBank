package com.capbank.gateway_service.infra.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<Map<String, Object>> handleGateway(GatewayException ex, ServerWebExchange exchange) {
        LOG.warn("Gateway error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorPayload(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, ServerWebExchange exchange) {
        LOG.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorPayload(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", exchange));
    }

    private Map<String, Object> errorPayload(HttpStatus status, String message, ServerWebExchange exchange) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", status.value());
        map.put("error", status.getReasonPhrase());
        map.put("message", message);
        if (exchange != null && exchange.getRequest() != null) {
            var req = exchange.getRequest();
            map.put("path", req.getPath().value());
            String corr = req.getHeaders().getFirst("X-Correlation-Id");
            if (corr != null && !corr.isBlank()) {
                map.put("correlationId", corr);
            }
        }
        return map;
    }
}
