package com.capbank.transaction_service.unit;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionHistoryUseCase.CreateTransactionHistoryCommand;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.FindTransactionHistoryQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.TransactionHistoryPage;
import com.capbank.transaction_service.core.application.port.out.TransactionHistoryRepositoryPort;
import com.capbank.transaction_service.core.application.service.TransactionHistoryService;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o serviço TransactionHistoryService
 * Testa a lógica de aplicação e interação com os ports
 */
@DisplayName("TransactionHistoryService Tests")
@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

    @Mock
    private TransactionHistoryRepositoryPort repositoryPort;

    private TransactionHistoryService service;

    @BeforeEach
    void setUp() {
        service = new TransactionHistoryService(repositoryPort);
    }

    @Test
    @DisplayName("Should create transaction history successfully")
    void shouldCreateTransactionHistorySuccessfully() {
       
        AccountId accountId = AccountId.generate();
        TransactionId transactionId = TransactionId.generate();
        Money balanceBefore = new Money("1000.00");
        Money amount = new Money("500.00");

        CreateTransactionHistoryCommand command = new CreateTransactionHistoryCommand(
                accountId, transactionId, balanceBefore, amount, TransactionType.DEPOSIT, "Test deposit");

        TransactionHistory expectedHistory = TransactionHistory.createDepositHistory(
                accountId, transactionId, balanceBefore, amount, "Test deposit");

        when(repositoryPort.existsByTransactionId(transactionId.getValue())).thenReturn(false);
        when(repositoryPort.save(any(TransactionHistory.class))).thenReturn(expectedHistory);

        
        TransactionHistory result = service.createTransactionHistory(command);

      
        assertNotNull(result);
        assertEquals(expectedHistory.getAccountId(), result.getAccountId());
        assertEquals(expectedHistory.getTransactionId(), result.getTransactionId());
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        verify(repositoryPort).existsByTransactionId(transactionId.getValue());
        verify(repositoryPort).save(any(TransactionHistory.class));
    }

    @Test
    @DisplayName("Should throw exception when transaction history already exists")
    void shouldThrowExceptionWhenTransactionHistoryAlreadyExists() {
        
        AccountId accountId = AccountId.generate();
        TransactionId transactionId = TransactionId.generate();
        Money balanceBefore = new Money("1000.00");
        Money amount = new Money("500.00");

        CreateTransactionHistoryCommand command = new CreateTransactionHistoryCommand(
                accountId, transactionId, balanceBefore, amount, TransactionType.DEPOSIT, "Test deposit");

        when(repositoryPort.existsByTransactionId(transactionId.getValue())).thenReturn(true);

       
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createTransactionHistory(command));

        assertTrue(exception.getMessage().contains("Transaction history already exists"));
        verify(repositoryPort).existsByTransactionId(transactionId.getValue());
        verify(repositoryPort, never()).save(any(TransactionHistory.class));
    }

    @Test
    @DisplayName("Should find transaction history by ID")
    void shouldFindTransactionHistoryById() {
       
        UUID historyId = UUID.randomUUID();
        TransactionHistory expectedHistory = TransactionHistory.createDepositHistory(
                AccountId.generate(),
                TransactionId.generate(),
                new Money("1000.00"),
                new Money("500.00"),
                "Test"
        );

        when(repositoryPort.findById(historyId)).thenReturn(Optional.of(expectedHistory));

     
        Optional<TransactionHistory> result = service.findById(historyId);

       
        assertTrue(result.isPresent());
        assertEquals(expectedHistory, result.get());
        verify(repositoryPort).findById(historyId);
    }

    @Test
    @DisplayName("Should find transaction history by account ID with pagination")
    void shouldFindTransactionHistoryByAccountIdWithPagination() {
        
        AccountId accountId = AccountId.generate();
        FindTransactionHistoryQuery query = FindTransactionHistoryQuery.create(accountId, 0, 20);

        TransactionHistory history1 = TransactionHistory.createDepositHistory(
                accountId, TransactionId.generate(), new Money("1000.00"), new Money("500.00"), "Deposit 1");
        TransactionHistory history2 = TransactionHistory.createWithdrawalHistory(
                accountId, TransactionId.generate(), new Money("1500.00"), new Money("200.00"), "Withdrawal 1");

        TransactionHistoryPage expectedPage = new TransactionHistoryPage(
                List.of(history1, history2), 0, 20, 2, 1, true, true);

        when(repositoryPort.findByAccountIdWithFilters(query)).thenReturn(expectedPage);

        
        TransactionHistoryPage result = service.findByAccountId(query);

        
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(0, result.pageNumber());
        assertEquals(20, result.pageSize());
        verify(repositoryPort).findByAccountIdWithFilters(query);
    }

    @Test
    @DisplayName("Should throw exception for large page size")
    void shouldThrowExceptionForLargePageSize() {
        
        AccountId accountId = AccountId.generate();
        FindTransactionHistoryQuery query = new FindTransactionHistoryQuery(
                accountId, null, null, null, 0, 150, "recordDate", "DESC");

       
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.findByAccountId(query));

        assertTrue(exception.getMessage().contains("Page size cannot be greater than 100"));
        verify(repositoryPort, never()).findByAccountIdWithFilters(any());
    }

    @Test
    @DisplayName("Should throw exception when finding by null ID")
    void shouldThrowExceptionWhenFindingByNullId() {
       
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.findById(null));

        assertEquals("ID cannot be null", exception.getMessage());
        verify(repositoryPort, never()).findById(any());
    }
}