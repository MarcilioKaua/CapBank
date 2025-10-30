package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.TransactionPage;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionPersistenceMapper {

    public TransactionJpaEntity toJpaEntity(Transaction transaction) {
        return new TransactionJpaEntity(
                transaction.getId().getValue(),
                transaction.getSourceAccountId() != null ? transaction.getSourceAccountId().getValue() : null,
                transaction.getTargetAccountId() != null ? transaction.getTargetAccountId().getValue() : null,
                transaction.getType(),
                transaction.getAmount().getAmount().setScale(2),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getStatus()
        );
    }

    public Transaction toDomainEntity(TransactionJpaEntity jpaEntity) {
        return new Transaction.Builder()
                .id(new TransactionId(jpaEntity.getId()))
                .sourceAccountId(jpaEntity.getSourceAccountId() != null ?
                    new AccountId(jpaEntity.getSourceAccountId()) : null)
                .targetAccountId(jpaEntity.getTargetAccountId() != null ?
                    new AccountId(jpaEntity.getTargetAccountId()) : null)
                .type(jpaEntity.getTransactionType())
                .amount(new Money(jpaEntity.getAmount()))
                .description(jpaEntity.getDescription())
                .transactionDate(jpaEntity.getTransactionDate())
                .status(jpaEntity.getStatus())
                .build();
    }

    public List<Transaction> toDomainEntityList(List<TransactionJpaEntity> jpaEntities) {
        return jpaEntities.stream()
                .map(this::toDomainEntity)
                .toList();
    }

    public TransactionPage toDomainPage(Page<TransactionJpaEntity> jpaPage) {
        return new TransactionPage(
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