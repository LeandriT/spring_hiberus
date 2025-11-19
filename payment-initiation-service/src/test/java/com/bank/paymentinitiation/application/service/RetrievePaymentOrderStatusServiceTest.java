package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.*;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para RetrievePaymentOrderStatusService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RetrievePaymentOrderStatusService Tests")
class RetrievePaymentOrderStatusServiceTest {
    
    @Mock
    private PaymentOrderRepository paymentOrderRepository;
    
    @InjectMocks
    private RetrievePaymentOrderStatusService retrievePaymentOrderStatusService;
    
    private PaymentOrder existingOrder;
    private static final String VALID_REFERENCE = "PO-0001";
    private static final String NON_EXISTENT_REFERENCE = "PO-9999";
    
    @BeforeEach
    void setUp() {
        existingOrder = PaymentOrder.create(
            VALID_REFERENCE,
            ExternalReference.of("EXT-1"),
            PayerReference.of("PAYER-123"),
            PayeeReference.of("PAYEE-456"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            "Factura 001",
            LocalDate.now().plusDays(1)
        );
    }
    
    @Test
    @DisplayName("Debería recuperar el estado de una orden existente")
    void shouldRetrieveStatusOfExistingOrder() {
        // Given
        when(paymentOrderRepository.findByReference(VALID_REFERENCE))
            .thenReturn(Optional.of(existingOrder));
        
        // When
        PaymentStatus result = retrievePaymentOrderStatusService.retrieveStatus(VALID_REFERENCE);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(PaymentStatus.INITIATED);
        verify(paymentOrderRepository, times(1)).findByReference(VALID_REFERENCE);
    }
    
    @Test
    @DisplayName("Debería retornar el estado correcto después de cambio de estado")
    void shouldReturnCorrectStatusAfterStatusChange() {
        // Given
        existingOrder.changeStatus(PaymentStatus.PENDING);
        when(paymentOrderRepository.findByReference(VALID_REFERENCE))
            .thenReturn(Optional.of(existingOrder));
        
        // When
        PaymentStatus result = retrievePaymentOrderStatusService.retrieveStatus(VALID_REFERENCE);
        
        // Then
        assertThat(result).isEqualTo(PaymentStatus.PENDING);
    }
    
    @Test
    @DisplayName("Debería lanzar PaymentOrderNotFoundException si la orden no existe")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(paymentOrderRepository.findByReference(NON_EXISTENT_REFERENCE))
            .thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> retrievePaymentOrderStatusService.retrieveStatus(NON_EXISTENT_REFERENCE))
            .isInstanceOf(PaymentOrderNotFoundException.class)
            .hasMessageContaining("Payment order not found with reference: " + NON_EXISTENT_REFERENCE);
        
        verify(paymentOrderRepository, times(1)).findByReference(NON_EXISTENT_REFERENCE);
    }
    
    @Test
    @DisplayName("Debería llamar al repositorio con la referencia correcta")
    void shouldCallRepositoryWithCorrectReference() {
        // Given
        when(paymentOrderRepository.findByReference(anyString()))
            .thenReturn(Optional.of(existingOrder));
        
        // When
        retrievePaymentOrderStatusService.retrieveStatus(VALID_REFERENCE);
        
        // Then
        verify(paymentOrderRepository, times(1)).findByReference(VALID_REFERENCE);
        verifyNoMoreInteractions(paymentOrderRepository);
    }
}

