package com.capbank.gateway_service.infra.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GatewayMetrics {

    public GatewayMetrics(MeterRegistry registry) {
        registry.counter("gateway.requests.total", "description", "Total de requisições roteadas pelo gateway");
        registry.counter("gateway.errors.total", "description", "Total de erros de roteamento no gateway");
    }

    public void incrementRequest(MeterRegistry registry) {
        registry.counter("gateway.requests.total").increment();
    }

    public void incrementError(MeterRegistry registry) {
        registry.counter("gateway.errors.total").increment();
    }
}

