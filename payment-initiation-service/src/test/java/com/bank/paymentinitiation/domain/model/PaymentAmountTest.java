package com.bank.paymentinitiation.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el value object PaymentAmount.
 */
@DisplayName("PaymentAmount Value Object Tests")
class PaymentAmountTest {
    
    @Test
    @DisplayName("Debería crear PaymentAmount válido con valor positivo")
    void shouldCreateValidPaymentAmount() {
        // Given
        BigDecimal amount = new BigDecimal("150.75");
        String currency = "USD";
        
        // When
        PaymentAmount paymentAmount = PaymentAmount.of(amount, currency);
        
        // Then
        assertThat(paymentAmount).isNotNull();
        assertThat(paymentAmount.getValue()).isEqualByComparingTo(amount);
        assertThat(paymentAmount.getCurrency()).isEqualTo("USD");
    }
    
    @Test
    @DisplayName("Debería normalizar la moneda a mayúsculas")
    void shouldNormalizeCurrencyToUppercase() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        
        // When
        PaymentAmount paymentAmount = PaymentAmount.of(amount, "usd");
        
        // Then
        assertThat(paymentAmount.getCurrency()).isEqualTo("USD");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si amount es null")
    void shouldThrowExceptionWhenAmountIsNull() {
        // When/Then
        assertThatThrownBy(() -> PaymentAmount.of(null, "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment amount value cannot be null");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si amount es cero")
    void shouldThrowExceptionWhenAmountIsZero() {
        // When/Then
        assertThatThrownBy(() -> PaymentAmount.of(BigDecimal.ZERO, "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment amount must be greater than zero");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si amount es negativo")
    void shouldThrowExceptionWhenAmountIsNegative() {
        // When/Then
        assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("-10.00"), "USD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment amount must be greater than zero");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si currency es null")
    void shouldThrowExceptionWhenCurrencyIsNull() {
        // When/Then
        assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("100.00"), null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency cannot be null or empty");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si currency es vacío")
    void shouldThrowExceptionWhenCurrencyIsEmpty() {
        // When/Then
        assertThatThrownBy(() -> PaymentAmount.of(new BigDecimal("100.00"), ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency cannot be null or empty");
    }
    
    @Test
    @DisplayName("Debería ser igual si tienen mismo amount y currency")
    void shouldBeEqualIfSameAmountAndCurrency() {
        // Given
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        
        // Then
        assertThat(amount1).isEqualTo(amount2);
        assertThat(amount1.hashCode()).isEqualTo(amount2.hashCode());
    }
    
    @Test
    @DisplayName("Debería ser diferente si tienen diferente amount")
    void shouldBeDifferentIfDifferentAmount() {
        // Given
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("200.00"), "USD");
        
        // Then
        assertThat(amount1).isNotEqualTo(amount2);
    }
    
    @Test
    @DisplayName("Debería ser diferente si tienen diferente currency")
    void shouldBeDifferentIfDifferentCurrency() {
        // Given
        PaymentAmount amount1 = PaymentAmount.of(new BigDecimal("100.00"), "USD");
        PaymentAmount amount2 = PaymentAmount.of(new BigDecimal("100.00"), "EUR");
        
        // Then
        assertThat(amount1).isNotEqualTo(amount2);
    }
}

