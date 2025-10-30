package com.capbank.auth_service.infra.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
