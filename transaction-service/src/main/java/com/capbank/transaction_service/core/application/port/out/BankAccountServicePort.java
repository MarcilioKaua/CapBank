package com.capbank.transaction_service.core.application.port.out;

import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;

public interface BankAccountServicePort {

    void updateBalance(AccountId accountId, Money amount, BalanceOperation operation);

    Money getBalance(AccountId accountId);

    enum BalanceOperation {
        ADD,
        SUBTRACT
    }

    record BalanceUpdateRequest(
            String accountId,
            String amount,
            String operation
    ) {}

    record BalanceResponse(
            String balance
    ) {}
}
