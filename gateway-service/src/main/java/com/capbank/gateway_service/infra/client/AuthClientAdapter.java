package com.capbank.gateway_service.infra.client;

import com.capbank.gateway_service.core.application.ports.out.AuthClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthClientAdapter implements AuthClientPort {

    private static final Logger LOG = LoggerFactory.getLogger(AuthClientAdapter.class);

    private final HttpClientBaseAdapter http;
    @Value("${services.auth.base-url:http://localhost:8083/api/auth}")
    private String authUrl;

    public AuthClientAdapter(HttpClientBaseAdapter http) {
        this.http = http;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String generateToken(String cpf, String password) {
        try {
            Map<String, Object> resp = http.post(authUrl + "/login",
                    Map.of("cpf", cpf, "password", password), Map.class);
            if (resp == null || resp.get("accessToken") == null) {
                throw new IllegalStateException("Auth response without accessToken");
            }
            return resp.get("accessToken").toString();
        } catch (Exception e) {
            LOG.error("Auth service call failed: {}", e.getMessage());
            throw e;
        }
    }
}
