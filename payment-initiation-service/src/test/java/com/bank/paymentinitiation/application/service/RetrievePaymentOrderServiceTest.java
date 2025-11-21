package com.bank.paymentinitiation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RetrievePaymentOrderService Tests")
class RetrievePaymentOrderServiceTest {

    @Mock
    private PaymentOrderRepository repository;

    @InjectMocks
    private RetrievePaymentOrderService service;

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
    @DisplayName("Should retrieve payment order successfully")
    void shouldRetrievePaymentOrderSuccessfully() {
        // Arrange
        String reference = "PO-1234567890123456";
        PaymentOrder order = createValidPaymentOrder();
        when(repository.findByReference(reference)).thenReturn(Optional.of(order));

        // Act
        PaymentOrder result = service.retrieve(reference);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo(reference);
        verify(repository).findByReference(reference);
    }

    @Test
    @DisplayName("Should throw when order not found")
    void shouldThrowWhenOrderNotFound() {
        // Arrange
        String reference = "PO-NOT-FOUND";
        when(repository.findByReference(reference)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.retrieve(reference))
                .isInstanceOf(PaymentOrderNotFoundException.class)
                .hasMessageContaining("Payment order not found with reference: " + reference);
        verify(repository).findByReference(reference);
    }

    @Test
    @DisplayName("Should throw when reference is null")
    void shouldThrowWhenReferenceIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> service.retrieve(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment order reference cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw when reference is blank")
    void shouldThrowWhenReferenceIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> service.retrieve("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment order reference cannot be null or blank");
    }
}

