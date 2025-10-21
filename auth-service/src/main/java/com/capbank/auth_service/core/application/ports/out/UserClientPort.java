package com.capbank.auth_service.core.application.ports.out;

public interface UserClientPort {
    boolean validateCredentials(String email, String password);
}
