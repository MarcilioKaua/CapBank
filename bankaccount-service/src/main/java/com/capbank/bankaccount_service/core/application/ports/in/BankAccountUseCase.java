package com.capbank.bankaccount_service.core.application.ports.in;

import com.capbank.bankaccount_service.core.domain.model.BankAccount;

import java.util.List;
import java.util.UUID;

public interface BankAccountUseCase {
    BankAccount create(BankAccount account);
    BankAccount update(UUID id, BankAccount account);
    void delete(UUID id);
    BankAccount findById(UUID id);
    List<BankAccount> findAll();
}
