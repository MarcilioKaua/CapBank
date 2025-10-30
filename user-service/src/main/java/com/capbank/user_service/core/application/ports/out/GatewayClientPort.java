package com.capbank.user_service.core.application.ports.out;

import java.util.UUID;

public interface GatewayClientPort {
    void createForUser(UUID userId, String accountType);
}
