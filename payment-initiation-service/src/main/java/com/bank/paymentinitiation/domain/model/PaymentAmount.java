package com.bank.paymentinitiation.domain.model;

import lombok.Value;
import java.math.BigDecimal;

/**
 * Payment Amount Value Object.
 * 
 * <p>Represents the amount and currency of a payment.
 * 
 * <p>This is a value object that enforces the business rule that
 * payment amounts must be greater than zero.
 * 
 * <p>Uses a static factory method to ensure validation:
 * <pre>{@code
 * PaymentAmount amount = PaymentAmount.of(new BigDecimal("150.75"), "USD");
 * }</pre>
 */
@Value
public class PaymentAmount {

    BigDecimal value;
    String currency;

    private PaymentAmount(final BigDecimal value, final String currency) {
        this.value = value;
        this.currency = currency;
    }

    /**
     * Creates a PaymentAmount using a static factory method.
     * 
     * <p>Validates that:
     * <ul>
     *   <li>value is not null</li>
     *   <li>value is greater than zero</li>
     *   <li>currency is not null or empty</li>
     * </ul>
     * 
     * @param value the payment amount (must be > 0)
     * @param currency the currency code (must not be null or empty)
     * @return a validated PaymentAmount instance
     * @throws IllegalArgumentException if validation fails
     */
    public static PaymentAmount of(final BigDecimal value, final String currency) {
        if (value == null) {
            throw new IllegalArgumentException("Payment amount value must not be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must not be null or empty");
        }
        return new PaymentAmount(value, currency.trim().toUpperCase());
    }
}

