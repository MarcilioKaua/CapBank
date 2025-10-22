package com.capbank.transaction_service.core.application.port.in;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;


public interface UpdateTransactionStatusUseCase {

    Transaction updateStatus(UpdateStatusCommand command);

   
    record UpdateStatusCommand(
            TransactionId transactionId,
            TransactionStatus newStatus,
            String reason
    ) {
        public UpdateStatusCommand {
            if (transactionId == null) {
                throw new IllegalArgumentException("TransactionId cannot be null");
            }
            if (newStatus == null) {
                throw new IllegalArgumentException("New status cannot be null");
            }
        }
    }
}