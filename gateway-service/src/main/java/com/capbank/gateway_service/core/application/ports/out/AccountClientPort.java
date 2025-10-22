package com.capbank.gateway_service.core.application.ports.out;

import java.util.Map;

public interface AccountClientPort {
    Map<String, Object> createAccount(Map<String, Object> accountPayload);
}
