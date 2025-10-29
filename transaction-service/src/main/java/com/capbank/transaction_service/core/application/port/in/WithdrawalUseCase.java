package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;

public interface WithdrawalUseCase {

    TransactionResult processWithdrawal(WithdrawalCommand command);

    record WithdrawalCommand(
            AccountId sourceAccountId,
            Money amount,
            String description
    ) {
        public WithdrawalCommand {
            if (sourceAccountId == null) {
                throw new IllegalArgumentException("Source account cannot be null for withdrawals");
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
