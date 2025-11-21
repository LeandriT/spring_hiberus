package com.bank.paymentinitiation.domain.service;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for PaymentOrderDomainService.
 */
@DisplayName("PaymentOrderDomainService Tests")
class PaymentOrderDomainServiceTest {

    private final PaymentOrderDomainService service = new PaymentOrderDomainService();

    private PaymentOrder createValidPaymentOrder() {
        PaymentOrder order = PaymentOrder.builder()
            .paymentOrderReference("PO-1234567890123456")
            .externalReference(new ExternalReference("EXT-123"))
            .payerReference(new PayerReference("EC123456789012345678"))
            .payeeReference(new PayeeReference("EC987654321098765432"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .build();
        order.initiate();
        return order;
    }

    @Test
    @DisplayName("Should validate payment order successfully when order is valid")
    void shouldValidatePaymentOrderSuccessfullyWhenOrderIsValid() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act & Assert - Should not throw exception
        Assertions.assertThatCode(() -> service.validate(order))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when order is null")
    void shouldThrowIllegalArgumentExceptionWhenOrderIsNull() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.validate(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Payment order must not be null");
    }

    @Test
    @DisplayName("Should validate payment order with all fields set")
    void shouldValidatePaymentOrderWithAllFieldsSet() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        order.validate(); // Aggregate validation passes

        // Act & Assert - Domain service validation should pass
        Assertions.assertThatCode(() -> service.validate(order))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should validate payment order after initiate is called")
    void shouldValidatePaymentOrderAfterInitiateIsCalled() {
        // Arrange
        PaymentOrder order = PaymentOrder.builder()
            .paymentOrderReference("PO-1234567890123456")
            .externalReference(new ExternalReference("EXT-123"))
            .payerReference(new PayerReference("EC123456789012345678"))
            .payeeReference(new PayeeReference("EC987654321098765432"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .build();
        
        // Act
        order.initiate();
        
        // Assert - Domain service validation should pass after initiate
        Assertions.assertThatCode(() -> service.validate(order))
            .doesNotThrowAnyException();
    }
}

