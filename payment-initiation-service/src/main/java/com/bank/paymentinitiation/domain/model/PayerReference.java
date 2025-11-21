package com.bank.paymentinitiation.domain.model;

import lombok.Value;

/**
 * Payer Reference Value Object.
 * 
 * <p>Represents a reference to the payer (debtor) account.
 * 
 * <p>This is a value object that enforces the business rule that
 * payer references must be non-empty strings.
 */
@Value
public class PayerReference {

    String value;

    /**
     * Creates a PayerReference.
     * 
     * @param value the payer reference value (must not be null or empty)
     * @throws IllegalArgumentException if value is null or empty
     */
    public PayerReference(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Payer reference must not be null or empty");
        }
        this.value = value.trim();
    }
}

