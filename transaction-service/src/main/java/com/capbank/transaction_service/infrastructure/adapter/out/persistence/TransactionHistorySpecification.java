package com.capbank.transaction_service.infrastructure.adapter.out.persistence;

import com.capbank.transaction_service.core.domain.enums.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionHistorySpecification {

    public static Specification<TransactionHistoryJpaEntity> withFilters(
            UUID accountId,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
   
            if (accountId != null) {
                predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
            }

            if (transactionType != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), transactionType));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recordDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("recordDate"), endDate));
            }

            query.orderBy(criteriaBuilder.desc(root.get("recordDate")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<TransactionHistoryJpaEntity> byAccountId(UUID accountId) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("recordDate")));
            return criteriaBuilder.equal(root.get("accountId"), accountId);
        };
    }
}