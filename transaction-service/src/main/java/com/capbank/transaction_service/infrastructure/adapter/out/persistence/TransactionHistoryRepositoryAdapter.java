package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.FindTransactionHistoryQuery;
import com.capbank.transaction_service.core.application.port.in.FindTransactionHistoryUseCase.TransactionHistoryPage;
import com.capbank.transaction_service.core.application.port.out.TransactionHistoryRepositoryPort;
import com.capbank.transaction_service.core.domain.entity.TransactionHistory;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class TransactionHistoryRepositoryAdapter implements TransactionHistoryRepositoryPort {

    private final TransactionHistoryJpaRepository jpaRepository;
    private final TransactionHistoryPersistenceMapper mapper;

    public TransactionHistoryRepositoryAdapter(
            TransactionHistoryJpaRepository jpaRepository,
            TransactionHistoryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TransactionHistory save(TransactionHistory transactionHistory) {
        TransactionHistoryJpaEntity jpaEntity = mapper.toJpaEntity(transactionHistory);
        TransactionHistoryJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<TransactionHistory> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<TransactionHistory> findByAccountId(AccountId accountId) {
        List<TransactionHistoryJpaEntity> jpaEntities =
                jpaRepository.findByAccountIdOrderByRecordDateDesc(accountId.getValue());
        return mapper.toDomainEntityList(jpaEntities);
    }

    @Override
    public TransactionHistoryPage findByAccountIdWithFilters(FindTransactionHistoryQuery query) {
        Pageable pageable = createPageable(query);

        var specification = TransactionHistorySpecification.withFilters(
                query.accountId().getValue(),
                query.transactionType(),
                query.startDate(),
                query.endDate()
        );

        Page<TransactionHistoryJpaEntity> jpaPage = jpaRepository.findAll(specification, pageable);

        return mapper.toDomainPage(jpaPage);
    }

    @Override
    public boolean existsByTransactionId(UUID transactionId) {
        return jpaRepository.existsByTransactionId(transactionId);
    }

    @Override
    public Optional<TransactionHistory> findLatestByAccountId(AccountId accountId) {
        return jpaRepository.findFirstByAccountIdOrderByRecordDateDesc(accountId.getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public long countByAccountId(AccountId accountId) {
        return jpaRepository.countByAccountId(accountId.getValue());
    }

    private Pageable createPageable(FindTransactionHistoryQuery query) {
        Sort.Direction direction = Sort.Direction.fromString(query.sortDirection());
        Sort sort = Sort.by(direction, query.sortBy());
        return PageRequest.of(query.page(), query.size(), sort);
    }
}