package com.capbank.transaction_service.core.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private final BigDecimal amount;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Money(String amount) {
        this(new BigDecimal(amount));
    }

    public Money(double amount) {
        this(BigDecimal.valueOf(amount));
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isEqualTo(Money other) {
        return this.amount.compareTo(other.amount) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}