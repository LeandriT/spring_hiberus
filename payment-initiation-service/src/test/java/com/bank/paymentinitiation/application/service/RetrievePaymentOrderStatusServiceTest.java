package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RetrievePaymentOrderStatusService.
 * 
 * Tests cover:
 * - Successful status retrieval
 * - Not found scenarios
 * - Invalid input
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RetrievePaymentOrderStatusService Tests")
class RetrievePaymentOrderStatusServiceTest {
    
    @Mock
    private PaymentOrderRepository paymentOrderRepository;
    
    @InjectMocks
    private RetrievePaymentOrderStatusService retrievePaymentOrderStatusService;
    
    private PaymentOrder existingOrder;
    private String paymentOrderReference;
    
    @BeforeEach
    void setUp() {
        paymentOrderReference = "PO-123";
        existingOrder = createValidPaymentOrder(paymentOrderReference, PaymentStatus.PENDING);
    }
    
    @Test
    @DisplayName("Should retrieve payment order status successfully")
    void shouldRetrievePaymentOrderStatusSuccessfully() {
        // Arrange
        when(paymentOrderRepository.findByReference(paymentOrderReference))
            .thenReturn(Optional.of(existingOrder));
        
        // Act
        PaymentStatus result = retrievePaymentOrderStatusService.retrieveStatus(paymentOrderReference);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(PaymentStatus.PENDING);
        
        verify(paymentOrderRepository).findByReference(paymentOrderReference);
    }
    
    @Test
    @DisplayName("Should throw PaymentOrderNotFoundException when order not found")
    void shouldThrowWhenOrderNotFound() {
        // Arrange
        when(paymentOrderRepository.findByReference(paymentOrderReference))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> retrievePaymentOrderStatusService.retrieveStatus(paymentOrderReference))
            .isInstanceOf(PaymentOrderNotFoundException.class)
            .hasMessageContaining("Payment order not found");
        
        verify(paymentOrderRepository).findByReference(paymentOrderReference);
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when reference is null")
    void shouldThrowWhenReferenceIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> retrievePaymentOrderStatusService.retrieveStatus(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment order reference cannot be null or empty");
        
        verify(paymentOrderRepository, never()).findByReference(any());
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when reference is blank")
    void shouldThrowWhenReferenceIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> retrievePaymentOrderStatusService.retrieveStatus("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment order reference cannot be null or empty");
        
        verify(paymentOrderRepository, never()).findByReference(any());
    }
    
    // Helper methods
    
    private PaymentOrder createValidPaymentOrder(String reference, PaymentStatus status) {
        return PaymentOrder.builder()
            .paymentOrderReference(reference)
            .externalReference(ExternalReference.of("EXT-1"))
            .payerReference(PayerReference.of("PAYER-123"))
            .payeeReference(PayeeReference.of("PAYEE-456"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("100.50"), "USD"))
            .remittanceInformation("Test payment")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}

