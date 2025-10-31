package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FindTransactionUseCase {


    Optional<Transaction> findById(TransactionId transactionId);

    TransactionPage findByAccount(FindTransactionQuery query);

  
    record FindTransactionQuery(
            AccountId accountId,
            TransactionType transactionType,
            TransactionStatus transactionStatus,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        public FindTransactionQuery {
            if (accountId == null) {
                throw new IllegalArgumentException("ID da conta não pode ser nulo");
            }
            if (page < 0) {
                throw new IllegalArgumentException("Página não pode ser negativa");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Tamanho deve ser positivo");
            }
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Data de início não pode ser após a data de fim");
            }
                }

        
        public static FindTransactionQuery create(AccountId accountId, int page, int size) {
            return new FindTransactionQuery(
                    accountId,
                    null, 
                    null, 
                    null, 
                    null, 
                    page,
                    size,
                    "transactionDate", 
                    "DESC" 
            );
        }
    }


    record TransactionPage(
            List<Transaction> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {}
}