package com.bank.paymentinitiation.application.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Unit tests for PaymentOrderReferenceGenerator.
 */
@DisplayName("PaymentOrderReferenceGenerator Tests")
class PaymentOrderReferenceGeneratorTest {

    private final PaymentOrderReferenceGenerator generator = new PaymentOrderReferenceGenerator();
    
    // Pattern from OpenAPI: ^PO-[0-9]+$
    private static final Pattern VALID_PATTERN = Pattern.compile("^PO-[0-9]+$");

    @Test
    @DisplayName("Should generate payment order reference with correct format")
    void shouldGeneratePaymentOrderReferenceWithCorrectFormat() {
        // Act
        String reference = generator.generate();

        // Assert
        Assertions.assertThat(reference).isNotNull();
        Assertions.assertThat(reference).startsWith("PO-");
        Assertions.assertThat(reference).matches(VALID_PATTERN);
        Assertions.assertThat(reference.length()).isGreaterThan(3); // "PO-" + at least 1 digit
    }

    @Test
    @DisplayName("Should generate unique references")
    void shouldGenerateUniqueReferences() {
        // Arrange
        Set<String> references = new HashSet<>();
        int numberOfReferences = 100;

        // Act
        for (int i = 0; i < numberOfReferences; i++) {
            references.add(generator.generate());
            // Small delay to ensure timestamp difference
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Assert - All references should be unique
        Assertions.assertThat(references).hasSize(numberOfReferences);
    }

    @Test
    @DisplayName("Should generate references with numeric suffix")
    void shouldGenerateReferencesWithNumericSuffix() {
        // Act
        String reference = generator.generate();

        // Assert
        String suffix = reference.substring(3); // Remove "PO-" prefix
        Assertions.assertThat(suffix).matches("^[0-9]+$"); // Only digits
        Assertions.assertThat(suffix.length()).isGreaterThanOrEqualTo(16); // 10 digits (timestamp) + 6 digits (random)
    }

    @Test
    @DisplayName("Should generate references that match OpenAPI pattern")
    void shouldGenerateReferencesThatMatchOpenApiPattern() {
        // Act
        String reference = generator.generate();

        // Assert - Must match ^PO-[0-9]+$ pattern
        Assertions.assertThat(reference).matches("^PO-[0-9]+$");
    }

    @Test
    @DisplayName("Should generate different references on consecutive calls")
    void shouldGenerateDifferentReferencesOnConsecutiveCalls() {
        // Act
        String reference1 = generator.generate();
        String reference2 = generator.generate();

        // Assert
        Assertions.assertThat(reference1).isNotEqualTo(reference2);
    }
}

