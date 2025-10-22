package com.capbank.gateway_service.core.domain.model;

import java.time.Instant;
import java.util.UUID;

public class GatewayRequestContext {

    private final String requestId;
    private final Instant timestamp;
    private final String callerService;

    public GatewayRequestContext(String callerService) {
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.callerService = callerService;
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCallerService() {
        return callerService;
    }

    @Override
    public String toString() {
        return "GatewayRequestContext{" +
                "requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                ", callerService='" + callerService + '\'' +
                '}';
    }
}
