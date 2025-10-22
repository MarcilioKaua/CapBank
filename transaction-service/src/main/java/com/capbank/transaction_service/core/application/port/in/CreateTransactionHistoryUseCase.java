package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;


public interface CreateTransactionHistoryUseCase {

    TransactionHistory createTransactionHistory(CreateTransactionHistoryCommand command);

   
    record CreateTransactionHistoryCommand(
            AccountId accountId,
            TransactionId transactionId,
            Money balanceBefore,
            Money transactionAmount,
            TransactionType transactionType,
            String description
    ) {
        public CreateTransactionHistoryCommand {
            if (accountId == null) {
                throw new IllegalArgumentException("AccountId cannot be null");
            }
            if (transactionId == null) {
                throw new IllegalArgumentException("TransactionId cannot be null");
            }
            if (balanceBefore == null) {
                throw new IllegalArgumentException("Balance before cannot be null");
            }
            if (transactionAmount == null) {
                throw new IllegalArgumentException("Transaction amount cannot be null");
            }
            if (transactionType == null) {
                throw new IllegalArgumentException("Transaction type cannot be null");
            }
        }
    }
}