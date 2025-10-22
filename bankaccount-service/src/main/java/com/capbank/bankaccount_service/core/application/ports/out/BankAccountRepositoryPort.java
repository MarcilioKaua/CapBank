package com.capbank.bankaccount_service.core.application.ports.out;

import com.capbank.bankaccount_service.core.domain.model.BankAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepositoryPort {
    BankAccount save(BankAccount account);
    void deleteById(UUID id);
    Optional<BankAccount> findById(UUID id);
    List<BankAccount> findAll();
}
