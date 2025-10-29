package com.capbank.gateway_service.infra.client;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClientBaseAdapter {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public HttpClientBaseAdapter(RestTemplate restTemplate, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public <T> T post(String url, Object body, Class<T> responseType) {
        CircuitBreaker cb = circuitBreakerFactory.create("gatewayHttpClient");
        return cb.run(() -> {
            ResponseEntity<T> response = restTemplate.postForEntity(url, body, responseType);
            return response.getBody();
        }, throwable -> null);
    }
}
