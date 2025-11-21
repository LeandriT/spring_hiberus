package com.bank.paymentinitiation.domain.model;

import lombok.Value;

/**
 * External Reference Value Object.
 * 
 * <p>Represents an external reference identifier provided by the client
 * to identify a payment order externally.
 * 
 * <p>This is a value object that enforces the business rule that
 * external references must be non-empty strings.
 */
@Value
public class ExternalReference {

    String value;

    /**
     * Creates an ExternalReference.
     * 
     * @param value the external reference value (must not be null or empty)
     * @throws IllegalArgumentException if value is null or empty
     */
    public ExternalReference(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("External reference must not be null or empty");
        }
        this.value = value.trim();
    }
}

