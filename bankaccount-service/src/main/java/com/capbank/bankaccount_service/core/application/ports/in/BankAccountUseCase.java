package com.capbank.bankaccount_service.core.application.ports.in;

import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.dto.BankAccountResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BankAccountUseCase {
    BankAccount create(BankAccount account);
    BankAccount update(UUID id, BankAccount account);
    void delete(UUID id);
    BankAccount findById(UUID id);
    List<BankAccount> findAll();
    BankAccountResponseDTO findByAccountNumber(String accountNumber);
    BankAccountResponseDTO findByUserId(String userId);
    BigDecimal getBalance(String accountNumber);
    BankAccountResponseDTO updateBalance(String accountNumber, BigDecimal newBalance);
    BankAccountResponseDTO updateStatus(String accountNumber, String status);
}
