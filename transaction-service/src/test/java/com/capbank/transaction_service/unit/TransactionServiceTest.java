package com.capbank.transaction_service.unit;

import com.capbank.transaction_service.core.application.port.in.DepositUseCase;
import com.capbank.transaction_service.core.application.port.in.WithdrawalUseCase;
import com.capbank.transaction_service.core.application.port.in.TransferUseCase;
import com.capbank.transaction_service.core.application.port.out.BankAccountServicePort;
import com.capbank.transaction_service.core.application.port.out.NotificationServicePort;
import com.capbank.transaction_service.core.application.port.out.TransactionHistoryRepositoryPort;
import com.capbank.transaction_service.core.application.port.out.TransactionRepositoryPort;
import com.capbank.transaction_service.core.application.service.TransactionService;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private TransactionHistoryRepositoryPort historyRepository;

    @Mock
    private NotificationServicePort notificationService;

    @Mock
    private BankAccountServicePort bankAccountService;

    private TransactionService transactionService;

    private AccountId sourceAccountId;
    private AccountId targetAccountId;
    private Money amount;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(
                transactionRepository,
                historyRepository,
                notificationService,
                bankAccountService
        );

        sourceAccountId = new AccountId("550e8400-e29b-41d4-a716-446655440000");
        targetAccountId = new AccountId("550e8400-e29b-41d4-a716-446655440001");
        amount = new Money(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should process deposit successfully")
    void shouldProcessDepositSuccessfully() {
      
        DepositUseCase.DepositCommand command = new DepositUseCase.DepositCommand(
                targetAccountId,
                amount,
                "Test deposit"
        );

        Transaction savedTransaction = Transaction.createDeposit(targetAccountId, amount, "Test deposit");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(historyRepository.findLatestByAccountId(any())).thenReturn(Optional.empty());
        when(historyRepository.save(any(TransactionHistory.class))).thenReturn(mock(TransactionHistory.class));
        when(notificationService.sendTransactionNotification(any())).thenReturn(true);

       
        DepositUseCase.TransactionResult result = transactionService.processDeposit(command);

        assertThat(result).isNotNull();
        assertThat(result.transaction()).isNotNull();
        assertThat(result.transaction().getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(result.transaction().getTargetAccountId()).isEqualTo(targetAccountId);
        assertThat(result.transaction().getAmount()).isEqualTo(amount);
        assertThat(result.notificationSent()).isTrue();
        assertThat(result.message()).contains("Deposit processed successfully");

        verify(transactionRepository).save(any(Transaction.class));
        verify(historyRepository).save(any(TransactionHistory.class));
        verify(notificationService).sendTransactionNotification(any());
    }

    @Test
    @DisplayName("Should process withdrawal successfully")
    void shouldProcessWithdrawalSuccessfully() {
       
        WithdrawalUseCase.WithdrawalCommand command = new WithdrawalUseCase.WithdrawalCommand(
                sourceAccountId,
                amount,
                "Test withdrawal"
        );

        Transaction savedTransaction = Transaction.createWithdrawal(sourceAccountId, amount, "Test withdrawal");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(historyRepository.findLatestByAccountId(any())).thenReturn(Optional.of(
                mock(TransactionHistory.class)
        ));
        when(historyRepository.save(any(TransactionHistory.class))).thenReturn(mock(TransactionHistory.class));
        when(notificationService.sendTransactionNotification(any())).thenReturn(true);

       
        WithdrawalUseCase.TransactionResult result = transactionService.processWithdrawal(command);

       
        assertThat(result).isNotNull();
        assertThat(result.transaction()).isNotNull();
        assertThat(result.transaction().getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(result.transaction().getSourceAccountId()).isEqualTo(sourceAccountId);
        assertThat(result.transaction().getAmount()).isEqualTo(amount);
        assertThat(result.notificationSent()).isTrue();
        assertThat(result.message()).contains("Withdrawal processed successfully");

        verify(transactionRepository).save(any(Transaction.class));
        verify(historyRepository).save(any(TransactionHistory.class));
        verify(notificationService).sendTransactionNotification(any());
    }

    @Test
    @DisplayName("Should process transfer successfully")
    void shouldProcessTransferSuccessfully() {
       
        TransferUseCase.TransferCommand command = new TransferUseCase.TransferCommand(
                sourceAccountId,
                targetAccountId,
                amount,
                "Test transfer"
        );

        Transaction savedTransaction = Transaction.createTransfer(
                sourceAccountId, targetAccountId, amount, "Test transfer"
        );
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(historyRepository.findLatestByAccountId(any())).thenReturn(Optional.empty());
        when(historyRepository.save(any(TransactionHistory.class))).thenReturn(mock(TransactionHistory.class));
        when(notificationService.sendTransactionNotification(any())).thenReturn(true);

        
        TransferUseCase.TransactionResult result = transactionService.processTransfer(command);

        
        assertThat(result).isNotNull();
        assertThat(result.transaction()).isNotNull();
        assertThat(result.transaction().getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(result.transaction().getSourceAccountId()).isEqualTo(sourceAccountId);
        assertThat(result.transaction().getTargetAccountId()).isEqualTo(targetAccountId);
        assertThat(result.transaction().getAmount()).isEqualTo(amount);
        assertThat(result.notificationSent()).isTrue();
        assertThat(result.message()).contains("Transfer processed successfully");

        verify(transactionRepository).save(any(Transaction.class));
        verify(historyRepository).save(any(TransactionHistory.class));
        verify(notificationService).sendTransactionNotification(any());
    }

    @Test
    @DisplayName("Should handle notification failure gracefully in deposit")
    void shouldHandleNotificationFailureGracefullyInDeposit() {
       
        DepositUseCase.DepositCommand command = new DepositUseCase.DepositCommand(
                targetAccountId,
                amount,
                "Test deposit"
        );

        Transaction savedTransaction = Transaction.createDeposit(targetAccountId, amount, "Test deposit");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(historyRepository.findLatestByAccountId(any())).thenReturn(Optional.empty());
        when(historyRepository.save(any(TransactionHistory.class))).thenReturn(mock(TransactionHistory.class));
        when(notificationService.sendTransactionNotification(any())).thenReturn(false);

      
        DepositUseCase.TransactionResult result = transactionService.processDeposit(command);

        
        assertThat(result).isNotNull();
        assertThat(result.transaction()).isNotNull();
        assertThat(result.notificationSent()).isFalse();

        verify(transactionRepository).save(any(Transaction.class));
        verify(historyRepository).save(any(TransactionHistory.class));
    }

    @Test
    @DisplayName("Should fail when deposit command has null target account")
    void shouldFailWhenDepositCommandHasNullTargetAccount() {
        
        assertThatThrownBy(() -> new DepositUseCase.DepositCommand(
                null,
                amount,
                "Test"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target account cannot be null");
    }

    @Test
    @DisplayName("Should fail when withdrawal command has null source account")
    void shouldFailWhenWithdrawalCommandHasNullSourceAccount() {
        
        assertThatThrownBy(() -> new WithdrawalUseCase.WithdrawalCommand(
                null,
                amount,
                "Test"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Source account cannot be null");
    }

    @Test
    @DisplayName("Should fail when transfer command has same source and target")
    void shouldFailWhenTransferCommandHasSameSourceAndTarget() {
       
        assertThatThrownBy(() -> new TransferUseCase.TransferCommand(
                sourceAccountId,
                sourceAccountId,
                amount,
                "Test"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Source and target accounts cannot be the same");
    }
}
