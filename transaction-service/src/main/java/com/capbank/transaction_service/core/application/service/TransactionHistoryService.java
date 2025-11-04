package com.capbank.transaction_service.core.application.service;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionHistoryUseCase;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase;
import com.capbank.transaction_service.core.application.port.out.TransactionHistoryRepositoryPort;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.enums.TransactionType;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TransactionHistoryService implements CreateTransactionHistoryUseCase, FindTransactionHistoryUseCase {

    private final TransactionHistoryRepositoryPort transactionHistoryRepositoryPort;

    public TransactionHistoryService(TransactionHistoryRepositoryPort transactionHistoryRepositoryPort) {
        this.transactionHistoryRepositoryPort = transactionHistoryRepositoryPort;
    }

    @Override
    public TransactionHistory createTransactionHistory(CreateTransactionHistoryCommand command) {
        // Validar se já existe histórico para esta transação
        if (transactionHistoryRepositoryPort.existsByTransactionId(command.transactionId().getValue())) {
            throw new IllegalArgumentException("Histórico de transação já existe para a transação: " + command.transactionId());
        }

        // Criar o histórico baseado no tipo de transação
        TransactionHistory transactionHistory = createHistoryByType(command);

        // Salvar e retornar
        return transactionHistoryRepositoryPort.save(transactionHistory);
    }

    private TransactionHistory createHistoryByType(CreateTransactionHistoryCommand command) {
        return switch (command.transactionType()) {
            case DEPOSIT -> TransactionHistory.createDepositHistory(
                    command.accountId(),
                    command.transactionId(),
                    command.balanceBefore(),
                    command.transactionAmount(),
                    command.description()
            );
            case WITHDRAWAL -> TransactionHistory.createWithdrawalHistory(
                    command.accountId(),
                    command.transactionId(),
                    command.balanceBefore(),
                    command.transactionAmount(),
                    command.description()
            );
            case TRANSFER -> TransactionHistory.createTransferHistory(
                    command.accountId(),
                    command.transactionId(),
                    command.balanceBefore(),
                    command.transactionAmount(),
                    command.description()
            );
        };
    }

    @Override
    public Optional<TransactionHistory> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return transactionHistoryRepositoryPort.findById(id);
    }

    @Override
    public TransactionHistoryPage findByAccountId(FindTransactionHistoryQuery query) {
        // Validações adicionais podem ser adicionadas aqui
        validateQuery(query);

        // Delegar para o repositório
        return transactionHistoryRepositoryPort.findByAccountIdWithFilters(query);
    }

    private void validateQuery(FindTransactionHistoryQuery query) {
        if (query.size() > 100) {
            throw new IllegalArgumentException("Tamanho da página não pode ser maior que 100");
        }

        // Outras validações de negócio podem ser adicionadas aqui
    }
}