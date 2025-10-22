package com.capbank.gateway_service.core.application.service;

import com.capbank.gateway_service.core.application.ports.out.AccountClientPort;
import com.capbank.gateway_service.core.application.ports.out.AuthClientPort;
import com.capbank.gateway_service.core.application.ports.out.UserClientPort;
import com.capbank.gateway_service.core.domain.model.GatewayRequestContext;
import com.capbank.gateway_service.infra.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GatewayOrchestratorService {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayOrchestratorService.class);

    private final AuthClientPort authClient;
    private final UserClientPort userClient;
    private final AccountClientPort accountClient;

    public GatewayOrchestratorService(AuthClientPort authClient,
                                      UserClientPort userClient,
                                      AccountClientPort accountClient) {
        this.authClient = authClient;
        this.userClient = userClient;
        this.accountClient = accountClient;
    }

    public Map<String, Object> handleUserRegistration(Map<String, Object> userPayload) {
        GatewayRequestContext context = new GatewayRequestContext("gateway-service");
        LOG.info("[Gateway] (reqId={}) Starting user registration", context.getRequestId());

        Map<String, Object> userResponse = userClient.registerUser(userPayload);
        if (userResponse == null || userResponse.get("id") == null) {
            throw new GatewayException("User service did not return a valid user");
        }

        try {
            accountClient.createAccount(Map.of(
                    "userId", userResponse.get("id"),
                    "cpf", userResponse.get("cpf"),
                    "fullName", userResponse.get("fullName"),
                    "accountType", userResponse.getOrDefault("accountType", "CHECKING")
            ));
            LOG.info("[Gateway] Account created for userId={}", userResponse.get("id"));
        } catch (Exception e) {
            LOG.warn("[Gateway] Account creation failed for userId={}: {}", userResponse.get("id"), e.getMessage());
        }

        return userResponse;
    }

    public Map<String, Object> handleLogin(String cpf, String password) {
        if (!userClient.validateCredentials(cpf, password)) {
            throw new GatewayException("Invalid CPF or password.");
        }
        String token = authClient.generateToken(cpf, password);
        return Map.of("accessToken", token, "tokenType", "Bearer");
    }

    private String safeHash(String value) {
        return Integer.toHexString(value == null ? 0 : value.hashCode());
    }
}
