package com.bank.paymentinitiation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InitiatePaymentOrderService Tests")
class InitiatePaymentOrderServiceTest {

    @Mock
    private PaymentOrderRepository repository;

    @Mock
    private PaymentOrderDomainService paymentOrderDomainService;

    @InjectMocks
    private InitiatePaymentOrderService service;

    private PaymentOrder createValidPaymentOrder() {
        return PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .remittanceInformation("Factura 001-123")
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .build();
    }

    @Test
    @DisplayName("Should initiate payment order successfully")
    void shouldInitiatePaymentOrderSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        when(repository.save(any(PaymentOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentOrder result = service.initiate(order);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(paymentOrderDomainService).validate(any(PaymentOrder.class));
        verify(repository).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void shouldThrowExceptionWhenValidationFails() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        doThrow(new InvalidPaymentException("Invalid payment order"))
                .when(paymentOrderDomainService).validate(any(PaymentOrder.class));

        // Act & Assert
        assertThatThrownBy(() -> service.initiate(order))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Invalid payment order");
        verify(repository, never()).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should use existing payment order reference when provided")
    void shouldUseExistingPaymentOrderReference() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        when(repository.save(any(PaymentOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentOrder result = service.initiate(order);

        // Assert
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-1234567890123456");
        verify(paymentOrderDomainService, never()).generateReference();
        verify(repository).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should generate payment order reference when not provided")
    void shouldGeneratePaymentOrderReferenceWhenNotProvided() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .paymentOrderReference(null)
                .build();
        when(paymentOrderDomainService.generateReference()).thenReturn("PO-GENERATED-123");
        when(repository.save(any(PaymentOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentOrder result = service.initiate(order);

        // Assert
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-GENERATED-123");
        verify(paymentOrderDomainService).generateReference();
        verify(repository).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should not save when order validation fails")
    void shouldNotSaveWhenOrderValidationFails() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .externalReference(null) // Invalid
                .build();

        // Act & Assert
        assertThatThrownBy(() -> service.initiate(order))
                .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).save(any(PaymentOrder.class));
    }
}

