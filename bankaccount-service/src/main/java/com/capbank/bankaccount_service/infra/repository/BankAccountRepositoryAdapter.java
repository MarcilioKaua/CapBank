package com.capbank.bankaccount_service.infra.repository;

import com.capbank.bankaccount_service.core.application.ports.out.BankAccountRepositoryPort;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.entity.BankAccountEntity;
import com.capbank.bankaccount_service.infra.mapper.BankAccountMapper;
import com.capbank.bankaccount_service.infra.repository.jpa.JpaBankAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BankAccountRepositoryAdapter implements BankAccountRepositoryPort {

    private final JpaBankAccountRepository jpaBankAccountRepository;
    private final BankAccountMapper bankAccountMapper;

    public BankAccountRepositoryAdapter(JpaBankAccountRepository jpaBankAccountRepository, BankAccountMapper bankAccountMapper) {
        this.jpaBankAccountRepository = jpaBankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public BankAccount save(BankAccount domain) {
        BankAccountEntity entity = bankAccountMapper.toEntity(domain);
        BankAccountEntity saved = jpaBankAccountRepository.save(entity);
        return bankAccountMapper.toDomain(saved);
    }

    @Override
    public Optional<BankAccount> findById(UUID id) {
        return jpaBankAccountRepository.findById(id)
                .map(bankAccountMapper::toDomain);
    }

    @Override
    public List<BankAccount> findAll() {
        return jpaBankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return jpaBankAccountRepository.findByAccountNumber(accountNumber)
                .map(bankAccountMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaBankAccountRepository.deleteById(id);
    }
}
