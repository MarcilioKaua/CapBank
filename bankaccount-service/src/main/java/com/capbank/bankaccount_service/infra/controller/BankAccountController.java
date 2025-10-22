package com.capbank.bankaccount_service.infra.controller;

import com.capbank.bankaccount_service.core.application.ports.in.BankAccountUseCase;
import com.capbank.bankaccount_service.core.domain.model.BankAccount;
import com.capbank.bankaccount_service.infra.dto.BankAccountRequestDTO;
import com.capbank.bankaccount_service.infra.dto.BankAccountResponseDTO;
import com.capbank.bankaccount_service.infra.mapper.BankAccountRequestMapper;
import com.capbank.bankaccount_service.infra.mapper.BankAccountResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bankaccount")
public class BankAccountController {

    private final BankAccountUseCase bankAccountUseCase;
    private final BankAccountRequestMapper bankAccountRequestMapper;
    private final BankAccountResponseMapper bankAccountResponseMapper;

    public BankAccountController(
            BankAccountUseCase bankAccountUseCase,
            BankAccountRequestMapper bankAccountRequestMapper,
            BankAccountResponseMapper bankAccountResponseMapper
    ) {
        this.bankAccountUseCase = bankAccountUseCase;
        this.bankAccountRequestMapper = bankAccountRequestMapper;
        this.bankAccountResponseMapper = bankAccountResponseMapper;
    }

    @PostMapping
    public BankAccountResponseDTO create(@RequestBody BankAccountRequestDTO request) {
        BankAccount account = bankAccountRequestMapper.toDomain(request);
        var created = bankAccountUseCase.create(account);
        return bankAccountResponseMapper.toResponse(created);
    }

    @GetMapping
    public List<BankAccountResponseDTO> findAll() {
        return bankAccountUseCase.findAll().stream().map(bankAccountResponseMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public BankAccountResponseDTO findById(@PathVariable UUID id) {
        return bankAccountResponseMapper.toResponse(bankAccountUseCase.findById(id));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<BankAccountResponseDTO> getByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(bankAccountUseCase.findByAccountNumber(accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(bankAccountUseCase.getBalance(accountNumber));
    }

    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<BankAccountResponseDTO> updateBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal newBalance
    ) {
        return ResponseEntity.ok(bankAccountUseCase.updateBalance(accountNumber, newBalance));
    }

    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<BankAccountResponseDTO> updateStatus(
            @PathVariable String accountNumber,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(bankAccountUseCase.updateStatus(accountNumber, status));
    }

    @PutMapping("/{id}")
    public BankAccountResponseDTO update(@PathVariable UUID id, @RequestBody BankAccountRequestDTO request) {
        BankAccount account = bankAccountRequestMapper.toDomain(request);
        var updated = bankAccountUseCase.update(id, account);
        return bankAccountResponseMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        bankAccountUseCase.delete(id);
    }
}
