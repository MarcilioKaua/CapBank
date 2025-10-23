package com.capbank.transaction_service.core.application.service;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionUseCase;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase;
import com.capbank.transaction_service.core.application.port.in.UpdateTransactionStatusUseCase;
import com.capbank.transaction_service.core.application.port.out.NotificationServicePort;
import com.capbank.transaction_service.core.application.port.out.TransactionHistoryRepositoryPort;
import com.capbank.transaction_service.core.application.port.out.TransactionRepositoryPort;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.NotificationChannel;
import com.capbank.transaction_service.core.domain.enums.NotificationType;
import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService implements CreateTransactionUseCase, FindTransactionUseCase, UpdateTransactionStatusUseCase {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepositoryPort transactionRepository;
    private final TransactionHistoryRepositoryPort historyRepository;
    private final NotificationServicePort notificationService;

    public TransactionService(
            TransactionRepositoryPort transactionRepository,
            TransactionHistoryRepositoryPort historyRepository,
            NotificationServicePort notificationService) {
        this.transactionRepository = transactionRepository;
        this.historyRepository = historyRepository;
        this.notificationService = notificationService;
    }

    @Override
    public TransactionResult processTransaction(CreateTransactionCommand command) {
        logger.info("Processing transaction: type={}, amount={}", command.type(), command.amount());

        try {
            // 1. Criar a transação
            Transaction transaction = createTransactionFromCommand(command);
            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("Transaction created with ID: {}", savedTransaction.getId());

            // 2. Criar o histórico (simulando consulta de saldo atual)
            Money currentBalance = getCurrentBalance(savedTransaction.getPrimaryAccountId());
            TransactionHistory history = createHistoryFromTransaction(savedTransaction, currentBalance);
            historyRepository.save(history);
            logger.info("Transaction history created for transaction: {}", savedTransaction.getId());

            // 3. Enviar notificação
            boolean notificationSent = sendTransactionNotification(savedTransaction);
            logger.info("Notification sent: {} for transaction: {}", notificationSent, savedTransaction.getId());

            String message = String.format("Transaction processed successfully. Amount: %s, Type: %s",
                    savedTransaction.getAmount(), savedTransaction.getType());

            return new TransactionResult(savedTransaction, message, notificationSent);

        } catch (Exception e) {
            logger.error("Error processing transaction: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Transaction> findById(TransactionId transactionId) {
        logger.info("Finding transaction by ID: {}", transactionId);
        return transactionRepository.findById(transactionId);
    }

    @Override
    public TransactionPage findByAccount(FindTransactionQuery query) {
        logger.info("Finding transactions for account: {}", query.accountId());


        if (query.size() > 100) {
            throw new IllegalArgumentException("Page size cannot be greater than 100");
        }

        return transactionRepository.findByAccountWithFilters(query);
    }

    @Override
    public Transaction updateStatus(UpdateStatusCommand command) {
        logger.info("Updating transaction status: id={}, newStatus={}",
                   command.transactionId(), command.newStatus());

        Transaction transaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + command.transactionId()));

      
        transaction.updateStatus(command.newStatus());
        Transaction updatedTransaction = transactionRepository.update(transaction);

        if (command.newStatus() == TransactionStatus.FAILED) {
            sendStatusChangeNotification(updatedTransaction, command.reason());
        }

        logger.info("Transaction status updated successfully: {}", command.transactionId());
        return updatedTransaction;
    }

    private Transaction createTransactionFromCommand(CreateTransactionCommand command) {
        return switch (command.type()) {
            case DEPOSIT -> Transaction.createDeposit(
                    command.targetAccountId(), command.amount(), command.description());
            case WITHDRAWAL -> Transaction.createWithdrawal(
                    command.sourceAccountId(), command.amount(), command.description());
            case TRANSFER -> Transaction.createTransfer(
                    command.sourceAccountId(), command.targetAccountId(),
                    command.amount(), command.description());
        };
    }

    private TransactionHistory createHistoryFromTransaction(Transaction transaction, Money currentBalance) {
        Money balanceBefore = switch (transaction.getType()) {
            case DEPOSIT -> {
                try {
                    yield currentBalance.subtract(transaction.getAmount());
                } catch (IllegalArgumentException e) {
                    yield Money.ZERO;
                }
            }
            case WITHDRAWAL, TRANSFER -> currentBalance.add(transaction.getAmount());
        };

        return switch (transaction.getType()) {
            case DEPOSIT -> TransactionHistory.createDepositHistory(
                    transaction.getPrimaryAccountId(),
                    transaction.getId(),
                    balanceBefore,
                    transaction.getAmount(),
                    transaction.getDescription()
            );
            case WITHDRAWAL -> TransactionHistory.createWithdrawalHistory(
                    transaction.getPrimaryAccountId(),
                    transaction.getId(),
                    balanceBefore,
                    transaction.getAmount(),
                    transaction.getDescription()
            );
            case TRANSFER -> TransactionHistory.createTransferHistory(
                    transaction.getPrimaryAccountId(),
                    transaction.getId(),
                    balanceBefore,
                    transaction.getAmount(),
                    transaction.getDescription()
            );
        };
    }

    private boolean sendTransactionNotification(Transaction transaction) {
        try {
            String title = generateNotificationTitle(transaction);
            String message = generateNotificationMessage(transaction);

            NotificationServicePort.TransactionNotification notification =
                    new NotificationServicePort.TransactionNotification(
                            "user-" + transaction.getPrimaryAccountId().toString(), 
                            transaction.getPrimaryAccountId(),
                            NotificationType.TRANSACTION,
                            NotificationChannel.EMAIL, 
                            title,
                            message,
                            NotificationServicePort.TransactionMetadata.from(transaction)
                    );

            return notificationService.sendTransactionNotification(notification);
        } catch (Exception e) {
            logger.warn("Failed to send notification for transaction {}: {}",
                       transaction.getId(), e.getMessage());
            return false;
        }
    }

    private void sendStatusChangeNotification(Transaction transaction, String reason) {
        try {
            String title = "Transaction Failed";
            String message = String.format("Your %s transaction of %s has failed. Reason: %s",
                    transaction.getType().toString().toLowerCase(),
                    transaction.getAmount(),
                    reason != null ? reason : "Unknown error");

            NotificationServicePort.TransactionNotification notification =
                    new NotificationServicePort.TransactionNotification(
                            "user-" + transaction.getPrimaryAccountId().toString(),
                            transaction.getPrimaryAccountId(),
                            NotificationType.ALERT,
                            NotificationChannel.EMAIL,
                            title,
                            message,
                            NotificationServicePort.TransactionMetadata.from(transaction)
                    );

            notificationService.sendTransactionNotification(notification);
        } catch (Exception e) {
            logger.warn("Failed to send status change notification: {}", e.getMessage());
        }
    }

    private String generateNotificationTitle(Transaction transaction) {
        return switch (transaction.getType()) {
            case DEPOSIT -> "Deposit Successful";
            case WITHDRAWAL -> "Withdrawal Successful";
            case TRANSFER -> "Transfer Successful";
        };
    }

    private String generateNotificationMessage(Transaction transaction) {
        return switch (transaction.getType()) {
            case DEPOSIT -> String.format("A deposit of %s has been processed successfully.",
                    transaction.getAmount());
            case WITHDRAWAL -> String.format("A withdrawal of %s has been processed successfully.",
                    transaction.getAmount());
            case TRANSFER -> String.format("A transfer of %s has been processed successfully.",
                    transaction.getAmount());
        };
    }

    private Money getCurrentBalance(com.capbank.transaction_service.core.domain.valueobject.AccountId accountId) {

        return historyRepository.findLatestByAccountId(accountId)
                .map(TransactionHistory::getBalanceAfter)
                .orElse(new Money("0.00")); 
    }
}