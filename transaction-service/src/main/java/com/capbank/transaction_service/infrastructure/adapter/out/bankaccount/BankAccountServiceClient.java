package com.capbank.transaction_service.infrastructure.adapter.out.bankaccount;

import com.capbank.transaction_service.core.application.port.out.BankAccountServicePort;
import com.capbank.transaction_service.core.domain.valueobject.AccountId;
import com.capbank.transaction_service.core.domain.valueobject.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class BankAccountServiceClient implements BankAccountServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceClient.class);

    private final RestTemplate restTemplate;
    private final String bankAccountServiceUrl;

    public BankAccountServiceClient(
            RestTemplate restTemplate,
            @Value("${services.bankaccount.url:http://localhost:8084}") String bankAccountServiceUrl) {
        this.restTemplate = restTemplate;
        this.bankAccountServiceUrl = bankAccountServiceUrl;
    }

    @Override
    public void updateBalance(AccountId accountId, Money amount, BalanceOperation operation) {
        try {
            logger.info("Updating balance for account: {}, amount: {}, operation: {}",
                       accountId, amount, operation);

            String getAccountUrl = bankAccountServiceUrl + "/api/bankaccount/" + accountId.toString();
            ResponseEntity<BankAccountDto> accountResponse = restTemplate.getForEntity(
                    getAccountUrl,
                    BankAccountDto.class
            );

            if (accountResponse.getStatusCode() != HttpStatus.OK || accountResponse.getBody() == null) {
                throw new RuntimeException("Failed to retrieve account: " + accountId);
            }

            String accountNumber = accountResponse.getBody().accountNumber();
            logger.info("Retrieved account number: {} for account ID: {}", accountNumber, accountId);

            Money currentBalance = getBalance(accountId);
            logger.info("Current balance: {}", currentBalance);

            Money newBalance = switch (operation) {
                case ADD -> currentBalance.add(amount);
                case SUBTRACT -> currentBalance.subtract(amount);
            };

            logger.info("New balance calculated: {}", newBalance);

            String updateBalanceUrl = bankAccountServiceUrl +
                    "/api/bankaccount/" + accountNumber + "/balance?newBalance=" + newBalance.getAmount();

            restTemplate.put(updateBalanceUrl, null);

            logger.info("Balance updated successfully for account: {}", accountId);

        } catch (Exception e) {
            logger.error("Error updating balance for account {}: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("Failed to update account balance: " + e.getMessage(), e);
        }
    }

    @Override
    public Money getBalance(AccountId accountId) {
        try {
            logger.info("Getting balance for account: {}", accountId);

            String getAccountUrl = bankAccountServiceUrl + "/api/bankaccount/" + accountId.toString();
            ResponseEntity<BankAccountDto> accountResponse = restTemplate.getForEntity(
                    getAccountUrl,
                    BankAccountDto.class
            );

            if (accountResponse.getStatusCode() != HttpStatus.OK || accountResponse.getBody() == null) {
                throw new RuntimeException("Failed to retrieve account: " + accountId);
            }

            String accountNumber = accountResponse.getBody().accountNumber();

            String getBalanceUrl = bankAccountServiceUrl + "/api/bankaccount/" + accountNumber + "/balance";
            ResponseEntity<BigDecimal> balanceResponse = restTemplate.getForEntity(
                    getBalanceUrl,
                    BigDecimal.class
            );

            if (balanceResponse.getStatusCode() != HttpStatus.OK || balanceResponse.getBody() == null) {
                throw new RuntimeException("Failed to retrieve balance for account: " + accountId);
            }

            Money balance = new Money(balanceResponse.getBody());
            logger.info("Retrieved balance: {} for account: {}", balance, accountId);

            return balance;

        } catch (Exception e) {
            logger.error("Error getting balance for account {}: {}", accountId, e.getMessage(), e);
            throw new RuntimeException("Failed to get account balance: " + e.getMessage(), e);
        }
    }

    record BankAccountDto(
            String id,
            String accountNumber,
            String agency,
            BigDecimal balance,
            String accountType,
            String accountStatus,
            String userId
    ) {}
}
