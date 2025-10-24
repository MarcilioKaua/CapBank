package com.capbank.transaction_service.core.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class AccountId {
    private final UUID value;

    public AccountId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AccountId cannot be null");
        }
        this.value = value;
    }

    public AccountId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("AccountId cannot be null or empty");
        }
        try {
            this.value = UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for AccountId: " + value);
        }
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccountId accountId = (AccountId) obj;
        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}