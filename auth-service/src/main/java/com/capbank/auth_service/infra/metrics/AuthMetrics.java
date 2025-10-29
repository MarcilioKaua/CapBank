package com.capbank.auth_service.infra.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuthMetrics {

    private final MeterRegistry registry;

    public AuthMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementLoginSuccess() {
        registry.counter("auth.logins.total", "status", "success").increment();
    }

    public void incrementLoginFailure() {
        registry.counter("auth.logins.total", "status", "failure").increment();
    }

    public void incrementTokenRefreshed() {
        registry.counter("auth.tokens.refreshed.total").increment();
    }
}
