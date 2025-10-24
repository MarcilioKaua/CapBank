package com.capbank.transaction_service.core.application.port.out;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.enums.NotificationChannel;
import com.capbank.transaction_service.core.domain.enums.NotificationType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;


public interface NotificationServicePort {


    boolean sendTransactionNotification(TransactionNotification notification);

    record TransactionNotification(
            String userId,
            AccountId accountId,
            NotificationType type,
            NotificationChannel channel,
            String title,
            String message,
            TransactionMetadata transactionData
    ) {}

   
    record TransactionMetadata(
            String transactionId,
            String transactionType,
            String amount,
            String status,
            String description,
            String timestamp
    ) {
        public static TransactionMetadata from(Transaction transaction) {
            return new TransactionMetadata(
                    transaction.getId().toString(),
                    transaction.getType().toString(),
                    transaction.getAmount().toString(),
                    transaction.getStatus().toString(),
                    transaction.getDescription(),
                    transaction.getTransactionDate().toString()
            );
        }
    }
}