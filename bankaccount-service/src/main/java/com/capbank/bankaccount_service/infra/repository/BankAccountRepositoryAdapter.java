package com.capbank.bankaccount_service.infra.repository;

import com.capbank.bankaccount_service.core.application.ports.out.BankAccountRepositoryPort;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankAccountRepositoryAdapter extends BankAccountRepositoryPort, JpaRepository<BankAccount, UUID> {
}
