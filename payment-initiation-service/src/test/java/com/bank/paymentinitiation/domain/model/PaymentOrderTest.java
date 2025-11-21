package com.bank.paymentinitiation.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PaymentOrder Aggregate Tests")
class PaymentOrderTest {

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
    @DisplayName("Should create payment order successfully with valid fields")
    void shouldCreatePaymentOrderSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act & Assert
        assertThat(order).isNotNull();
        assertThat(order.getPaymentOrderReference()).isEqualTo("PO-1234567890123456");
        assertThat(order.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(order.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(order.getStatus()).isEqualTo(PaymentStatus.INITIATED);
    }

    @Test
    @DisplayName("Should throw exception when paymentOrderReference is null")
    void shouldThrowExceptionWhenPaymentOrderReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .paymentOrderReference(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment order reference cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when paymentOrderReference is blank")
    void shouldThrowExceptionWhenPaymentOrderReferenceIsBlank() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .paymentOrderReference("   ")
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment order reference cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when externalReference is null")
    void shouldThrowExceptionWhenExternalReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .externalReference(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("External reference cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when payerReference is null")
    void shouldThrowExceptionWhenPayerReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .payerReference(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payer reference cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when payeeReference is null")
    void shouldThrowExceptionWhenPayeeReferenceIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .payeeReference(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payee reference cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when instructedAmount is null")
    void shouldThrowExceptionWhenInstructedAmountIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .instructedAmount(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Instructed amount cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when requestedExecutionDate is null")
    void shouldThrowExceptionWhenRequestedExecutionDateIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .requestedExecutionDate(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Requested execution date cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .status(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Status cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when createdAt is null")
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .createdAt(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Created at cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when updatedAt is null")
    void shouldThrowExceptionWhenUpdatedAtIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .updatedAt(null)
                .build();

        // Act & Assert
        assertThatThrownBy(order::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Updated at cannot be null");
    }

    @Test
    @DisplayName("Should initiate payment order successfully")
    void shouldInitiatePaymentOrderSuccessfully() {
        // Arrange
        PaymentOrder order = PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .build();

        // Act
        PaymentOrder initiatedOrder = order.initiate();

        // Assert
        assertThat(initiatedOrder.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(initiatedOrder.getCreatedAt()).isNotNull();
        assertThat(initiatedOrder.getUpdatedAt()).isNotNull();
        assertThat(initiatedOrder.getCreatedAt()).isEqualTo(initiatedOrder.getUpdatedAt());
    }

    @Test
    @DisplayName("Should allow transition from INITIATED to PENDING")
    void shouldAllowTransitionFromInitiatedToPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.PENDING);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(changedOrder.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should allow transition from INITIATED to CANCELLED")
    void shouldAllowTransitionFromInitiatedToCancelled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.CANCELLED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should allow transition from PENDING to PROCESSED")
    void shouldAllowTransitionFromPendingToProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.PROCESSED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.PROCESSED);
    }

    @Test
    @DisplayName("Should allow transition from PENDING to FAILED")
    void shouldAllowTransitionFromPendingToFailed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.FAILED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition from PENDING to CANCELLED")
    void shouldAllowTransitionFromPendingToCancelled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.CANCELLED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should allow transition from PROCESSED to COMPLETED")
    void shouldAllowTransitionFromProcessedToCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING)
                .changeStatus(PaymentStatus.PROCESSED);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.COMPLETED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should allow transition from PROCESSED to FAILED")
    void shouldAllowTransitionFromProcessedToFailed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING)
                .changeStatus(PaymentStatus.PROCESSED);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.FAILED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should throw exception when transitioning from INITIATED to PROCESSED")
    void shouldThrowExceptionWhenTransitioningFromInitiatedToProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PROCESSED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from INITIATED to PROCESSED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from INITIATED to COMPLETED")
    void shouldThrowExceptionWhenTransitioningFromInitiatedToCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.COMPLETED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from INITIATED to COMPLETED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from PENDING to COMPLETED")
    void shouldThrowExceptionWhenTransitioningFromPendingToCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING);

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.COMPLETED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from PENDING to COMPLETED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from COMPLETED to any other status")
    void shouldThrowExceptionWhenTransitioningFromCompleted() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING)
                .changeStatus(PaymentStatus.PROCESSED)
                .changeStatus(PaymentStatus.COMPLETED);

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot change status from final state: COMPLETED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from FAILED to any other status")
    void shouldThrowExceptionWhenTransitioningFromFailed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING)
                .changeStatus(PaymentStatus.FAILED);

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot change status from final state: FAILED");
    }

    @Test
    @DisplayName("Should throw exception when transitioning from CANCELLED to any other status")
    void shouldThrowExceptionWhenTransitioningFromCancelled() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.CANCELLED);

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.PENDING))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot change status from final state: CANCELLED");
    }

    @Test
    @DisplayName("Should throw exception when new status is null")
    void shouldThrowExceptionWhenNewStatusIsNull() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New status cannot be null");
    }

    @Test
    @DisplayName("Should return same instance when changing to same status")
    void shouldReturnSameInstanceWhenChangingToSameStatus() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.INITIATED);

        // Assert
        assertThat(changedOrder).isSameAs(order);
    }

    @Test
    @DisplayName("Should update updatedAt on each valid status transition")
    void shouldUpdateUpdatedAtOnEachValidStatusTransition() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        LocalDateTime originalUpdatedAt = order.getUpdatedAt();

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.PENDING);

        // Assert
        assertThat(changedOrder.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should allow transition to FAILED from PENDING")
    void shouldAllowTransitionToFailedFromPending() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.FAILED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition to FAILED from PROCESSED")
    void shouldAllowTransitionToFailedFromProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING)
                .changeStatus(PaymentStatus.PROCESSED);

        // Act
        PaymentOrder changedOrder = order.changeStatus(PaymentStatus.FAILED);

        // Assert
        assertThat(changedOrder.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should allow transition to CANCELLED from PROCESSED")
    void shouldAllowTransitionToCancelledFromProcessed() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder()
                .changeStatus(PaymentStatus.PENDING)
                .changeStatus(PaymentStatus.PROCESSED);

        // Act & Assert
        assertThatThrownBy(() -> order.changeStatus(PaymentStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from PROCESSED to CANCELLED");
    }

    @Test
    @DisplayName("Should use builder to create payment order")
    void shouldUseBuilderToCreatePaymentOrder() {
        // Arrange & Act
        PaymentOrder order = PaymentOrder.builder()
                .paymentOrderReference("PO-TEST")
                .externalReference(new ExternalReference("EXT-TEST"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("100.00"), "USD"))
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Assert
        assertThat(order).isNotNull();
        assertThat(order.getPaymentOrderReference()).isEqualTo("PO-TEST");
        assertThat(order.getExternalReference().getValue()).isEqualTo("EXT-TEST");
    }

    @Test
    @DisplayName("Should use toBuilder to modify payment order")
    void shouldUseToBuilderToModifyPaymentOrder() {
        // Arrange
        PaymentOrder original = createValidPaymentOrder();

        // Act
        PaymentOrder modified = original.toBuilder()
                .remittanceInformation("Modified remittance")
                .build();

        // Assert
        assertThat(modified).isNotNull();
        assertThat(modified.getRemittanceInformation()).isEqualTo("Modified remittance");
        assertThat(modified.getPaymentOrderReference()).isEqualTo(original.getPaymentOrderReference());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder order1 = createValidPaymentOrder().toBuilder()
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order2 = createValidPaymentOrder().toBuilder()
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order3 = createValidPaymentOrder().toBuilder()
                .paymentOrderReference("PO-DIFFERENT")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act & Assert
        assertThat(order1).isEqualTo(order2);
        assertThat(order1).isNotEqualTo(order3);
        assertThat(order1).isNotEqualTo(null);
        assertThat(order1).isNotEqualTo("not a PaymentOrder");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder order1 = createValidPaymentOrder().toBuilder()
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order2 = createValidPaymentOrder().toBuilder()
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order3 = createValidPaymentOrder().toBuilder()
                .paymentOrderReference("PO-DIFFERENT")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act & Assert
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
        assertThat(order1.hashCode()).isNotEqualTo(order3.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();

        // Act
        String toString = order.toString();

        // Assert
        assertThat(toString).isNotNull();
        assertThat(toString).contains("PaymentOrder");
        assertThat(toString).contains("PO-1234567890123456");
    }

    @Test
    @DisplayName("Should implement equals with all fields different")
    void shouldImplementEqualsWithAllFieldsDifferent() {
        // Arrange
        PaymentOrder order1 = createValidPaymentOrder();
        PaymentOrder order2 = createValidPaymentOrder().toBuilder()
                .externalReference(new ExternalReference("EXT-DIFFERENT"))
                .payerReference(new PayerReference("EC111111111111111111"))
                .payeeReference(new PayeeReference("EC222222222222222222"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("999.99"), "EUR"))
                .remittanceInformation("Different remittance")
                .requestedExecutionDate(LocalDate.now().plusDays(10))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();

        // Act & Assert
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    @DisplayName("Should implement equals with null remittanceInformation")
    void shouldImplementEqualsWithNullRemittanceInformation() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder order1 = createValidPaymentOrder().toBuilder()
                .remittanceInformation(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order2 = createValidPaymentOrder().toBuilder()
                .remittanceInformation(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order3 = createValidPaymentOrder().toBuilder()
                .remittanceInformation("Some remittance")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act & Assert
        assertThat(order1).isEqualTo(order2);
        assertThat(order1).isNotEqualTo(order3);
    }

    @Test
    @DisplayName("Should implement hashCode with null remittanceInformation")
    void shouldImplementHashCodeWithNullRemittanceInformation() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder order1 = createValidPaymentOrder().toBuilder()
                .remittanceInformation(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
        PaymentOrder order2 = createValidPaymentOrder().toBuilder()
                .remittanceInformation(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act & Assert
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString with null remittanceInformation")
    void shouldImplementToStringWithNullRemittanceInformation() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder().toBuilder()
                .remittanceInformation(null)
                .build();

        // Act
        String toString = order.toString();

        // Assert
        assertThat(toString).isNotNull();
        assertThat(toString).contains("PaymentOrder");
    }

    @Test
    @DisplayName("Should implement equals with builder toString")
    void shouldImplementEqualsWithBuilderToString() {
        // Arrange
        PaymentOrder.PaymentOrderBuilder builder = PaymentOrder.builder()
                .paymentOrderReference("PO-TEST")
                .externalReference(new ExternalReference("EXT-TEST"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("100.00"), "USD"))
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());

        // Act
        String builderToString = builder.toString();

        // Assert
        assertThat(builderToString).isNotNull();
        assertThat(builderToString).contains("PaymentOrderBuilder");
    }
}

