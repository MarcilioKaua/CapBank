package com.capbank.bankaccount_service.core.application.service;

import com.capbank.bankaccount_service.core.application.ports.in.BankAccountUseCase;
import com.capbank.bankaccount_service.core.application.ports.out.BankAccountRepositoryPort;
import com.capbank.bankaccount_service.core.domain.enums.AccountStatus;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.dto.BankAccountResponseDTO;
import com.capbank.bankaccount_service.infra.exception.BankAccountNotFoundException;
import com.capbank.bankaccount_service.infra.mapper.BankAccountResponseMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountServiceImpl implements BankAccountUseCase {

    private final BankAccountRepositoryPort bankAccountRepository;
    private final BankAccountResponseMapper bankAccountResponseMapper;

    public BankAccountServiceImpl(
            BankAccountRepositoryPort bankAccountRepository,
            BankAccountResponseMapper bankAccountResponseMapper
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountResponseMapper = bankAccountResponseMapper;
    }

    @Override
    public BankAccount create(BankAccount account) {
        account.setId(UUID.randomUUID());
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);
        return bankAccountRepository.save(account);
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount findById(UUID id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
    }

    public BankAccountResponseDTO findByAccountNumber(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
        return bankAccountResponseMapper.toResponse(account);
    }

    public BigDecimal getBalance(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .map(BankAccount::getBalance)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
    }

    public BankAccountResponseDTO updateBalance(String accountNumber, BigDecimal newBalance) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
        account.setBalance(newBalance);
        bankAccountRepository.save(account);
        return bankAccountResponseMapper.toResponse(account);
    }

    public BankAccountResponseDTO updateStatus(String accountNumber, String status) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));
        account.setStatus(AccountStatus.valueOf(status.toUpperCase()));
        bankAccountRepository.save(account);
        return bankAccountResponseMapper.toResponse(account);
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
}
