package com.capbank.transaction_service.core.application.port.out;

import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.FindTransactionQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.TransactionPage;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {

 
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId transactionId);

    List<Transaction> findByAccount(AccountId accountId);

    TransactionPage findByAccountWithFilters(FindTransactionQuery query);

    Transaction update(Transaction transaction);

    long countByAccount(AccountId accountId);

    boolean exists(TransactionId transactionId);
}