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
                throw new IllegalArgumentException("ID da conta não pode ser nulo");
            }
            if (transactionId == null) {
                throw new IllegalArgumentException("ID da transação não pode ser nulo");
            }
            if (balanceBefore == null) {
                throw new IllegalArgumentException("Saldo anterior não pode ser nulo");
            }
            if (transactionAmount == null) {
                throw new IllegalArgumentException("Valor da transação não pode ser nulo");
            }
            if (transactionType == null) {
                throw new IllegalArgumentException("Tipo de transação não pode ser nulo");
            }
        }
    }
}