package com.bank.paymentinitiation.domain.model;

import java.util.regex.Pattern;

import lombok.Value;

/**
 * Value object representing a Payment Order Reference.
 * 
 * This is a business identifier for a payment order, following BIAN
 * naming conventions. It should be immutable and validated.
 * 
 * Format: PO-{identifier} (e.g., PO-0001, PO-2024-001)
 * Pattern: PO-[A-Z0-9-]+
 */
@Value
public class PaymentOrderReference {
    
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^PO-[A-Z0-9-]+$");
    
    String value;
    
    private PaymentOrderReference(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment order reference cannot be null or empty");
        }
        if (!REFERENCE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "Payment order reference must match pattern PO-[A-Z0-9-]+. Got: " + value
            );
        }
        this.value = value;
    }
    
    /**
     * Factory method to create a PaymentOrderReference.
     * 
     * @param value the payment order reference (must match pattern PO-[A-Z0-9-]+)
     * @return a new PaymentOrderReference instance
     * @throws IllegalArgumentException if value is null, empty, or doesn't match pattern
     */
    public static PaymentOrderReference of(String value) {
        return new PaymentOrderReference(value);
    }
}
