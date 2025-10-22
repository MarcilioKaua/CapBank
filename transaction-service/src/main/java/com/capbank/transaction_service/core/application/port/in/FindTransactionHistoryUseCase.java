package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FindTransactionHistoryUseCase {


    Optional<TransactionHistory> findById(UUID id);

    TransactionHistoryPage findByAccountId(FindTransactionHistoryQuery query);

  
    record FindTransactionHistoryQuery(
            AccountId accountId,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        public FindTransactionHistoryQuery {
            if (accountId == null) {
                throw new IllegalArgumentException("AccountId cannot be null");
            }
            if (page < 0) {
                throw new IllegalArgumentException("Page cannot be negative");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be positive");
            }
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }

        public static FindTransactionHistoryQuery create(
                AccountId accountId,
                int page,
                int size) {
            return new FindTransactionHistoryQuery(
                    accountId,
                    null, 
                    null, 
                    null, 
                    page,
                    size,
                    "recordDate", 
                    "DESC" 
            );
        }
    }

    record TransactionHistoryPage(
            List<TransactionHistory> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {}
}