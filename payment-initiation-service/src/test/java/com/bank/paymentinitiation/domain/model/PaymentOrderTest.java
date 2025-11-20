package com.bank.paymentinitiation.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PaymentOrder aggregate.
 * 
 * Tests cover:
 * - Validation of required fields
 * - Status transitions (valid and invalid)
 * - Initiate method
 * - Change status method
 * - Exception handling
 */
@DisplayName("PaymentOrder Aggregate Tests")
class PaymentOrderTest {
    
    // ==================== Validaciones de Creación ====================
    
    @Test
    @DisplayName("Should create valid PaymentOrder with all required fields")
    void shouldCreateValidPaymentOrder() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        
        // Act & Assert
        assertThat(order).isNotNull();
        assertThat(order.getPaymentOrderReference()).isEqualTo("PO-12345");
        assertThat(order.getExternalReference()).isNotNull();
        assertThat(order.getPayerReference()).isNotNull();
        assertThat(order.getPayeeReference()).isNotNull();
        assertThat(order.getInstructedAmount()).isNotNull();
        assertThat(order.getRequestedExecutionDate()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(order.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when paymentOrderReference is null")
    void shouldThrowExceptionWhenPaymentOrderReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .paymentOrderReference(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment order reference cannot be null or empty");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when paymentOrderReference is blank")
    void shouldThrowExceptionWhenPaymentOrderReferenceIsBlank() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .paymentOrderReference("   ")
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payment order reference cannot be null or empty");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when externalReference is null")
    void shouldThrowExceptionWhenExternalReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .externalReference(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("External reference cannot be null");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when payerReference is null")
    void shouldThrowExceptionWhenPayerReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .payerReference(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payer reference cannot be null");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when payeeReference is null")
    void shouldThrowExceptionWhenPayeeReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .payeeReference(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payee reference cannot be null");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when instructedAmount is null")
    void shouldThrowExceptionWhenInstructedAmountIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .instructedAmount(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Instructed amount cannot be null");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when requestedExecutionDate is null")
    void shouldThrowExceptionWhenRequestedExecutionDateIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .requestedExecutionDate(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Requested execution date cannot be null");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when createdAt is null")
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .createdAt(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Created at timestamp cannot be null");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(null)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Status cannot be null");
    }
    
    // ==================== Reglas de Negocio / Transiciones de Estado ====================
    
    @Test
    @DisplayName("Should allow transition from INITIATED to PENDING")
    void shouldAllowTransitionFromInitiatedToPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.INITIATED)
            .build();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.PENDING);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(updated.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
    
    @Test
    @DisplayName("Should allow transition from INITIATED to CANCELLED")
    void shouldAllowTransitionFromInitiatedToCancelled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.INITIATED)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.CANCELLED);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }
    
    @Test
    @DisplayName("Should allow transition from PENDING to PROCESSED")
    void shouldAllowTransitionFromPendingToProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.PENDING)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.PROCESSED);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.PROCESSED);
    }
    
    @Test
    @DisplayName("Should allow transition from PENDING to FAILED")
    void shouldAllowTransitionFromPendingToFailed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.PENDING)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.FAILED);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
    
    @Test
    @DisplayName("Should allow transition from PENDING to CANCELLED")
    void shouldAllowTransitionFromPendingToCancelled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.PENDING)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.CANCELLED);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }
    
    @Test
    @DisplayName("Should allow transition from PROCESSED to COMPLETED")
    void shouldAllowTransitionFromProcessedToCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.PROCESSED)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.COMPLETED);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
    
    @Test
    @DisplayName("Should allow transition from PROCESSED to FAILED")
    void shouldAllowTransitionFromProcessedToFailed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.PROCESSED)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.FAILED);
        
        // Assert
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
    
    @Test
    @DisplayName("Should throw IllegalStateException when transitioning from INITIATED to PROCESSED directly")
    void shouldThrowExceptionWhenTransitioningFromInitiatedToProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.INITIATED)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PROCESSED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Invalid status transition from INITIATED to PROCESSED");
    }
    
    @Test
    @DisplayName("Should throw IllegalStateException when transitioning from COMPLETED to any status")
    void shouldThrowExceptionWhenTransitioningFromCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.COMPLETED)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal status");
    }
    
    @Test
    @DisplayName("Should throw IllegalStateException when transitioning from FAILED to any status")
    void shouldThrowExceptionWhenTransitioningFromFailed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.FAILED)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal status");
    }
    
    @Test
    @DisplayName("Should throw IllegalStateException when transitioning from CANCELLED to any status")
    void shouldThrowExceptionWhenTransitioningFromCancelled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.CANCELLED)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal status");
    }
    
    @Test
    @DisplayName("Should update updatedAt when status changes")
    void shouldUpdateUpdatedAtWhenStatusChanges() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.INITIATED)
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.PENDING);
        
        // Assert
        assertThat(updated.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
    
    @Test
    @DisplayName("Should return same instance when changing to same status")
    void shouldReturnSameInstanceWhenChangingToSameStatus() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.INITIATED)
            .build();
        
        // Act
        PaymentOrder updated = order.changeStatus(PaymentStatus.INITIATED);
        
        // Assert
        assertThat(updated).isSameAs(order);
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException when changing status to null")
    void shouldThrowExceptionWhenChangingStatusToNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.INITIATED)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("New status cannot be null");
    }
    
    @Test
    @DisplayName("Should initiate payment order and set status to INITIATED")
    void shouldInitiatePaymentOrder() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(null)
            .createdAt(null)
            .updatedAt(null)
            .build();
        
        // Act
        PaymentOrder initiated = order.initiate();
        
        // Assert
        assertThat(initiated.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(initiated.getCreatedAt()).isNotNull();
        assertThat(initiated.getUpdatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should throw IllegalStateException when initiating order with non-null status")
    void shouldThrowExceptionWhenInitiatingOrderWithExistingStatus() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .status(PaymentStatus.PENDING)
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> order.initiate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot initiate payment order. Current status: PENDING");
    }
    
    // ==================== Helpers ====================
    
    private PaymentOrder createValidPaymentOrder() {
        return createValidPaymentOrderBuilder().build();
    }
    
    private PaymentOrder.PaymentOrderBuilder createValidPaymentOrderBuilder() {
        return PaymentOrder.builder()
            .paymentOrderReference("PO-12345")
            .externalReference(ExternalReference.of("EXT-1"))
            .payerReference(PayerReference.of("PAYER-123"))
            .payeeReference(PayeeReference.of("PAYEE-456"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("100.50"), "USD"))
            .remittanceInformation("Test payment")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now());
    }
}

