package com.capbank.transaction_service.unit;

import com.capbank.transaction_service.core.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Value Object Money
 * Testa operações monetárias e validações
 */
@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create money with correct precision")
    void shouldCreateMoneyWithCorrectPrecision() {
       
        Money money = new Money("123.456");

     
        assertEquals(new BigDecimal("123.46"), money.getAmount());
    }

    @Test
    @DisplayName("Should add money correctly")
    void shouldAddMoneyCorrectly() {
      
        Money money1 = new Money("100.50");
        Money money2 = new Money("50.25");

      
        Money result = money1.add(money2);

      
        assertEquals(new Money("150.75"), result);
    }

    @Test
    @DisplayName("Should subtract money correctly")
    void shouldSubtractMoneyCorrectly() {
       
        Money money1 = new Money("100.50");
        Money money2 = new Money("25.25");

        
        Money result = money1.subtract(money2);

        
        assertEquals(new Money("75.25"), result);
    }

    @Test
    @DisplayName("Should compare money amounts correctly")
    void shouldCompareMoneyAmountsCorrectly() {
        
        Money money1 = new Money("100.00");
        Money money2 = new Money("50.00");
        Money money3 = new Money("100.00");

        
        assertTrue(money1.isGreaterThan(money2));
        assertTrue(money2.isLessThan(money1));
        assertTrue(money1.isEqualTo(money3));
    }

    @Test
    @DisplayName("Should throw exception for null amount")
    void shouldThrowExceptionForNullAmount() {
        
        assertThrows(IllegalArgumentException.class, () -> new Money((BigDecimal) null));
    }

    @Test
    @DisplayName("Should throw exception for negative amount")
    void shouldThrowExceptionForNegativeAmount() {
       
        assertThrows(IllegalArgumentException.class, () -> new Money("-10.00"));
    }

    @Test
    @DisplayName("Should maintain equality contract")
    void shouldMaintainEqualityContract() {
        
        Money money1 = new Money("100.00");
        Money money2 = new Money("100.00");
        Money money3 = new Money("50.00");

        
        assertEquals(money1, money2);
        assertNotEquals(money1, money3);
        assertEquals(money1.hashCode(), money2.hashCode());
    }
}