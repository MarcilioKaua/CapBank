package com.capbank.transaction_service.core.domain.entity;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransactionHistory {
    private final UUID id;
    private final AccountId accountId;
    private final TransactionId transactionId;
    private final Money balanceBefore;
    private final Money balanceAfter;
    private final Money transactionAmount;
    private final TransactionType transactionType;
    private final TransactionStatus status;
    private final String description;
    private final LocalDateTime recordDate;

    private TransactionHistory(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.accountId = Objects.requireNonNull(builder.accountId, "ID da conta não pode ser nulo");
        this.transactionId = Objects.requireNonNull(builder.transactionId, "ID da transação não pode ser nulo");
        this.balanceBefore = Objects.requireNonNull(builder.balanceBefore, "Saldo anterior não pode ser nulo");
        this.balanceAfter = Objects.requireNonNull(builder.balanceAfter, "Saldo posterior não pode ser nulo");
        this.transactionAmount = Objects.requireNonNull(builder.transactionAmount, "Valor da transação não pode ser nulo");
        this.transactionType = Objects.requireNonNull(builder.transactionType, "Tipo de transação não pode ser nulo");
        this.status = Objects.requireNonNull(builder.status, "Status não pode ser nulo");
        this.description = builder.description != null ? builder.description : "";
        this.recordDate = builder.recordDate != null ? builder.recordDate : LocalDateTime.now();

        validateBusinessRules();
    }

    private void validateBusinessRules() {
        Money expectedBalance = calculateExpectedBalance();
        if (!balanceAfter.isEqualTo(expectedBalance)) {
            throw new IllegalArgumentException("Cálculo do saldo está incorreto");
        }
    }

    private Money calculateExpectedBalance() {
        return switch (transactionType) {
            case DEPOSIT -> balanceBefore.add(transactionAmount);
            case WITHDRAWAL -> balanceBefore.subtract(transactionAmount);
            case TRANSFER -> {
                yield balanceBefore.subtract(transactionAmount);
            }
        };
    }

    public static TransactionHistory createDepositHistory(
            AccountId accountId,
            TransactionId transactionId,
            Money balanceBefore,
            Money depositAmount,
            String description) {

        return new Builder()
                .accountId(accountId)
                .transactionId(transactionId)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceBefore.add(depositAmount))
                .transactionAmount(depositAmount)
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .description(description)
                .build();
    }

    public static TransactionHistory createWithdrawalHistory(
            AccountId accountId,
            TransactionId transactionId,
            Money balanceBefore,
            Money withdrawalAmount,
            String description) {

        return new Builder()
                .accountId(accountId)
                .transactionId(transactionId)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceBefore.subtract(withdrawalAmount))
                .transactionAmount(withdrawalAmount)
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .description(description)
                .build();
    }

    public static TransactionHistory createTransferHistory(
            AccountId accountId,
            TransactionId transactionId,
            Money balanceBefore,
            Money transferAmount,
            String description) {

        return new Builder()
                .accountId(accountId)
                .transactionId(transactionId)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceBefore.subtract(transferAmount))
                .transactionAmount(transferAmount)
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .description(description)
                .build();
    }

    public UUID getId() { return id; }
    public AccountId getAccountId() { return accountId; }
    public TransactionId getTransactionId() { return transactionId; }
    public Money getBalanceBefore() { return balanceBefore; }
    public Money getBalanceAfter() { return balanceAfter; }
    public Money getTransactionAmount() { return transactionAmount; }
    public TransactionType getTransactionType() { return transactionType; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getRecordDate() { return recordDate; }

    public static class Builder {
        private UUID id;
        private AccountId accountId;
        private TransactionId transactionId;
        private Money balanceBefore;
        private Money balanceAfter;
        private Money transactionAmount;
        private TransactionType transactionType;
        private TransactionStatus status;
        private String description;
        private LocalDateTime recordDate;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder accountId(AccountId accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder transactionId(TransactionId transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder balanceBefore(Money balanceBefore) {
            this.balanceBefore = balanceBefore;
            return this;
        }

        public Builder balanceAfter(Money balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }

        public Builder transactionAmount(Money transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder recordDate(LocalDateTime recordDate) {
            this.recordDate = recordDate;
            return this;
        }

        public TransactionHistory build() {
            return new TransactionHistory(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransactionHistory that = (TransactionHistory) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", transactionId=" + transactionId +
                ", transactionType=" + transactionType +
                ", amount=" + transactionAmount +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", status=" + status +
                ", recordDate=" + recordDate +
                '}';
    }
}