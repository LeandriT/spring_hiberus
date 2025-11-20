package com.bank.paymentinitiation.domain.model;

import lombok.Value;

/**
 * Value object representing an external reference.
 * 
 * This is an immutable value object that encapsulates an external reference
 * provided by the client for traceability. It validates that the reference
 * is not null or empty.
 * 
 * Following Domain-Driven Design principles, this value object ensures
 * business invariants are maintained (reference must be non-empty).
 */
@Value
public class ExternalReference {
    
    String value;
    
    private ExternalReference(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("External reference cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * Factory method to create an ExternalReference.
     * 
     * @param value the external reference (must not be null or empty)
     * @return a new ExternalReference instance
     * @throws IllegalArgumentException if value is null or empty
     */
    public static ExternalReference of(String value) {
        return new ExternalReference(value);
    }
}

