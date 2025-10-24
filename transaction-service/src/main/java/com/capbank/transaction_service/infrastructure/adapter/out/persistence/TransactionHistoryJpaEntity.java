package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_history", indexes = {
        @Index(name = "idx_account_id", columnList = "account_id"),
        @Index(name = "idx_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_record_date", columnList = "record_date")
})
public class TransactionHistoryJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @Column(name = "balance_before", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "transaction_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal transactionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    
    public TransactionHistoryJpaEntity() {}

    public TransactionHistoryJpaEntity(
            UUID id,
            UUID accountId,
            UUID transactionId,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            BigDecimal transactionAmount,
            TransactionType transactionType,
            TransactionStatus status,
            String description,
            LocalDateTime recordDate) {
        this.id = id;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.transactionAmount = transactionAmount;
        this.transactionType = transactionType;
        this.status = status;
        this.description = description;
        this.recordDate = recordDate;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(BigDecimal balanceBefore) { this.balanceBefore = balanceBefore; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDateTime recordDate) { this.recordDate = recordDate; }
}