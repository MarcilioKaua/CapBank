package com.capbank.gateway_service.infra.controller;

import com.capbank.gateway_service.core.application.service.GatewayOrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayController.class);

    private final GatewayOrchestratorService orchestrator;

    public GatewayController(GatewayOrchestratorService orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> userPayload) {
        LOG.info("Gateway received register request");
        return ResponseEntity.ok(orchestrator.handleUserRegistration(userPayload));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        LOG.info("Gateway received login request");
        return ResponseEntity.ok(orchestrator.handleLogin(request.get("cpf"), request.get("password")));
    }
}
