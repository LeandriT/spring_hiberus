package com.bank.paymentinitiation.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * Unit tests for PaymentAmount value object.
 */
@DisplayName("PaymentAmount Value Object Tests")
class PaymentAmountTest {

    @Test
    @DisplayName("Should create PaymentAmount successfully with valid amount and currency")
    void shouldCreatePaymentAmountSuccessfullyWithValidAmountAndCurrency() {
        // Arrange & Act
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("150.75"), "USD");

        // Assert
        Assertions.assertThat(amount.getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        Assertions.assertThat(amount.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> PaymentAmount.of(null, "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment amount value must not be null");
    }

    @Test
    @DisplayName("Should throw exception when amount is zero")
    void shouldThrowExceptionWhenAmountIsZero() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> PaymentAmount.of(BigDecimal.ZERO, "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment amount must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("-10.50"), "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment amount must be greater than zero");
    }

    @Test
    @DisplayName("Should throw exception when currency is null")
    void shouldThrowExceptionWhenCurrencyIsNull() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("100.00"), null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when currency is empty")
    void shouldThrowExceptionWhenCurrencyIsEmpty() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("100.00"), ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency must not be null or empty");
    }

    @Test
    @DisplayName("Should normalize currency to uppercase")
    void shouldNormalizeCurrencyToUppercase() {
        // Act
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("100.00"), "usd");

        // Assert
        Assertions.assertThat(amount.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should trim currency whitespace")
    void shouldTrimCurrencyWhitespace() {
        // Act
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("100.00"), "  USD  ");

        // Assert
        Assertions.assertThat(amount.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should be equal when value and currency are the same")
    void shouldBeEqualWhenValueAndCurrencyAreTheSame() {
        // Arrange
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("100.00"), "USD");

        // Assert
        Assertions.assertThat(amount1).isEqualTo(amount2);
        Assertions.assertThat(amount1.hashCode()).isEqualTo(amount2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when value is different")
    void shouldNotBeEqualWhenValueIsDifferent() {
        // Arrange
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("200.00"), "USD");

        // Assert
        Assertions.assertThat(amount1).isNotEqualTo(amount2);
    }

    @Test
    @DisplayName("Should not be equal when currency is different")
    void shouldNotBeEqualWhenCurrencyIsDifferent() {
        // Arrange
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("100.00"), "EUR");

        // Assert
        Assertions.assertThat(amount1).isNotEqualTo(amount2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToString() {
        // Arrange
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("100.00"), "USD");

        // Assert
        Assertions.assertThat(amount).isNotEqualTo(null);
        Assertions.assertThat(amount.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        // Arrange
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("100.00"), "USD");

        // Assert
        Assertions.assertThat(amount).isNotEqualTo("not a PaymentAmount");
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        // Arrange
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("100.00"), "USD");

        // Act & Assert
        int hashCode1 = amount.hashCode();
        int hashCode2 = amount.hashCode();
        Assertions.assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Arrange
        PaymentAmount amount = PaymentAmount.of(new BigDecimal("100.00"), "USD");

        // Act
        String toString = amount.toString();

        // Assert
        Assertions.assertThat(toString).isNotNull();
        Assertions.assertThat(toString).isNotEmpty();
    }
}

