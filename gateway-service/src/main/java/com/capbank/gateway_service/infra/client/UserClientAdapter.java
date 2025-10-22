package com.capbank.gateway_service.infra.client;

import com.capbank.gateway_service.core.application.ports.out.UserClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class UserClientAdapter implements UserClientPort {

    private static final Logger LOG = LoggerFactory.getLogger(UserClientAdapter.class);

    private final HttpClientBaseAdapter http;

    @Value("${services.user.url}")
    private String userUrl;

    public UserClientAdapter(HttpClientBaseAdapter http) {
        this.http = http;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> registerUser(Map<String, Object> userPayload) {
        String requestId = UUID.randomUUID().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", requestId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);
        return http.post(userUrl + "/register", entity, Map.class);
    }

    @Override
    public boolean validateCredentials(String cpf, String password) {
        try {
            Boolean ok = http.post(userUrl + "/validate",
                    Map.of("cpf", cpf, "password", password), Boolean.class);
            return Boolean.TRUE.equals(ok);
        } catch (Exception e) {
            LOG.error("User service validation failed: {}", e.getMessage());
            return false;
        }
    }
}
