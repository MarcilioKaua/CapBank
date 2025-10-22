package com.capbank.transaction_service.infrastructure.mapper;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionUseCase.CreateTransactionCommand;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.TransactionPage;
import com.capbank.transaction_service.core.application.port.in.UpdateTransactionStatusUseCase.UpdateStatusCommand;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import com.capbank.transaction_service.infrastructure.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {


    public CreateTransactionCommand toCommand(CreateTransactionRequest request) {
        AccountId sourceAccountId = request.sourceAccountId() != null && !request.sourceAccountId().trim().isEmpty()
                ? new AccountId(request.sourceAccountId())
                : null;

        AccountId targetAccountId = request.targetAccountId() != null && !request.targetAccountId().trim().isEmpty()
                ? new AccountId(request.targetAccountId())
                : null;

        return new CreateTransactionCommand(
                sourceAccountId,
                targetAccountId,
                request.transactionType(),
                new Money(request.amount()),
                request.description()
        );
    }

    public UpdateStatusCommand toUpdateCommand(String transactionId, UpdateTransactionStatusRequest request) {
        return new UpdateStatusCommand(
                new TransactionId(transactionId),
                request.status(),
                request.reason()
        );
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId().toString(),
                transaction.getSourceAccountId() != null ? transaction.getSourceAccountId().toString() : null,
                transaction.getTargetAccountId() != null ? transaction.getTargetAccountId().toString() : null,
                transaction.getType(),
                transaction.getAmount().getAmount(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getTransactionDate()
        );
    }

    public List<TransactionResponse> toResponseList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionPageResponse toPageResponse(TransactionPage page) {
        return new TransactionPageResponse(
                toResponseList(page.content()),
                page.pageNumber(),
                page.pageSize(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last()
        );
    }
}