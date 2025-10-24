package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;

public interface CreateTransactionUseCase {

    TransactionResult processTransaction(CreateTransactionCommand command);

    record CreateTransactionCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            TransactionType type,
            Money amount,
            String description
    ) {
        public CreateTransactionCommand {
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
            if (type == null) {
                throw new IllegalArgumentException("Transaction type cannot be null");
            }

            switch (type) {
                case DEPOSIT -> {
                    if (targetAccountId == null) {
                        throw new IllegalArgumentException("Target account is required for deposits");
                    }
                }
                case WITHDRAWAL -> {
                    if (sourceAccountId == null) {
                        throw new IllegalArgumentException("Source account is required for withdrawals");
                    }
                }
                case TRANSFER -> {
                    if (sourceAccountId == null || targetAccountId == null) {
                        throw new IllegalArgumentException("Both accounts are required for transfers");
                    }
                }
            }
        }
    }

    record TransactionResult(
            Transaction transaction,
            String message,
            boolean notificationSent
    ) {}
}