package com.capbank.bankaccount_service.infra.mapper;

import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.dto.BankAccountRequestDTO;
import com.capbank.bankaccount_service.infra.dto.BankAccountResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    BankAccount toDomain(BankAccountRequestDTO dto);

    BankAccountResponseDTO toResponse(BankAccount domain);
}
