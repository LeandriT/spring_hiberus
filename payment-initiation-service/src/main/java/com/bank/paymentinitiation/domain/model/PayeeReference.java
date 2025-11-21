package com.bank.paymentinitiation.domain.model;

import lombok.Value;

/**
 * Payee Reference Value Object.
 * 
 * <p>Represents a reference to the payee (creditor) account.
 * 
 * <p>This is a value object that enforces the business rule that
 * payee references must be non-empty strings.
 */
@Value
public class PayeeReference {

    String value;

    /**
     * Creates a PayeeReference.
     * 
     * @param value the payee reference value (must not be null or empty)
     * @throws IllegalArgumentException if value is null or empty
     */
    public PayeeReference(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Payee reference must not be null or empty");
        }
        this.value = value.trim();
    }
}

