package com.capbank.bankaccount_service.infra.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountRequestDTO {
    private String accountNumber;
    private String agency;
    private BigDecimal balance;
    private String accountType;
    private UUID userId;

    public BankAccountRequestDTO() {
    }

    public BankAccountRequestDTO(String accountNumber, String agency, BigDecimal balance, String accountType, UUID userId) {
        this.accountNumber = accountNumber;
        this.agency = agency;
        this.balance = balance;
        this.accountType = accountType;
        this.userId = userId;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
