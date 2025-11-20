package com.bank.paymentinitiation.domain.model;

import lombok.Value;

/**
 * Value object representing a payer (debtor) reference.
 * 
 * This is an immutable value object that encapsulates a payer reference.
 * It validates that the reference is not null or empty.
 * 
 * Following Domain-Driven Design principles, this value object ensures
 * business invariants are maintained (reference must be non-empty).
 */
@Value
public class PayerReference {
    
    String value;
    
    private PayerReference(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payer reference cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * Factory method to create a PayerReference.
     * 
     * @param value the payer reference (must not be null or empty)
     * @return a new PayerReference instance
     * @throws IllegalArgumentException if value is null or empty
     */
    public static PayerReference of(String value) {
        return new PayerReference(value);
    }
}

