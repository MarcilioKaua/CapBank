package com.capbank.gateway_service.infra.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger LOG = LoggerFactory.getLogger(FallbackController.class);

    @RequestMapping("/service-unavailable")
    public ResponseEntity<Map<String, Object>> serviceUnavailable(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        String method = Optional.ofNullable(exchange.getRequest().getMethod())
                .map(m -> m.name())
                .orElse("UNKNOWN");
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "unknown";
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
        Throwable cause = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);

        String serviceHint = toServiceHint(routeId);
        String message = serviceHint != null
                ? String.format("Downstream service '%s' is unavailable. Please try again later.", serviceHint)
                : "Downstream service is unavailable. Please try again later.";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
        body.put("message", message);
        body.put("routeId", routeId);
        if (serviceHint != null) {
            body.put("service", serviceHint);
        }
        body.put("method", method);
        body.put("path", path);
        body.put("requestId", exchange.getRequest().getId());
        if (correlationId != null && !correlationId.isBlank()) {
            body.put("correlationId", correlationId);
        }
        if (cause != null) {
            body.put("cause", cause.getClass().getSimpleName());
        }

        LOG.warn("Fallback triggered for routeId={}, path={}, method={}, cause={}", routeId, path, method,
                cause != null ? cause.getClass().getSimpleName() : "n/a");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, "30")
                .body(body);
    }

    private String toServiceHint(String routeId) {
        if (routeId == null) return null;
        return switch (routeId) {
            case "user-service" -> "user-service";
            case "auth-service", "user-login-proxy" -> "auth-service";
            case "account-service" -> "bankaccount-service";
            case "transaction-service" -> "transaction-service";
            case "frontend-service" -> "frontend";
            default -> routeId;
        };
    }
}
