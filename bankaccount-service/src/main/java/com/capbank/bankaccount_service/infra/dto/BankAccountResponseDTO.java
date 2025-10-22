package com.capbank.bankaccount_service.infra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BankAccountResponseDTO {
    private UUID id;
    private String accountNumber;
    private String agency;
    private BigDecimal balance;
    private String accountType;
    private String status;
    private UUID userId;
    private LocalDateTime createdAt;

    public BankAccountResponseDTO() {
    }

    public BankAccountResponseDTO(UUID id, String accountNumber, String agency, BigDecimal balance, String accountType, String status, UUID userId, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.balance = balance;
        this.accountType = accountType;
        this.status = status;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
