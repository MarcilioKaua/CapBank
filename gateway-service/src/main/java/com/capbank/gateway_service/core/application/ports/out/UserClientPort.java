package com.capbank.gateway_service.core.application.ports.out;

import java.util.Map;

public interface UserClientPort {
    Map<String, Object> registerUser(Map<String, Object> userPayload);
    boolean validateCredentials(String cpf, String password);
}
