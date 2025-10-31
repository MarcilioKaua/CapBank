package com.capbank.transaction_service.core.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class TransactionId {
    private final UUID value;

    public TransactionId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ID da transação não pode ser nulo");
        }
        this.value = value;
    }

    public TransactionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da transação não pode ser nulo ou vazio");
        }
        try {
            this.value = UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato UUID inválido para ID da transação: " + value);
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