package com.capbank.bankaccount_service.core.application.service;

import com.capbank.bankaccount_service.core.application.ports.in.BankAccountUseCase;
import com.capbank.bankaccount_service.core.application.ports.out.BankAccountRepositoryPort;
import com.capbank.bankaccount_service.core.domain.enums.AccountStatus;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.exception.BankAccountNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountServiceImpl implements BankAccountUseCase {

    private final BankAccountRepositoryPort bankAccountRepository;

    public BankAccountServiceImpl(BankAccountRepositoryPort bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public BankAccount create(BankAccount account) {
        account.setId(UUID.randomUUID());
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount update(UUID id, BankAccount account) {
        BankAccount existing = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
        account.setId(existing.getId());
        return bankAccountRepository.save(account);
    }

    @Override
    public void delete(UUID id) {
        bankAccountRepository.deleteById(id);
    }

    @Override
    public BankAccount findById(UUID id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }
}
