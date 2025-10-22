package com.capbank.transaction_service.unit;

import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade Transaction
 * Testa as regras de negócio e validações do domínio
 */
@DisplayName("Transaction Domain Tests")
class TransactionTest {

    @Test
    @DisplayName("Should create deposit transaction successfully")
    void shouldCreateDepositTransactionSuccessfully() {
       
        AccountId targetAccount = AccountId.generate();
        Money amount = new Money("500.00");
        String description = "Test deposit";

        Transaction transaction = Transaction.createDeposit(targetAccount, amount, description);

        assertNotNull(transaction);
        assertNull(transaction.getSourceAccountId());
        assertEquals(targetAccount, transaction.getTargetAccountId());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    @DisplayName("Should create withdrawal transaction successfully")
    void shouldCreateWithdrawalTransactionSuccessfully() {
     
        AccountId sourceAccount = AccountId.generate();
        Money amount = new Money("200.00");
        String description = "ATM withdrawal";

        Transaction transaction = Transaction.createWithdrawal(sourceAccount, amount, description);

        assertNotNull(transaction);
        assertEquals(sourceAccount, transaction.getSourceAccountId());
        assertNull(transaction.getTargetAccountId());
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    @DisplayName("Should create transfer transaction successfully")
    void shouldCreateTransferTransactionSuccessfully() {
       
        AccountId sourceAccount = AccountId.generate();
        AccountId targetAccount = AccountId.generate();
        Money amount = new Money("1000.00");
        String description = "Transfer to savings";

       
        Transaction transaction = Transaction.createTransfer(sourceAccount, targetAccount, amount, description);

        assertNotNull(transaction);
        assertEquals(sourceAccount, transaction.getSourceAccountId());
        assertEquals(targetAccount, transaction.getTargetAccountId());
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when deposit has no target account")
    void shouldThrowExceptionWhenDepositHasNoTargetAccount() {
   
        Money amount = new Money("500.00");

        
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction.Builder()
                        .type(TransactionType.DEPOSIT)
                        .amount(amount)
                        .build()
        );
    }

    @Test
    @DisplayName("Should throw exception when withdrawal has no source account")
    void shouldThrowExceptionWhenWithdrawalHasNoSourceAccount() {
      
        Money amount = new Money("200.00");

       
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction.Builder()
                        .type(TransactionType.WITHDRAWAL)
                        .amount(amount)
                        .build()
        );
    }

    @Test
    @DisplayName("Should throw exception when transfer has same source and target accounts")
    void shouldThrowExceptionWhenTransferHasSameAccounts() {
  
        AccountId sameAccount = AccountId.generate();
        Money amount = new Money("1000.00");

        assertThrows(IllegalArgumentException.class, () ->
                new Transaction.Builder()
                        .sourceAccountId(sameAccount)
                        .targetAccountId(sameAccount)
                        .type(TransactionType.TRANSFER)
                        .amount(amount)
                        .build()
        );
    }

    @Test
    @DisplayName("Should update transaction status successfully")
    void shouldUpdateTransactionStatusSuccessfully() {
     
        AccountId targetAccount = AccountId.generate();
        Money amount = new Money("300.00");
        Transaction transaction = new Transaction.Builder()
                .targetAccountId(targetAccount)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .build();

        transaction.updateStatus(TransactionStatus.SUCCESS);

        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when trying to change successful transaction status")
    void shouldThrowExceptionWhenChangingSuccessfulTransactionStatus() {
    
        AccountId targetAccount = AccountId.generate();
        Money amount = new Money("300.00");
        Transaction transaction = Transaction.createDeposit(targetAccount, amount, "Test");

        assertThrows(IllegalArgumentException.class, () ->
                transaction.updateStatus(TransactionStatus.FAILED)
        );
    }

    @Test
    @DisplayName("Should correctly identify if transaction involves account")
    void shouldCorrectlyIdentifyIfTransactionInvolvesAccount() {
 
        AccountId sourceAccount = AccountId.generate();
        AccountId targetAccount = AccountId.generate();
        AccountId otherAccount = AccountId.generate();
        Transaction transaction = Transaction.createTransfer(sourceAccount, targetAccount, new Money("500.00"), "Test");

        assertTrue(transaction.involvesAccount(sourceAccount));
        assertTrue(transaction.involvesAccount(targetAccount));
        assertFalse(transaction.involvesAccount(otherAccount));
    }

    @Test
    @DisplayName("Should return correct primary account for each transaction type")
    void shouldReturnCorrectPrimaryAccountForEachTransactionType() {
   
        AccountId sourceAccount = AccountId.generate();
        AccountId targetAccount = AccountId.generate();
        Money amount = new Money("100.00");

        Transaction deposit = Transaction.createDeposit(targetAccount, amount, "Deposit");
        Transaction withdrawal = Transaction.createWithdrawal(sourceAccount, amount, "Withdrawal");
        Transaction transfer = Transaction.createTransfer(sourceAccount, targetAccount, amount, "Transfer");


        assertEquals(targetAccount, deposit.getPrimaryAccountId());
        assertEquals(sourceAccount, withdrawal.getPrimaryAccountId());
        assertEquals(sourceAccount, transfer.getPrimaryAccountId()); // Source é considerado primário para transfers
    }
}