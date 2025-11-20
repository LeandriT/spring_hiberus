package com.bank.paymentinitiation.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;

/**
 * Unit tests for InitiatePaymentOrderService.
 * 
 * Tests cover:
 * - Successful initiation
 * - Validation failures
 * - Reference generation
 * - Repository interaction
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InitiatePaymentOrderService Tests")
class InitiatePaymentOrderServiceTest {
    
    @Mock
    private PaymentOrderRepository paymentOrderRepository;
    
    @Mock
    private PaymentOrderDomainService paymentOrderDomainService;
    
    @InjectMocks
    private InitiatePaymentOrderService initiatePaymentOrderService;
    
    private PaymentOrder validOrder;
    private PaymentOrder savedOrder;
    
    @BeforeEach
    void setUp() {
        validOrder = createValidPaymentOrder();
        savedOrder = validOrder.toBuilder()
            .paymentOrderReference("PO-GENERATED-123")
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("Should initiate payment order successfully")
    void shouldInitiatePaymentOrderSuccessfully() {
        // Arrange
        doNothing().when(paymentOrderDomainService).validate(any(PaymentOrder.class));
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(invocation -> {
            PaymentOrder saved = invocation.getArgument(0);
            return saved; // Return the order as saved
        });
        
        // Act
        PaymentOrder result = initiatePaymentOrderService.initiate(validOrder);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(result.getPaymentOrderReference()).isNotNull();
        
        // Note: validate is called AFTER initiate(), so we need to verify with the initiated order
        verify(paymentOrderDomainService).validate(any(PaymentOrder.class));
        verify(paymentOrderRepository).save(any(PaymentOrder.class));
    }
    
    @Test
    @DisplayName("Should use existing paymentOrderReference if provided")
    void shouldUseExistingPaymentOrderReference() {
        // Arrange
        PaymentOrder orderWithReference = validOrder.toBuilder()
            .paymentOrderReference("PO-EXISTING-123")
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        doNothing().when(paymentOrderDomainService).validate(any(PaymentOrder.class));
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(invocation -> {
            PaymentOrder saved = invocation.getArgument(0);
            return saved;
        });
        
        // Act
        PaymentOrder result = initiatePaymentOrderService.initiate(orderWithReference);
        
        // Assert
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-EXISTING-123");
        verify(paymentOrderRepository).save(any(PaymentOrder.class));
    }
    
    @Test
    @DisplayName("Should throw InvalidPaymentException when validation fails")
    void shouldThrowExceptionWhenValidationFails() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid payment order"))
            .when(paymentOrderDomainService).validate(any(PaymentOrder.class));
        
        // Act & Assert
        assertThatThrownBy(() -> initiatePaymentOrderService.initiate(validOrder))
            .isInstanceOf(InvalidPaymentException.class)
            .hasMessageContaining("Invalid payment order");
        
        // Note: validate is called AFTER initiate(), so we need to verify with any order
        // The validation will be called on the initiated order, not the original
        verify(paymentOrderDomainService).validate(any(PaymentOrder.class));
        verify(paymentOrderRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should not save when order is invalid")
    void shouldNotSaveWhenOrderInvalid() {
        // Arrange
        PaymentOrder invalidOrder = validOrder.toBuilder()
            .paymentOrderReference(null)
            .externalReference(null) // Invalid
            .build();
        
        // Mock generateReference to return a valid reference
        when(paymentOrderDomainService.generateReference())
            .thenReturn(com.bank.paymentinitiation.domain.model.PaymentOrderReference.of("PO-TEST123"));
        doNothing().when(paymentOrderDomainService).validate(any(PaymentOrder.class));
        
        // Act & Assert
        // The order will be initiated first, then validated. The validation will fail on externalReference being null
        assertThatThrownBy(() -> initiatePaymentOrderService.initiate(invalidOrder))
            .isInstanceOf(InvalidPaymentException.class)
            .hasMessageContaining("Invalid payment order");
        
        // Note: validate is called AFTER initiate(), so it will be called on the initiated order
        verify(paymentOrderDomainService).validate(any(PaymentOrder.class));
        verify(paymentOrderRepository, never()).save(any());
    }
    
    // Helper methods
    
    private PaymentOrder createValidPaymentOrder() {
        return PaymentOrder.builder()
            .paymentOrderReference("PO-TEMP-VALID")
            .externalReference(ExternalReference.of("EXT-1"))
            .payerReference(PayerReference.of("PAYER-123"))
            .payeeReference(PayeeReference.of("PAYEE-456"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("100.50"), "USD"))
            .remittanceInformation("Test payment")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}

