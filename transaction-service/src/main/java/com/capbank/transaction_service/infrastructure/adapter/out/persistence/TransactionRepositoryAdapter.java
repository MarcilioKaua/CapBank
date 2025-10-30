package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.FindTransactionQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionUseCase.TransactionPage;
import com.capbank.transaction_service.core.application.port.out.TransactionRepositoryPort;
import com.capbank.transaction_service.core.domain.entity.Transaction;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.TransactionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    public TransactionRepositoryAdapter(
            TransactionJpaRepository jpaRepository,
            TransactionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity jpaEntity = mapper.toJpaEntity(transaction);
        TransactionJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(TransactionId transactionId) {
        return jpaRepository.findById(transactionId.getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Transaction> findByAccount(AccountId accountId) {
        List<TransactionJpaEntity> jpaEntities =
                jpaRepository.findByAccountId(accountId.getValue());
        return mapper.toDomainEntityList(jpaEntities);
    }

    @Override
    public TransactionPage findByAccountWithFilters(FindTransactionQuery query) {
        Pageable pageable = createPageable(query);

        
        var specification = TransactionSpecification.withFiltersForAccount(
                query.accountId().getValue(),
                query.transactionType(),
                query.transactionStatus(),
                query.startDate(),
                query.endDate()
        );

        Page<TransactionJpaEntity> jpaPage = jpaRepository.findAll(specification, pageable);

        return mapper.toDomainPage(jpaPage);
    }

    @Override
    public Transaction update(Transaction transaction) {
       
        if (!jpaRepository.existsById(transaction.getId().getValue())) {
            throw new IllegalArgumentException("Transaction not found: " + transaction.getId());
        }

        TransactionJpaEntity jpaEntity = mapper.toJpaEntity(transaction);
        TransactionJpaEntity updatedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(updatedEntity);
    }

    @Override
    public long countByAccount(AccountId accountId) {
        return jpaRepository.countByAccountId(accountId.getValue());
    }

    @Override
    public boolean exists(TransactionId transactionId) {
        return jpaRepository.existsById(transactionId.getValue());
    }

    private Pageable createPageable(FindTransactionQuery query) {
        Sort.Direction direction = Sort.Direction.fromString(query.sortDirection());
        Sort sort = Sort.by(direction, query.sortBy());
        return PageRequest.of(query.page(), query.size(), sort);
    }
}