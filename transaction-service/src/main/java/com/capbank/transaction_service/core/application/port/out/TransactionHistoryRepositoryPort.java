package com.capbank.transaction_service.core.application.port.out;

import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.FindTransactionHistoryQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.TransactionHistoryPage;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionHistoryRepositoryPort {


    TransactionHistory save(TransactionHistory transactionHistory);


    Optional<TransactionHistory> findById(UUID id);


    List<TransactionHistory> findByAccountId(AccountId accountId);

    TransactionHistoryPage findByAccountIdWithFilters(FindTransactionHistoryQuery query);

    boolean existsByTransactionId(UUID transactionId);

    Optional<TransactionHistory> findLatestByAccountId(AccountId accountId);
    
    long countByAccountId(AccountId accountId);
}