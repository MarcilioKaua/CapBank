package com.capbank.gateway_service.infra.controller;

import com.capbank.gateway_service.infra.client.HttpClientBaseAdapter;
import com.capbank.gateway_service.infra.dto.BankAccountCreateRequest;
import com.capbank.gateway_service.infra.dto.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/gateway")
public class GatewayOrchestratorController {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayOrchestratorController.class);

    private final HttpClientBaseAdapter httpClient;
    private final String accountBaseUrl;

    public GatewayOrchestratorController(
            HttpClientBaseAdapter httpClient,
            @Value("${services.account.base-url:http://localhost:8084}") String accountBaseUrl) {
        this.httpClient = httpClient;
        this.accountBaseUrl = accountBaseUrl;
    }

    @PostMapping("/user-registered")
    public ResponseEntity<Void> onUserRegistered(@RequestBody UserRegisteredEvent event) {
        if (event.getUserId() == null || event.getAccountType() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LOG.info("Gateway received USER_REGISTERED for userId={}, accountType={}", event.getUserId(), event.getAccountType());

        BankAccountCreateRequest request = new BankAccountCreateRequest();
        request.setUserId(event.getUserId());
        request.setAccountType(event.getAccountType());
        request.setBalance(BigDecimal.ZERO);
        request.setAccountNumber(null);
        request.setAgency(null);

        String url = accountBaseUrl + "/api/bankaccount";
        httpClient.post(url, request, Void.class);

        LOG.info("Bank account created for userId={}", event.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        LOG.error("Gateway orchestration failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gateway orchestration failed");
    }
}
