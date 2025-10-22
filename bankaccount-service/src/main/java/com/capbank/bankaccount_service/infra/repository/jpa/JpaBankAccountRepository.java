package com.capbank.bankaccount_service.infra.repository.jpa;

import com.capbank.bankaccount_service.infra.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaBankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
}
