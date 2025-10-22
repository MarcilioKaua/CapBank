package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionHistoryJpaRepository extends JpaRepository<TransactionHistoryJpaEntity, UUID>, JpaSpecificationExecutor<TransactionHistoryJpaEntity> {

    
    List<TransactionHistoryJpaEntity> findByAccountIdOrderByRecordDateDesc(UUID accountId);

    Optional<TransactionHistoryJpaEntity> findFirstByAccountIdOrderByRecordDateDesc(UUID accountId);

    boolean existsByTransactionId(UUID transactionId);

    long countByAccountId(UUID accountId);
}