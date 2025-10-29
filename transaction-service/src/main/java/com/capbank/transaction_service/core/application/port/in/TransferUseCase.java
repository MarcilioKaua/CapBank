package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;

public interface TransferUseCase {

    TransactionResult processTransfer(TransferCommand command);

    record TransferCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount,
            String description
    ) {
        public TransferCommand {
            if (sourceAccountId == null) {
                throw new IllegalArgumentException("Source account cannot be null for transfers");
            }
            if (targetAccountId == null) {
                throw new IllegalArgumentException("Target account cannot be null for transfers");
            }
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
            if (sourceAccountId.equals(targetAccountId)) {
                throw new IllegalArgumentException("Source and target accounts cannot be the same");
            }
        }
    }

    record TransactionResult(
            Transaction transaction,
            String message,
            boolean notificationSent
    ) {}
}
