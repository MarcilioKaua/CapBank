package com.capbank.transaction_service.infrastructure.mapper;

import com.capbank.transaction_service.core.application.port.in.CreateTransactionHistoryUseCase.CreateTransactionHistoryCommand;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.TransactionHistoryPage;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import com.capbank.transaction_service.infrastructure.dto.CreateTransactionHistoryRequest;
import com.capbank.transaction_service.infrastructure.dto.TransactionHistoryPageResponse;
import com.capbank.transaction_service.infrastructure.dto.TransactionHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TransactionHistoryMapper {

    public CreateTransactionHistoryCommand toCommand(CreateTransactionHistoryRequest request) {
        return new CreateTransactionHistoryCommand(
                new AccountId(request.accountId()),
                new TransactionId(request.transactionId()),
                new Money(request.balanceBefore()),
                new Money(request.transactionAmount()),
                request.transactionType(),
                request.description()
        );
    }

    public TransactionHistoryResponse toResponse(TransactionHistory transactionHistory) {
        return new TransactionHistoryResponse(
                transactionHistory.getId().toString(),
                transactionHistory.getAccountId().toString(),
                transactionHistory.getTransactionId().toString(),
                transactionHistory.getBalanceBefore().getAmount(),
                transactionHistory.getBalanceAfter().getAmount(),
                transactionHistory.getTransactionAmount().getAmount(),
                transactionHistory.getTransactionType(),
                transactionHistory.getStatus(),
                transactionHistory.getDescription(),
                transactionHistory.getRecordDate()
        );
    }


    public List<TransactionHistoryResponse> toResponseList(List<TransactionHistory> transactionHistories) {
        return transactionHistories.stream()
                .map(this::toResponse)
                .toList();
    }


    public TransactionHistoryPageResponse toPageResponse(TransactionHistoryPage page) {
        return new TransactionHistoryPageResponse(
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