package com.capbank.auth_service.infra.client;

import com.capbank.auth_service.core.application.ports.out.UserClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserClientAdapter implements UserClientPort {

    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public UserClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        try {
            Map<String, String> body = Map.of("email", email, "password", password);
            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    userServiceUrl + "/validate", body, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }
}
