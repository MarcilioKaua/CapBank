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
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/gateway")
public class GatewayOrchestratorController {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayOrchestratorController.class);

    private final HttpClientBaseAdapter httpClient;
    private final String accountBaseUrl;
    private final String defaultAgency;

    public GatewayOrchestratorController(
            HttpClientBaseAdapter httpClient,
            @Value("${services.account.base-url:http://localhost:8084}") String accountBaseUrl,
            @Value("${bank.account.default-agency:0001}") String defaultAgency) {
        this.httpClient = httpClient;
        this.accountBaseUrl = accountBaseUrl;
        this.defaultAgency = defaultAgency;
    }

    @PostMapping("/user-registered")
    public ResponseEntity<Void> onUserRegistered(@RequestBody UserRegisteredEvent event) {
        if (event.getUserId() == null || event.getAccountType() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LOG.info("Gateway received USER_REGISTERED for userId={}, accountType={}", event.getUserId(), event.getAccountType());

        String generatedAccountNumber = generateAccountNumber();
        String agencyToUse = defaultAgency;

        BankAccountCreateRequest request = new BankAccountCreateRequest();
        request.setUserId(event.getUserId());
        request.setAccountType(event.getAccountType());
        request.setBalance(BigDecimal.ZERO);
        request.setAccountNumber(generatedAccountNumber);
        request.setAgency(agencyToUse);

        String url = accountBaseUrl + "/api/bankaccount";
        httpClient.post(url, request, Void.class);

        LOG.info("Bank account created for userId={} with agency={} and accountNumber=****{}",
                event.getUserId(), agencyToUse, generatedAccountNumber.substring(Math.max(0, generatedAccountNumber.length() - 4)));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private String generateAccountNumber() {
        int number = ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
        return String.valueOf(number);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        LOG.error("Gateway orchestration failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gateway orchestration failed");
    }
}
