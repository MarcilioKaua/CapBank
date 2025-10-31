package com.capbank.user_service.infra.client;

import com.capbank.user_service.core.application.ports.out.GatewayClientPort;
import com.capbank.user_service.infra.client.dto.AuthResponseDTO;
import com.capbank.user_service.infra.client.dto.UserLoggedEvent;
import com.capbank.user_service.infra.client.dto.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class GatewayClientAdapter implements GatewayClientPort {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayClientAdapter.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public GatewayClientAdapter(RestTemplate restTemplate,
                                CircuitBreakerFactory<?, ?> circuitBreakerFactory,
                                @Value("${app.gateway.base-url:http://localhost:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.baseUrl = baseUrl;
    }

    @Override
    public void createForUser(UUID userId, String accountType) {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEventType("USER_REGISTERED");
        event.setUserId(userId);
        event.setAccountType(accountType);
        event.setOccurredAt(OffsetDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegisteredEvent> entity = new HttpEntity<>(event, headers);

        String url = baseUrl + "/api/gateway/user-registered";
        CircuitBreaker cb = circuitBreakerFactory.create("userGatewayClient");

        cb.run(() -> {
            restTemplate.postForEntity(url, entity, Void.class);
            return null;
        }, throwable -> {
            LOG.error("Gateway orchestration failed for userId={}: {}", userId, throwable.getMessage());
            throw new IllegalStateException("Gateway unavailable to process user registration", throwable);
        });

        LOG.info("Sent USER_REGISTERED to gateway for userId={} via {}", userId, url);
    }

    @Override
    public AuthResponseDTO loginForUser(String cpf, String password) {
        UserLoggedEvent event = new UserLoggedEvent();
        event.setEventType("USER_LOGGED");
        event.setCpf(cpf);
        event.setPassword(password);
        event.setOccurredAt(OffsetDateTime.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserLoggedEvent> entity = new HttpEntity<>(event, headers);

        String url = baseUrl + "/api/gateway/user-logged";
        CircuitBreaker cb = circuitBreakerFactory.create("userGatewayClient");

        return cb.run(() -> {
            var response = restTemplate.postForEntity(url, entity, AuthResponseDTO.class);
            LOG.info("Received token from gateway for cpf={} via {}", cpf, url);
            return response.getBody();
        }, throwable -> {
            LOG.error("Gateway orchestration failed for cpf={}: {}", cpf, throwable.getMessage());
            throw new IllegalStateException("Gateway unavailable to process user login", throwable);
        });
    }
}
