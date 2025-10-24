package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_source_account", columnList = "source_account_id"),
        @Index(name = "idx_target_account", columnList = "target_account_id"),
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_status", columnList = "status")
})
public class TransactionJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "source_account_id")
    private UUID sourceAccountId;

    @Column(name = "target_account_id")
    private UUID targetAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TransactionJpaEntity() {}

    public TransactionJpaEntity(
            UUID id,
            UUID sourceAccountId,
            UUID targetAccountId,
            TransactionType transactionType,
            BigDecimal amount,
            String description,
            LocalDateTime transactionDate,
            TransactionStatus status) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(UUID sourceAccountId) { this.sourceAccountId = sourceAccountId; }

    public UUID getTargetAccountId() { return targetAccountId; }
    public void setTargetAccountId(UUID targetAccountId) { this.targetAccountId = targetAccountId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}