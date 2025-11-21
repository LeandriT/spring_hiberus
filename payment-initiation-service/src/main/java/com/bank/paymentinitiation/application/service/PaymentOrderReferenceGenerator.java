package com.bank.paymentinitiation.application.service;

import org.springframework.stereotype.Component;


/**
 * Payment Order Reference Generator.
 * 
 * <p>Generates unique payment order references in format "PO-{UUID compacto}".
 * 
 * <p>Format: "PO-" followed by UUID without hyphens (uppercase).
 * Example: "PO-A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6"
 * 
 * <p>⚠️ CRITICAL: The UUID is generated WITHOUT hyphens using `replace("-", "")`
 * to ensure compatibility with pattern validation and avoid issues with hyphens.
 */
@Component
public class PaymentOrderReferenceGenerator {

    /**
     * Generates a unique payment order reference.
     * 
     * <p>Format: "PO-{numeric identifier}"
     * Example: "PO-1234567890123456"
     * 
     * <p>The format must match the OpenAPI pattern: ^PO-[0-9]+$
     * Uses timestamp + random number to ensure uniqueness.
     * 
     * @return a unique payment order reference
     */
    public String generate() {
        // Generate a numeric identifier using timestamp (last 10 digits) + random (6 digits)
        // This ensures uniqueness while matching the pattern ^PO-[0-9]+$
        long timestamp = System.currentTimeMillis();
        long random = (long) (Math.random() * 1000000); // 6 digits
        String numericId = String.format("%010d%06d", timestamp % 10000000000L, random);
        return "PO-" + numericId;
    }
}

