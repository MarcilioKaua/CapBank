package com.capbank.bankaccount_service.core.domain.model;

import com.capbank.bankaccount_service.core.domain.enums.AccountStatus;
import com.capbank.bankaccount_service.core.domain.enums.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BankAccount {

    private UUID id;
    private String accountNumber;
    private String agency;
    private BigDecimal balance;
    private AccountType accountType;
    private UUID userId;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public BankAccount() {}

    public BankAccount(UUID id, String accountNumber, String agency, BigDecimal balance, AccountType accountType, UUID userId, AccountStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.balance = balance;
        this.accountType = accountType;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
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

    public AccountType getAccountType() {
        return accountType;
    }
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}