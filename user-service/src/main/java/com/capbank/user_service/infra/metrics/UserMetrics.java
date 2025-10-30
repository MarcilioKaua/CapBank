package com.capbank.user_service.infra.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetrics {

    private final MeterRegistry registry;

    public UserMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementCreateSuccess() {
        registry.counter("user.create.total", "status", "success").increment();
    }

    public void incrementCreateFailure() {
        registry.counter("user.create.total", "status", "failure").increment();
    }

}
