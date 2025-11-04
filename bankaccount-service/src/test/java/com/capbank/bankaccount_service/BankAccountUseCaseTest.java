package com.capbank.bankaccount_service;

import com.capbank.bankaccount_service.core.application.ports.out.BankAccountRepositoryPort;
import com.capbank.bankaccount_service.core.application.service.BankAccountServiceImpl;
import com.capbank.bankaccount_service.core.domain.enums.AccountStatus;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.dto.BankAccountResponseDTO;
import com.capbank.bankaccount_service.infra.exception.BankAccountNotFoundException;
import com.capbank.bankaccount_service.infra.mapper.BankAccountMapper;
import com.capbank.bankaccount_service.infra.mapper.BankAccountResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountUseCaseTest {

    @Mock private BankAccountRepositoryPort bankAccountRepository;

    @Mock private BankAccountMapper bankAccountMapper;

    @Mock private BankAccountResponseMapper bankAccountResponseMapper;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Test
    @DisplayName("Deve criar conta bancária com sucesso quando dados são válidos")
    void shouldCreateBankAccountSuccessfully() {
        BankAccount newAccount = new BankAccount();
        newAccount.setBalance(BigDecimal.ZERO);

        BankAccount savedAccount = new BankAccount();
        savedAccount.setId(UUID.randomUUID());
        savedAccount.setCreatedAt(LocalDateTime.now());
        savedAccount.setStatus(AccountStatus.ACTIVE);

        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savedAccount);

        BankAccount result = bankAccountService.create(newAccount);

        assertNotNull(result.getId());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("Deve encontrar conta bancária quando o ID existir")
    void shouldFindBankAccountById() {
        UUID id = UUID.randomUUID();
        BankAccount account = new BankAccount();
        account.setId(id);

        when(bankAccountRepository.findById(id)).thenReturn(Optional.of(account));

        BankAccount found = bankAccountService.findById(id);

        assertEquals(id, found.getId());
        verify(bankAccountRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta bancária não é encontrada por ID")
    void shouldThrowWhenAccountNotFoundById() {
        UUID id = UUID.randomUUID();
        when(bankAccountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.findById(id));
    }

    @Test
    @DisplayName("Deve atualizar saldo com sucesso quando conta existe")
    void shouldUpdateBalanceSuccessfully() {
        String accountNumber = "12345";
        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.valueOf(100));

        BankAccountResponseDTO responseDTO = new BankAccountResponseDTO();
        responseDTO.setAccountNumber(accountNumber);
        responseDTO.setBalance(BigDecimal.valueOf(200));

        when(bankAccountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(account)).thenReturn(account);
        when(bankAccountResponseMapper.toResponse(account)).thenReturn(responseDTO);

        BankAccountResponseDTO result = bankAccountService.updateBalance(accountNumber, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(200), result.getBalance());
        verify(bankAccountRepository).save(account);
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta não existe para atualização de saldo")
    void shouldThrowWhenAccountNotFoundOnUpdateBalance() {
        when(bankAccountRepository.findByAccountNumber("999")).thenReturn(Optional.empty());
        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.updateBalance("999", BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve deletar conta bancária com sucesso")
    void shouldDeleteBankAccount() {
        UUID id = UUID.randomUUID();
        doNothing().when(bankAccountRepository).deleteById(id);

        bankAccountService.delete(id);

        verify(bankAccountRepository).deleteById(id);
    }
}