package com.capbank.transaction_service.core.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class TransactionId {
    private final UUID value;

    public TransactionId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("TransactionId cannot be null");
        }
        this.value = value;
    }

    public TransactionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TransactionId cannot be null or empty");
        }
        try {
            this.value = UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for TransactionId: " + value);
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransactionId that = (TransactionId) obj;
        return Objects.equals(value, that.value);
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