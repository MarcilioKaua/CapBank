package com.capbank.user_service.infra.client;

import com.capbank.user_service.core.application.ports.out.GatewayClientPort;
import com.capbank.user_service.infra.client.dto.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class GatewayClientAdapter implements GatewayClientPort {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayClientAdapter.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GatewayClientAdapter(RestTemplate restTemplate,
                                @Value("${app.gateway.base-url:http://localhost:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public void createForUser(UUID userId, String accountType) {
        try {
            UserRegisteredEvent event = new UserRegisteredEvent();
            event.setEventType("USER_REGISTERED");
            event.setUserId(userId);
            event.setAccountType(accountType);
            event.setOccurredAt(OffsetDateTime.now());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserRegisteredEvent> entity = new HttpEntity<>(event, headers);

            String url = baseUrl + "/api/gateway/user-registered";
            restTemplate.postForEntity(url, entity, Void.class);
            LOG.info("Sent USER_REGISTERED to gateway for userId={} via {}", userId, url);
        } catch (RestClientException ex) {
            LOG.error("Gateway orchestration failed for userId={}: {}", userId, ex.getMessage());
            throw ex;
        }
    }
}
