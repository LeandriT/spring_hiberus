package com.bank.paymentinitiation.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Component that generates unique payment order references.
 * 
 * Generates references in the format "PO-{UUID compacto}" where UUID compacto
 * is a compact representation of a UUID (e.g., "PO-A1B2C3D4E5F6G7H8").
 * 
 * This component is used by the REST controller to generate unique business
 * identifiers for payment orders before they are persisted.
 */
@Component
public class PaymentOrderReferenceGenerator {
    
    /**
     * Generates a unique payment order reference.
     * 
     * Format: "PO-{UUID compacto}" where UUID compacto is a compact representation
     * of a UUID without hyphens, converted to uppercase.
     * 
     * Example: "PO-A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6"
     * 
     * @return a unique payment order reference
     */
    public String generate() {
        UUID uuid = UUID.randomUUID();
        // Remove hyphens and convert to uppercase
        String compactUuid = uuid.toString().replace("-", "").toUpperCase();
        return "PO-" + compactUuid;
    }
}

