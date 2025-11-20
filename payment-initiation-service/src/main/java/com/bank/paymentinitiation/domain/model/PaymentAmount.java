package com.bank.paymentinitiation.domain.model;

import lombok.Value;

import java.math.BigDecimal;

/**
 * Value object representing a payment amount with currency.
 * 
 * This is an immutable value object that encapsulates amount and currency.
 * It validates that the amount is positive (greater than zero).
 * 
 * Following Domain-Driven Design principles, this value object ensures
 * business invariants are maintained (amount must be positive).
 */
@Value
public class PaymentAmount {
    
    BigDecimal value;
    String currency;
    
    private PaymentAmount(BigDecimal value, String currency) {
        if (value == null) {
            throw new IllegalArgumentException("Amount value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        this.value = value;
        this.currency = currency;
    }
    
    /**
     * Factory method to create a PaymentAmount.
     * 
     * @param value the amount value (must be > 0)
     * @param currency the currency code (must not be null or empty)
     * @return a new PaymentAmount instance
     * @throws IllegalArgumentException if value is null, <= 0, or currency is invalid
     */
    public static PaymentAmount of(BigDecimal value, String currency) {
        return new PaymentAmount(value, currency);
    }
    
    /**
     * Factory method to create a PaymentAmount from double.
     * 
     * @param value the amount value (must be > 0)
     * @param currency the currency code (must not be null or empty)
     * @return a new PaymentAmount instance
     * @throws IllegalArgumentException if value is <= 0 or currency is invalid
     */
    public static PaymentAmount of(double value, String currency) {
        return new PaymentAmount(BigDecimal.valueOf(value), currency);
    }
}

