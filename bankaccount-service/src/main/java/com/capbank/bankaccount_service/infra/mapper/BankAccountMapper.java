package com.capbank.bankaccount_service.infra.mapper;

import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.entity.BankAccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountEntity toEntity(BankAccount domain);
    BankAccount toDomain(BankAccountEntity entity);
}
