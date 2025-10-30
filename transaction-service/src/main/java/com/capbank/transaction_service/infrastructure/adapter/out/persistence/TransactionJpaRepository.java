package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID>, JpaSpecificationExecutor<TransactionJpaEntity> {

    List<TransactionJpaEntity> findBySourceAccountIdOrderByTransactionDateDesc(UUID sourceAccountId);

    List<TransactionJpaEntity> findByTargetAccountIdOrderByTransactionDateDesc(UUID targetAccountId);

    @Query("SELECT t FROM TransactionJpaEntity t WHERE t.sourceAccountId = :accountId OR t.targetAccountId = :accountId ORDER BY t.transactionDate DESC")
    List<TransactionJpaEntity> findByAccountId(@Param("accountId") UUID accountId);

 
    @Query("SELECT COUNT(t) FROM TransactionJpaEntity t WHERE t.sourceAccountId = :accountId OR t.targetAccountId = :accountId")
    long countByAccountId(@Param("accountId") UUID accountId);

    List<TransactionJpaEntity> findByStatusOrderByTransactionDateDesc(TransactionStatus status);

    List<TransactionJpaEntity> findByTransactionTypeOrderByTransactionDateDesc(TransactionType transactionType);
}