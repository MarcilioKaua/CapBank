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
                throw new IllegalArgumentException("Valor não pode ser nulo");
            }
            if (type == null) {
                throw new IllegalArgumentException("Tipo de transação não pode ser nulo");
            }

            switch (type) {
                case DEPOSIT -> {
                    if (targetAccountId == null) {
                        throw new IllegalArgumentException("Conta de destino é obrigatória para depósitos");
                    }
                }
                case WITHDRAWAL -> {
                    if (sourceAccountId == null) {
                        throw new IllegalArgumentException("Conta de origem é obrigatória para saques");
                    }
                }
                case TRANSFER -> {
                    if (sourceAccountId == null || targetAccountId == null) {
                        throw new IllegalArgumentException("Ambas as contas são obrigatórias para transferências");
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