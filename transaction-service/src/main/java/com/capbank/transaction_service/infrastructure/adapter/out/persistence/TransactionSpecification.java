package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.domain.enums.TransactionStatus;
import com.capbank.transaction_service.core.domain.enums.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TransactionSpecification {


    public static Specification<TransactionJpaEntity> withFiltersForAccount(
            UUID accountId,
            TransactionType transactionType,
            TransactionStatus transactionStatus,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (accountId != null) {
                Predicate sourceAccount = criteriaBuilder.equal(root.get("sourceAccountId"), accountId);
                Predicate targetAccount = criteriaBuilder.equal(root.get("targetAccountId"), accountId);
                predicates.add(criteriaBuilder.or(sourceAccount, targetAccount));
            }

            if (transactionType != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), transactionType));
            }

            if (transactionStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), transactionStatus));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }

            query.orderBy(criteriaBuilder.desc(root.get("transactionDate")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<TransactionJpaEntity> byAccountId(UUID accountId) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("transactionDate")));

            Predicate sourceAccount = criteriaBuilder.equal(root.get("sourceAccountId"), accountId);
            Predicate targetAccount = criteriaBuilder.equal(root.get("targetAccountId"), accountId);

            return criteriaBuilder.or(sourceAccount, targetAccount);
        };
    }

    public static Specification<TransactionJpaEntity> byStatus(TransactionStatus status) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("transactionDate")));
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}