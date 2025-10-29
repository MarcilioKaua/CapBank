package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;

public interface DepositUseCase {

    TransactionResult processDeposit(DepositCommand command);

    record DepositCommand(
            AccountId targetAccountId,
            Money amount,
            String description
    ) {
        public DepositCommand {
            if (targetAccountId == null) {
                throw new IllegalArgumentException("Target account cannot be null for deposits");
            }
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
        }
    }

    record TransactionResult(
            Transaction transaction,
            String message,
            boolean notificationSent
    ) {}
}
