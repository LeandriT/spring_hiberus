package com.bank.paymentinitiation.domain.model;

import lombok.Value;

/**
 * Value object representing a payee (creditor) reference.
 * 
 * This is an immutable value object that encapsulates a payee reference.
 * It validates that the reference is not null or empty.
 * 
 * Following Domain-Driven Design principles, this value object ensures
 * business invariants are maintained (reference must be non-empty).
 */
@Value
public class PayeeReference {
    
    String value;
    
    private PayeeReference(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payee reference cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * Factory method to create a PayeeReference.
     * 
     * @param value the payee reference (must not be null or empty)
     * @return a new PayeeReference instance
     * @throws IllegalArgumentException if value is null or empty
     */
    public static PayeeReference of(String value) {
        return new PayeeReference(value);
    }
}

