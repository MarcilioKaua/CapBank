package com.capbank.bankaccount_service.infra.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMetrics {

    private final MeterRegistry registry;

    public BankAccountMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementAccountCreated() {
        registry.counter("accounts.created.total", "type", "creation").increment();
    }

}
