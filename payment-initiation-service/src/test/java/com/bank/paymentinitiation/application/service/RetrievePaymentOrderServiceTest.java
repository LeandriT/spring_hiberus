package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * Unit tests for RetrievePaymentOrderService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RetrievePaymentOrderService Tests")
class RetrievePaymentOrderServiceTest {

    @Mock
    private PaymentOrderRepository repository;

    @InjectMocks
    private RetrievePaymentOrderService service;

    private PaymentOrder createValidPaymentOrder() {
        PaymentOrder order = PaymentOrder.builder()
            .paymentOrderReference("PO-TEST123456")
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
    @DisplayName("Should retrieve payment order successfully")
    void shouldRetrievePaymentOrderSuccessfully() {
        // Arrange
        String paymentOrderReference = "PO-TEST123456";
        PaymentOrder order = createValidPaymentOrder();
        when(repository.findByReference(paymentOrderReference))
            .thenReturn(Optional.of(order));

        // Act
        PaymentOrder result = service.retrieve(paymentOrderReference);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentOrderReference()).isEqualTo(paymentOrderReference);
    }

    @Test
    @DisplayName("Should throw PaymentOrderNotFoundException when order not found")
    void shouldThrowWhenOrderNotFound() {
        // Arrange
        String paymentOrderReference = "PO-NOTFOUND";
        when(repository.findByReference(paymentOrderReference))
            .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.retrieve(paymentOrderReference))
            .isInstanceOf(PaymentOrderNotFoundException.class)
            .hasMessageContaining("Payment order with reference 'PO-NOTFOUND' not found");
    }

    @Test
    @DisplayName("Should throw exception when reference is null")
    void shouldThrowWhenReferenceIsNull() {
        // Arrange
        when(repository.findByReference(null))
            .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.retrieve(null))
            .isInstanceOf(PaymentOrderNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when reference is blank")
    void shouldThrowWhenReferenceIsBlank() {
        // Arrange
        when(repository.findByReference("   "))
            .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.retrieve("   "))
            .isInstanceOf(PaymentOrderNotFoundException.class);
    }
}

