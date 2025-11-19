package com.bank.paymentinitiation.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el agregado PaymentOrder.
 * 
 * Cubre:
 * - Validaciones de invariantes
 * - Cambios de estado y transiciones válidas
 * - Excepciones en casos de error
 */
@DisplayName("PaymentOrder Aggregate Tests")
class PaymentOrderTest {
    
    private static final String VALID_REFERENCE = "PO-0001";
    private static final String VALID_EXTERNAL_REF = "EXT-1";
    private static final String VALID_PAYER_REF = "PAYER-123";
    private static final String VALID_PAYEE_REF = "PAYEE-456";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("150.75");
    private static final String VALID_CURRENCY = "USD";
    private static final String VALID_REMITTANCE_INFO = "Factura 001";
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);
    
    @Nested
    @DisplayName("Creación de PaymentOrder")
    class CreationTests {
        
        @Test
        @DisplayName("Debería crear una orden de pago válida con estado INITIATED")
        void shouldCreateValidPaymentOrderWithInitiatedStatus() {
            // Given
            ExternalReference externalRef = ExternalReference.of(VALID_EXTERNAL_REF);
            PayerReference payerRef = PayerReference.of(VALID_PAYER_REF);
            PayeeReference payeeRef = PayeeReference.of(VALID_PAYEE_REF);
            PaymentAmount amount = PaymentAmount.of(VALID_AMOUNT, VALID_CURRENCY);
            
            // When
            PaymentOrder order = PaymentOrder.create(
                VALID_REFERENCE,
                externalRef,
                payerRef,
                payeeRef,
                amount,
                VALID_REMITTANCE_INFO,
                FUTURE_DATE
            );
            
            // Then
            assertThat(order).isNotNull();
            assertThat(order.getPaymentOrderReference()).isEqualTo(VALID_REFERENCE);
            assertThat(order.getExternalReference()).isEqualTo(externalRef);
            assertThat(order.getPayerReference()).isEqualTo(payerRef);
            assertThat(order.getPayeeReference()).isEqualTo(payeeRef);
            assertThat(order.getInstructedAmount()).isEqualTo(amount);
            assertThat(order.getRemittanceInformation()).isEqualTo(VALID_REMITTANCE_INFO);
            assertThat(order.getRequestedExecutionDate()).isEqualTo(FUTURE_DATE);
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.INITIATED);
            assertThat(order.getCreatedAt()).isNotNull();
            assertThat(order.getUpdatedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("Debería crear una orden con remittanceInformation null")
        void shouldCreateOrderWithNullRemittanceInformation() {
            // Given
            ExternalReference externalRef = ExternalReference.of(VALID_EXTERNAL_REF);
            PayerReference payerRef = PayerReference.of(VALID_PAYER_REF);
            PayeeReference payeeRef = PayeeReference.of(VALID_PAYEE_REF);
            PaymentAmount amount = PaymentAmount.of(VALID_AMOUNT, VALID_CURRENCY);
            
            // When
            PaymentOrder order = PaymentOrder.create(
                VALID_REFERENCE,
                externalRef,
                payerRef,
                payeeRef,
                amount,
                null,
                FUTURE_DATE
            );
            
            // Then
            assertThat(order).isNotNull();
            assertThat(order.getRemittanceInformation()).isNull();
        }
        
        @Test
        @DisplayName("Debería lanzar excepción si la fecha de ejecución es en el pasado")
        void shouldThrowExceptionWhenExecutionDateIsInPast() {
            // Given
            ExternalReference externalRef = ExternalReference.of(VALID_EXTERNAL_REF);
            PayerReference payerRef = PayerReference.of(VALID_PAYER_REF);
            PayeeReference payeeRef = PayeeReference.of(VALID_PAYEE_REF);
            PaymentAmount amount = PaymentAmount.of(VALID_AMOUNT, VALID_CURRENCY);
            LocalDate pastDate = LocalDate.now().minusDays(1);
            
            // When/Then
            assertThatThrownBy(() -> PaymentOrder.create(
                VALID_REFERENCE,
                externalRef,
                payerRef,
                payeeRef,
                amount,
                VALID_REMITTANCE_INFO,
                pastDate
            ))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Requested execution date cannot be in the past");
        }
    }
    
    @Nested
    @DisplayName("Validaciones")
    class ValidationTests {
        
        @Test
        @DisplayName("Debería pasar validación para orden válida")
        void shouldPassValidationForValidOrder() {
            // Given
            PaymentOrder order = createValidOrder();
            
            // When/Then
            assertThatCode(() -> order.validate())
                .doesNotThrowAnyException();
        }
        
        @Test
        @DisplayName("Debería lanzar excepción si paymentOrderReference es null")
        void shouldThrowExceptionWhenReferenceIsNull() {
            // Given
            PaymentOrder order = createValidOrder();
            // Usar reflexión para establecer reference a null (solo para test)
            // Alternativamente, podríamos tener un método de test helper
            // Por ahora, testear que create() lanza excepción con null
            assertThatThrownBy(() -> PaymentOrder.create(
                null,
                ExternalReference.of(VALID_EXTERNAL_REF),
                PayerReference.of(VALID_PAYER_REF),
                PayeeReference.of(VALID_PAYEE_REF),
                PaymentAmount.of(VALID_AMOUNT, VALID_CURRENCY),
                VALID_REMITTANCE_INFO,
                FUTURE_DATE
            ))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payment order reference cannot be null or empty");
        }
    }
    
    @Nested
    @DisplayName("Cambios de Estado")
    class StatusTransitionTests {
        
        @Test
        @DisplayName("Debería cambiar de INITIATED a PENDING")
        void shouldTransitionFromInitiatedToPending() {
            // Given
            PaymentOrder order = createValidOrder();
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.INITIATED);
            
            // When
            order.changeStatus(PaymentStatus.PENDING);
            
            // Then
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
        }
        
        @Test
        @DisplayName("Debería cambiar de PENDING a PROCESSED")
        void shouldTransitionFromPendingToProcessed() {
            // Given
            PaymentOrder order = createValidOrder();
            order.changeStatus(PaymentStatus.PENDING);
            
            // When
            order.changeStatus(PaymentStatus.PROCESSED);
            
            // Then
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.PROCESSED);
        }
        
        @Test
        @DisplayName("Debería cambiar de PROCESSED a COMPLETED")
        void shouldTransitionFromProcessedToCompleted() {
            // Given
            PaymentOrder order = createValidOrder();
            order.changeStatus(PaymentStatus.PENDING);
            order.changeStatus(PaymentStatus.PROCESSED);
            
            // When
            order.changeStatus(PaymentStatus.COMPLETED);
            
            // Then
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }
        
        @Test
        @DisplayName("Debería permitir cambio a FAILED desde INITIATED")
        void shouldAllowTransitionToFailedFromInitiated() {
            // Given
            PaymentOrder order = createValidOrder();
            
            // When
            order.changeStatus(PaymentStatus.FAILED);
            
            // Then
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }
        
        @Test
        @DisplayName("Debería permitir cambio a CANCELLED desde INITIATED")
        void shouldAllowTransitionToCancelledFromInitiated() {
            // Given
            PaymentOrder order = createValidOrder();
            
            // When
            order.changeStatus(PaymentStatus.CANCELLED);
            
            // Then
            assertThat(order.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        }
        
        @Test
        @DisplayName("Debería lanzar excepción al cambiar de INITIATED a PROCESSED directamente")
        void shouldThrowExceptionWhenSkippingStatus() {
            // Given
            PaymentOrder order = createValidOrder();
            
            // When/Then
            assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PROCESSED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from INITIATED to PROCESSED");
        }
        
        @Test
        @DisplayName("Debería lanzar excepción al cambiar de COMPLETED a otro estado")
        void shouldThrowExceptionWhenChangingFromCompleted() {
            // Given
            PaymentOrder order = createValidOrder();
            order.changeStatus(PaymentStatus.PENDING);
            order.changeStatus(PaymentStatus.PROCESSED);
            order.changeStatus(PaymentStatus.COMPLETED);
            
            // When/Then
            assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition");
        }
        
        @Test
        @DisplayName("Debería lanzar excepción si newStatus es null")
        void shouldThrowExceptionWhenNewStatusIsNull() {
            // Given
            PaymentOrder order = createValidOrder();
            
            // When/Then
            assertThatThrownBy(() -> order.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New status cannot be null");
        }
        
        @Test
        @DisplayName("No debería cambiar updatedAt si el estado es el mismo")
        void shouldNotChangeUpdatedAtWhenStatusIsSame() {
            // Given
            PaymentOrder order = createValidOrder();
            LocalDateTime initialUpdatedAt = order.getUpdatedAt();
            
            // When
            order.changeStatus(PaymentStatus.INITIATED);
            
            // Then
            assertThat(order.getUpdatedAt()).isEqualTo(initialUpdatedAt);
        }
    }
    
    @Nested
    @DisplayName("Equals y HashCode")
    class EqualsAndHashCodeTests {
        
        @Test
        @DisplayName("Debería ser igual si tienen el mismo paymentOrderReference")
        void shouldBeEqualIfSameReference() {
            // Given
            PaymentOrder order1 = createValidOrder();
            PaymentOrder order2 = createValidOrderWithReference(VALID_REFERENCE);
            
            // Then
            assertThat(order1).isEqualTo(order2);
            assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
        }
        
        @Test
        @DisplayName("Debería ser diferente si tienen diferentes paymentOrderReference")
        void shouldBeDifferentIfDifferentReference() {
            // Given
            PaymentOrder order1 = createValidOrder();
            PaymentOrder order2 = createValidOrderWithReference("PO-0002");
            
            // Then
            assertThat(order1).isNotEqualTo(order2);
        }
    }
    
    // Helper methods
    
    private PaymentOrder createValidOrder() {
        return createValidOrderWithReference(VALID_REFERENCE);
    }
    
    private PaymentOrder createValidOrderWithReference(String reference) {
        return PaymentOrder.create(
            reference,
            ExternalReference.of(VALID_EXTERNAL_REF),
            PayerReference.of(VALID_PAYER_REF),
            PayeeReference.of(VALID_PAYEE_REF),
            PaymentAmount.of(VALID_AMOUNT, VALID_CURRENCY),
            VALID_REMITTANCE_INFO,
            FUTURE_DATE
        );
    }
}

