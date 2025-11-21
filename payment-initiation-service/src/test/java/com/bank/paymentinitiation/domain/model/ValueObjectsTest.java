package com.bank.paymentinitiation.domain.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Value Objects Tests")
class ValueObjectsTest {

    @Test
    @DisplayName("ExternalReference should implement equals, hashCode and toString")
    void externalReferenceShouldImplementEqualsHashCodeToString() {
        // Arrange
        ExternalReference ref1 = new ExternalReference("EXT-1");
        ExternalReference ref2 = new ExternalReference("EXT-1");
        ExternalReference ref3 = new ExternalReference("EXT-2");

        // Assert
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        assertThat(ref1.toString()).contains("EXT-1");
    }

    @Test
    @DisplayName("PayerReference should implement equals, hashCode and toString")
    void payerReferenceShouldImplementEqualsHashCodeToString() {
        // Arrange
        PayerReference ref1 = new PayerReference("EC123456789012345678");
        PayerReference ref2 = new PayerReference("EC123456789012345678");
        PayerReference ref3 = new PayerReference("EC987654321098765432");

        // Assert
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        assertThat(ref1.toString()).contains("EC123456789012345678");
    }

    @Test
    @DisplayName("PayeeReference should implement equals, hashCode and toString")
    void payeeReferenceShouldImplementEqualsHashCodeToString() {
        // Arrange
        PayeeReference ref1 = new PayeeReference("EC123456789012345678");
        PayeeReference ref2 = new PayeeReference("EC123456789012345678");
        PayeeReference ref3 = new PayeeReference("EC987654321098765432");

        // Assert
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        assertThat(ref1.toString()).contains("EC123456789012345678");
    }

    @Test
    @DisplayName("PaymentAmount should implement equals, hashCode and toString")
    void paymentAmountShouldImplementEqualsHashCodeToString() {
        // Arrange
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("150.75"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("150.75"), "USD");
        PaymentAmount amount3 = PaymentAmount.of(new BigDecimal("200.00"), "USD");

        // Assert
        assertThat(amount1).isEqualTo(amount2);
        assertThat(amount1).isNotEqualTo(amount3);
        assertThat(amount1.hashCode()).isEqualTo(amount2.hashCode());
        assertThat(amount1.toString()).contains("150.75");
        assertThat(amount1.toString()).contains("USD");
    }

    @Test
    @DisplayName("PaymentAmount should throw exception when value is zero")
    void paymentAmountShouldThrowExceptionWhenValueIsZero() {
        // Act & Assert
        assertThatThrownBy(() -> PaymentAmount.of(BigDecimal.ZERO, "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount must be greater than zero");
    }

    @Test
    @DisplayName("PaymentAmount should throw exception when value is negative")
    void paymentAmountShouldThrowExceptionWhenValueIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("-10.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount must be greater than zero");
    }
}

