package com.capbank.gateway_service.infra.client;

import com.capbank.gateway_service.core.application.ports.out.AccountClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountClientAdapter implements AccountClientPort {

    private final HttpClientBaseAdapter http;

    @Value("${services.account.url}")
    private String accountUrl;

    public AccountClientAdapter(HttpClientBaseAdapter http) {
        this.http = http;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> createAccount(Map<String, Object> accountPayload) {
        return http.post(accountUrl, accountPayload, Map.class);
    }
}
