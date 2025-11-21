package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for InitiatePaymentOrderService.
 */
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
            .paymentOrderReference("PO-TEST123456")
            .externalReference(new ExternalReference("EXT-123"))
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
        PaymentOrder savedOrder = PaymentOrder.builder()
            .paymentOrderReference(order.getPaymentOrderReference())
            .externalReference(order.getExternalReference())
            .payerReference(order.getPayerReference())
            .payeeReference(order.getPayeeReference())
            .instructedAmount(order.getInstructedAmount())
            .remittanceInformation(order.getRemittanceInformation())
            .requestedExecutionDate(order.getRequestedExecutionDate())
            .status(PaymentStatus.INITIATED)
            .build();
        savedOrder.initiate();
        
        when(repository.save(any(PaymentOrder.class))).thenReturn(savedOrder);

        // Act
        PaymentOrder result = service.initiate(order);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
        verify(paymentOrderDomainService).validate(any(PaymentOrder.class));
        verify(repository).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void shouldThrowExceptionWhenValidationFails() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrder invalidOrder = order.toBuilder()
            .externalReference(null) // Invalid: null external reference
            .build();
        invalidOrder.initiate();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.initiate(invalidOrder))
            .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should not save when order is invalid")
    void shouldNotSaveWhenOrderInvalid() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrder invalidOrder = order.toBuilder()
            .instructedAmount(null) // Invalid: null amount
            .build();
        invalidOrder.initiate();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.initiate(invalidOrder))
            .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should call initiate before validation")
    void shouldCallInitiateBeforeValidation() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrder savedOrder = PaymentOrder.builder()
            .paymentOrderReference(order.getPaymentOrderReference())
            .externalReference(order.getExternalReference())
            .payerReference(order.getPayerReference())
            .payeeReference(order.getPayeeReference())
            .instructedAmount(order.getInstructedAmount())
            .remittanceInformation(order.getRemittanceInformation())
            .requestedExecutionDate(order.getRequestedExecutionDate())
            .status(PaymentStatus.INITIATED)
            .build();
        savedOrder.initiate();
        
        when(repository.save(any(PaymentOrder.class))).thenReturn(savedOrder);

        // Act
        PaymentOrder result = service.initiate(order);

        // Assert - initiate() sets status and createdAt, which are required for validate()
        Assertions.assertThat(result.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
    }
}

