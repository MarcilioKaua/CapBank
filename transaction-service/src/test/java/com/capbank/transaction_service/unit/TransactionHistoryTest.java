package com.capbank.transaction_service.unit;

import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade TransactionHistory
 * Testa as regras de negócio e validações do domínio
 */
@DisplayName("TransactionHistory Domain Tests")
class TransactionHistoryTest {

    @Test
    @DisplayName("Should create deposit history with correct balance calculation")
    void shouldCreateDepositHistoryWithCorrectBalance() {
      
        AccountId accountId = AccountId.generate();
        TransactionId transactionId = TransactionId.generate();
        Money balanceBefore = new Money("1000.00");
        Money depositAmount = new Money("500.00");
        String description = "Deposit via ATM";

        
        TransactionHistory history = TransactionHistory.createDepositHistory(
                accountId, transactionId, balanceBefore, depositAmount, description);

       
        assertNotNull(history);
        assertEquals(accountId, history.getAccountId());
        assertEquals(transactionId, history.getTransactionId());
        assertEquals(balanceBefore, history.getBalanceBefore());
        assertEquals(new Money("1500.00"), history.getBalanceAfter());
        assertEquals(depositAmount, history.getTransactionAmount());
        assertEquals(TransactionType.DEPOSIT, history.getTransactionType());
        assertEquals(TransactionStatus.SUCCESS, history.getStatus());
        assertEquals(description, history.getDescription());
    }

    @Test
    @DisplayName("Should create withdrawal history with correct balance calculation")
    void shouldCreateWithdrawalHistoryWithCorrectBalance() {
        
        AccountId accountId = AccountId.generate();
        TransactionId transactionId = TransactionId.generate();
        Money balanceBefore = new Money("1000.00");
        Money withdrawalAmount = new Money("300.00");
        String description = "ATM Withdrawal";

      
        TransactionHistory history = TransactionHistory.createWithdrawalHistory(
                accountId, transactionId, balanceBefore, withdrawalAmount, description);

       
        assertNotNull(history);
        assertEquals(new Money("700.00"), history.getBalanceAfter());
        assertEquals(TransactionType.WITHDRAWAL, history.getTransactionType());
    }

    @Test
    @DisplayName("Should create transfer history with correct balance calculation")
    void shouldCreateTransferHistoryWithCorrectBalance() {
      
        AccountId accountId = AccountId.generate();
        TransactionId transactionId = TransactionId.generate();
        Money balanceBefore = new Money("2000.00");
        Money transferAmount = new Money("800.00");
        String description = "Transfer to savings";

        
        TransactionHistory history = TransactionHistory.createTransferHistory(
                accountId, transactionId, balanceBefore, transferAmount, description);

        
        assertNotNull(history);
        assertEquals(new Money("1200.00"), history.getBalanceAfter());
        assertEquals(TransactionType.TRANSFER, history.getTransactionType());
    }

    @Test
    @DisplayName("Should throw exception when building with null required fields")
    void shouldThrowExceptionWhenBuildingWithNullRequiredFields() {
      
        TransactionHistory.Builder builder = new TransactionHistory.Builder();

        assertThrows(NullPointerException.class, () -> builder.build());
    }

    @Test
    @DisplayName("Should throw exception when balance calculation is incorrect")
    void shouldThrowExceptionWhenBalanceCalculationIsIncorrect() {

        AccountId accountId = AccountId.generate();
        TransactionId transactionId = TransactionId.generate();
        Money balanceBefore = new Money("1000.00");
        Money incorrectBalanceAfter = new Money("2000.00"); // Wrong calculation
        Money transactionAmount = new Money("500.00");

        assertThrows(IllegalArgumentException.class, () ->
                new TransactionHistory.Builder()
                        .accountId(accountId)
                        .transactionId(transactionId)
                        .balanceBefore(balanceBefore)
                        .balanceAfter(incorrectBalanceAfter)
                        .transactionAmount(transactionAmount)
                        .transactionType(TransactionType.DEPOSIT)
                        .status(TransactionStatus.SUCCESS)
                        .build()
        );
    }

    @Test
    @DisplayName("Should generate unique IDs for different transactions")
    void shouldGenerateUniqueIdsForDifferentTransactions() {
     
        AccountId accountId = AccountId.generate();
        TransactionId transactionId1 = TransactionId.generate();
        TransactionId transactionId2 = TransactionId.generate();
        Money balance = new Money("1000.00");
        Money amount = new Money("100.00");

        
        TransactionHistory history1 = TransactionHistory.createDepositHistory(
                accountId, transactionId1, balance, amount, "First deposit");
        TransactionHistory history2 = TransactionHistory.createDepositHistory(
                accountId, transactionId2, balance, amount, "Second deposit");

      
        assertNotEquals(history1.getId(), history2.getId());
        assertNotEquals(history1.getTransactionId(), history2.getTransactionId());
    }
}