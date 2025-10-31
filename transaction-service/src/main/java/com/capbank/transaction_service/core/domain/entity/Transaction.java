package com.capbank.transaction_service.core.domain.entity;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;

import java.time.LocalDateTime;
import java.util.Objects;


public class Transaction {
    private final TransactionId id;
    private final AccountId sourceAccountId;  
    private final AccountId targetAccountId;  
    private final TransactionType type;
    private final Money amount;
    private final String description;
    private final LocalDateTime transactionDate;
    private TransactionStatus status;  

    private Transaction(Builder builder) {
        this.id = builder.id != null ? builder.id : TransactionId.generate();
        this.sourceAccountId = builder.sourceAccountId;
        this.targetAccountId = builder.targetAccountId;
        this.type = Objects.requireNonNull(builder.type, "Tipo de transação não pode ser nulo");
        this.amount = Objects.requireNonNull(builder.amount, "Valor não pode ser nulo");
        this.description = builder.description != null ? builder.description : "";
        this.transactionDate = builder.transactionDate != null ? builder.transactionDate : LocalDateTime.now();
        this.status = builder.status != null ? builder.status : TransactionStatus.PENDING;

        validateBusinessRules();
    }

    private void validateBusinessRules() {
      
        switch (type) {
            case DEPOSIT -> {
                if (targetAccountId == null) {
                    throw new IllegalArgumentException("Conta de destino é obrigatória para depósitos");
                }
                if (sourceAccountId != null) {
                    throw new IllegalArgumentException("Conta de origem deve ser nula para depósitos");
                }
            }
            case WITHDRAWAL -> {
                if (sourceAccountId == null) {
                    throw new IllegalArgumentException("Conta de origem é obrigatória para saques");
                }
                if (targetAccountId != null) {
                    throw new IllegalArgumentException("Conta de destino deve ser nula para saques");
                }
            }
            case TRANSFER -> {
                if (sourceAccountId == null || targetAccountId == null) {
                    throw new IllegalArgumentException("Ambas as contas de origem e destino são obrigatórias para transferências");
                }
                if (sourceAccountId.equals(targetAccountId)) {
                    throw new IllegalArgumentException("Contas de origem e destino não podem ser as mesmas");
                }
            }
        }
    }

    public static Transaction createDeposit(AccountId targetAccountId, Money amount, String description) {
        return new Builder()
                .targetAccountId(targetAccountId)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .description(description)
                .status(TransactionStatus.SUCCESS)
                .build();
    }

    // para saque
    public static Transaction createWithdrawal(AccountId sourceAccountId, Money amount, String description) {
        return new Builder()
                .sourceAccountId(sourceAccountId)
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .description(description)
                .status(TransactionStatus.SUCCESS)
                .build();
    }

    // para transferência
    public static Transaction createTransfer(AccountId sourceAccountId, AccountId targetAccountId, Money amount, String description) {
        return new Builder()
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .description(description)
                .status(TransactionStatus.SUCCESS)
                .build();
    }

    public Transaction updateStatus(TransactionStatus newStatus) {
        if (this.status == TransactionStatus.SUCCESS && newStatus != TransactionStatus.SUCCESS) {
            throw new IllegalArgumentException("Não é possível alterar o status de uma transação bem-sucedida");
        }
        this.status = newStatus;
        return this;
    }

    public boolean involvesAccount(AccountId accountId) {
        return accountId.equals(sourceAccountId) || accountId.equals(targetAccountId);
    }

    public AccountId getPrimaryAccountId() {
        return switch (type) {
            case DEPOSIT -> targetAccountId;
            case WITHDRAWAL -> sourceAccountId;
            case TRANSFER -> sourceAccountId; 
        };
    }

    public TransactionId getId() { return id; }
    public AccountId getSourceAccountId() { return sourceAccountId; }
    public AccountId getTargetAccountId() { return targetAccountId; }
    public TransactionType getType() { return type; }
    public Money getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public TransactionStatus getStatus() { return status; }

    public static class Builder {
        private TransactionId id;
        private AccountId sourceAccountId;
        private AccountId targetAccountId;
        private TransactionType type;
        private Money amount;
        private String description;
        private LocalDateTime transactionDate;
        private TransactionStatus status;

        public Builder id(TransactionId id) {
            this.id = id;
            return this;
        }

        public Builder sourceAccountId(AccountId sourceAccountId) {
            this.sourceAccountId = sourceAccountId;
            return this;
        }

        public Builder targetAccountId(AccountId targetAccountId) {
            this.targetAccountId = targetAccountId;
            return this;
        }

        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", sourceAccount=" + sourceAccountId +
                ", targetAccount=" + targetAccountId +
                ", status=" + status +
                ", date=" + transactionDate +
                '}';
    }
}