package com.bank.paymentinitiation.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PaymentOrderDomainService Tests")
class PaymentOrderDomainServiceTest {

    private final PaymentOrderDomainService service = new PaymentOrderDomainService();

    private PaymentOrder createValidPaymentOrder() {
        return PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .remittanceInformation("Factura 001-123")
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should generate reference in correct format")
    void shouldGenerateReferenceInCorrectFormat() {
        // Act
        String reference = service.generateReference();

        // Assert
        assertThat(reference).isNotNull();
        assertThat(reference).startsWith("PO-");
        assertThat(reference.substring(3)).matches("^[0-9]+$"); // Solo números después de "PO-"
    }

    @Test
    @DisplayName("Should generate unique references")
    void shouldGenerateUniqueReferences() {
        // Act
        String reference1 = service.generateReference();
        String reference2 = service.generateReference();

        // Assert
        assertThat(reference1).isNotEqualTo(reference2);
    }

    @Test
    @DisplayName("Should validate payment order successfully")
    void shouldValidatePaymentOrderSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act & Assert
        service.validate(order); // No debe lanzar excepción
    }

    @Test
    @DisplayName("Should throw exception when order is null")
    void shouldThrowExceptionWhenOrderIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> service.validate(null))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Payment order cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when requested execution date is in the past")
    void shouldThrowExceptionWhenRequestedExecutionDateIsInPast() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .requestedExecutionDate(LocalDate.now().minusDays(1))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> service.validate(order))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Requested execution date cannot be in the past");
    }
}

