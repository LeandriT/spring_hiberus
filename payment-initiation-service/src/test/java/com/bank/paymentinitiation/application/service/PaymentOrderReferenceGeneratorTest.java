package com.bank.paymentinitiation.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PaymentOrderReferenceGenerator Tests")
class PaymentOrderReferenceGeneratorTest {

    private final PaymentOrderReferenceGenerator generator = new PaymentOrderReferenceGenerator();

    @Test
    @DisplayName("Should generate reference in correct format")
    void shouldGenerateReferenceInCorrectFormat() {
        // Act
        String reference = generator.generate();

        // Assert
        assertThat(reference).isNotNull();
        assertThat(reference).startsWith("PO-");
        assertThat(reference.substring(3)).matches("^[0-9]+$"); // Solo números después de "PO-"
        assertThat(reference).matches("^PO-[0-9]+$"); // Cumple patrón del OpenAPI
    }

    @Test
    @DisplayName("Should generate unique references")
    void shouldGenerateUniqueReferences() {
        // Act
        String reference1 = generator.generate();
        String reference2 = generator.generate();

        // Assert
        assertThat(reference1).isNotEqualTo(reference2);
    }

    @Test
    @DisplayName("Should generate reference with sufficient length")
    void shouldGenerateReferenceWithSufficientLength() {
        // Act
        String reference = generator.generate();

        // Assert
        assertThat(reference.length()).isGreaterThan(10); // Al menos "PO-" + algunos dígitos
    }
}

