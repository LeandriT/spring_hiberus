package com.bank.paymentinitiation.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for PaymentOrder aggregate root.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Validation of creation and required fields</li>
 *   <li>State transitions and business rules</li>
 *   <li>Invariant enforcement</li>
 * </ul>
 */
@DisplayName("PaymentOrder Aggregate Root Tests")
class PaymentOrderTest {

    // Test data builders/helpers
    private static final String VALID_REFERENCE = "PO-A1B2C3D4E5F6G7H8";
    private static final String VALID_EXTERNAL_REF = "EXT-123";
    private static final String VALID_PAYER_REF = "EC123456789012345678";
    private static final String VALID_PAYEE_REF = "EC987654321098765432";
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(1);

    private PaymentOrder createValidPaymentOrderBuilder() {
        return PaymentOrder.builder()
            .paymentOrderReference(VALID_REFERENCE)
            .externalReference(new ExternalReference(VALID_EXTERNAL_REF))
            .payerReference(new PayerReference(VALID_PAYER_REF))
            .payeeReference(new PayeeReference(VALID_PAYEE_REF))
            .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(FUTURE_DATE)
            .build();
    }

    // ==================== Validations Tests ====================

    @Test
    @DisplayName("Should create payment order successfully with valid fields")
    void shouldCreatePaymentOrderSuccessfullyWithValidFields() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();

        // Act
        order.initiate();
        order.validate();

        // Assert
        Assertions.assertThat(order.getPaymentOrderReference()).isEqualTo(VALID_REFERENCE);
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        Assertions.assertThat(order.getCreatedAt()).isNotNull();
        Assertions.assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when paymentOrderReference is null")
    void shouldThrowExceptionWhenPaymentOrderReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .paymentOrderReference(null)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payment order reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when paymentOrderReference is blank")
    void shouldThrowExceptionWhenPaymentOrderReferenceIsBlank() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .paymentOrderReference("   ")
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payment order reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when externalReference is null")
    void shouldThrowExceptionWhenExternalReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .externalReference(null)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("External reference must not be null");
    }

    @Test
    @DisplayName("Should throw exception when payerReference is null")
    void shouldThrowExceptionWhenPayerReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .payerReference(null)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payer reference must not be null");
    }

    @Test
    @DisplayName("Should throw exception when payeeReference is null")
    void shouldThrowExceptionWhenPayeeReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .payeeReference(null)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payee reference must not be null");
    }

    @Test
    @DisplayName("Should throw exception when instructedAmount is null")
    void shouldThrowExceptionWhenInstructedAmountIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .instructedAmount(null)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Instructed amount must not be null");
    }

    @Test
    @DisplayName("Should throw exception when requestedExecutionDate is null")
    void shouldThrowExceptionWhenRequestedExecutionDateIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .requestedExecutionDate(null)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Requested execution date must not be null");
    }

    @Test
    @DisplayName("Should throw exception when requestedExecutionDate is in the past")
    void shouldThrowExceptionWhenRequestedExecutionDateIsInThePast() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(1);
        PaymentOrder order = createValidPaymentOrderBuilder()
            .toBuilder()
            .requestedExecutionDate(pastDate)
            .build();

        // Act & Assert
        order.initiate();
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Requested execution date must not be in the past");
    }

    @Test
    @DisplayName("Should throw exception when validate is called before initiate")
    void shouldThrowExceptionWhenValidateIsCalledBeforeInitiate() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();

        // Act & Assert - validate() requires status and createdAt to be set by initiate()
        Assertions.assertThatThrownBy(() -> order.validate())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Status must not be null");
    }

    // ==================== State Transition Tests ====================

    @Test
    @DisplayName("Should transition from INITIATED to PENDING successfully")
    void shouldTransitionFromInitiatedToPendingSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        // Act
        // Wait a bit to ensure updatedAt changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        order.changeStatus(PaymentStatus.PENDING);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.PENDING);
        Assertions.assertThat(order.getUpdatedAt()).isAfter(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should transition from PENDING to PROCESSED successfully")
    void shouldTransitionFromPendingToProcessedSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        // Act
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        order.changeStatus(PaymentStatus.PROCESSED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.PROCESSED);
        Assertions.assertThat(order.getUpdatedAt()).isAfter(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should transition from PROCESSED to COMPLETED successfully")
    void shouldTransitionFromProcessedToCompletedSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        // Act
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        order.changeStatus(PaymentStatus.COMPLETED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        Assertions.assertThat(order.getUpdatedAt()).isAfter(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should throw exception when transitioning from INITIATED to COMPLETED")
    void shouldThrowExceptionWhenTransitioningFromInitiatedToCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.COMPLETED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from INITIATED to COMPLETED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from PENDING to COMPLETED")
    void shouldThrowExceptionWhenTransitioningFromPendingToCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.COMPLETED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from PENDING to COMPLETED");
    }

    @Test
    @DisplayName("Should allow transition to FAILED from any state")
    void shouldAllowTransitionToFailedFromAnyState() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();

        // Act
        order.changeStatus(PaymentStatus.FAILED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition to CANCELLED from INITIATED")
    void shouldAllowTransitionToCancelledFromInitiated() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();

        // Act
        order.changeStatus(PaymentStatus.CANCELLED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should allow transition to CANCELLED from PENDING")
    void shouldAllowTransitionToCancelledFromPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);

        // Act
        order.changeStatus(PaymentStatus.CANCELLED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw exception when cancelling COMPLETED order")
    void shouldThrowExceptionWhenCancellingCompletedOrder() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);
        order.changeStatus(PaymentStatus.COMPLETED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.CANCELLED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot cancel payment order in status COMPLETED");
    }

    @Test
    @DisplayName("Should throw exception when cancelling FAILED order")
    void shouldThrowExceptionWhenCancellingFailedOrder() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.FAILED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.CANCELLED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot cancel payment order in status FAILED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from COMPLETED state")
    void shouldThrowExceptionWhenTransitioningFromCompletedState() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);
        order.changeStatus(PaymentStatus.COMPLETED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PROCESSED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal state COMPLETED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from FAILED state")
    void shouldThrowExceptionWhenTransitioningFromFailedState() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.FAILED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal state FAILED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from CANCELLED state")
    void shouldThrowExceptionWhenTransitioningFromCancelledState() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.CANCELLED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal state CANCELLED");
    }

    @Test
    @DisplayName("Should update updatedAt when status changes")
    void shouldUpdateUpdatedAtWhenStatusChanges() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        // Act
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        order.changeStatus(PaymentStatus.PENDING);

        // Assert
        Assertions.assertThat(order.getUpdatedAt()).isAfter(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should set status to INITIATED and timestamps when initiate is called")
    void shouldSetStatusToInitiatedAndTimestampsWhenInitiateIsCalled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();

        // Act
        order.initiate();

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        Assertions.assertThat(order.getCreatedAt()).isNotNull();
        Assertions.assertThat(order.getUpdatedAt()).isNotNull();
        // Both timestamps should be set and updatedAt should be equal or after createdAt
        Assertions.assertThat(order.getUpdatedAt()).isAfterOrEqualTo(order.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw exception when newStatus is null in changeStatus")
    void shouldThrowExceptionWhenNewStatusIsNullInChangeStatus() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("New status must not be null");
    }

    @Test
    @DisplayName("Should throw exception when current status is null in changeStatus")
    void shouldThrowExceptionWhenCurrentStatusIsNullInChangeStatus() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        // Don't call initiate() so status remains null

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot change status: current status is null");
    }

    @Test
    @DisplayName("Should allow transition to FAILED from PENDING")
    void shouldAllowTransitionToFailedFromPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);

        // Act
        order.changeStatus(PaymentStatus.FAILED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition to FAILED from PROCESSED")
    void shouldAllowTransitionToFailedFromProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);

        // Act
        order.changeStatus(PaymentStatus.FAILED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition to FAILED from COMPLETED")
    void shouldAllowTransitionToFailedFromCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);
        order.changeStatus(PaymentStatus.COMPLETED);

        // Act
        order.changeStatus(PaymentStatus.FAILED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition to CANCELLED from PROCESSED")
    void shouldAllowTransitionToCancelledFromProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);

        // Act
        order.changeStatus(PaymentStatus.CANCELLED);

        // Assert
        Assertions.assertThat(order.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw exception when transitioning from PENDING to INITIATED")
    void shouldThrowExceptionWhenTransitioningFromPendingToInitiated() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.INITIATED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from PENDING to INITIATED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from PROCESSED to PENDING")
    void shouldThrowExceptionWhenTransitioningFromProcessedToPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from PROCESSED to PENDING");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from COMPLETED to PENDING")
    void shouldThrowExceptionWhenTransitioningFromCompletedToPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrderBuilder();
        order.initiate();
        order.changeStatus(PaymentStatus.PENDING);
        order.changeStatus(PaymentStatus.PROCESSED);
        order.changeStatus(PaymentStatus.COMPLETED);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from terminal state COMPLETED");
    }
}

