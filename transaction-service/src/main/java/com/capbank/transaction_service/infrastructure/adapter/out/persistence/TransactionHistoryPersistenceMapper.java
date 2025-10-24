package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.TransactionHistoryPage;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionHistoryPersistenceMapper {

   
    public TransactionHistoryJpaEntity toJpaEntity(TransactionHistory transactionHistory) {
        return new TransactionHistoryJpaEntity(
                transactionHistory.getId(),
                transactionHistory.getAccountId().getValue(),
                transactionHistory.getTransactionId().getValue(),
                transactionHistory.getBalanceBefore().getAmount(),
                transactionHistory.getBalanceAfter().getAmount(),
                transactionHistory.getTransactionAmount().getAmount(),
                transactionHistory.getTransactionType(),
                transactionHistory.getStatus(),
                transactionHistory.getDescription(),
                transactionHistory.getRecordDate()
        );
    }

    public TransactionHistory toDomainEntity(TransactionHistoryJpaEntity jpaEntity) {
        return new TransactionHistory.Builder()
                .id(jpaEntity.getId())
                .accountId(new AccountId(jpaEntity.getAccountId()))
                .transactionId(new TransactionId(jpaEntity.getTransactionId()))
                .balanceBefore(new Money(jpaEntity.getBalanceBefore()))
                .balanceAfter(new Money(jpaEntity.getBalanceAfter()))
                .transactionAmount(new Money(jpaEntity.getTransactionAmount()))
                .transactionType(jpaEntity.getTransactionType())
                .status(jpaEntity.getStatus())
                .description(jpaEntity.getDescription())
                .recordDate(jpaEntity.getRecordDate())
                .build();
    }

    public List<TransactionHistory> toDomainEntityList(List<TransactionHistoryJpaEntity> jpaEntities) {
        return jpaEntities.stream()
                .map(this::toDomainEntity)
                .toList();
    }


    public TransactionHistoryPage toDomainPage(Page<TransactionHistoryJpaEntity> jpaPage) {
        return new TransactionHistoryPage(
                toDomainEntityList(jpaPage.getContent()),
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages(),
                jpaPage.isFirst(),
                jpaPage.isLast()
        );
    }
}