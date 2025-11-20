package com.bank.paymentinitiation.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

/**
 * Aggregate root representing a Payment Order in the domain.
 * 
 * This is the main entity in the Payment Initiation domain, following
 * Domain-Driven Design principles. It encapsulates the business logic
 * and invariants related to payment orders.
 * 
 * A PaymentOrder represents a request to transfer funds from a payer
 * (debtor) to a payee (creditor), following BIAN Payment Initiation standards.
 * 
 * This class should remain framework-agnostic (no Spring/JPA annotations).
 * 
 * Business Rules:
 * - Payment amount must be positive
 * - Status transitions must follow valid sequences
 * - Requested execution date should not be in the past (validation in domain service)
 */
@Value
@Builder(toBuilder = true)
public class PaymentOrder {
    
    String paymentOrderReference;
    ExternalReference externalReference;
    PayerReference payerReference;
    PayeeReference payeeReference;
    PaymentAmount instructedAmount;
    String remittanceInformation;
    LocalDate requestedExecutionDate;
    PaymentStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    
    /**
     * Validates the payment order according to business rules.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (paymentOrderReference == null || paymentOrderReference.isBlank()) {
            throw new IllegalArgumentException("Payment order reference cannot be null or empty");
        }
        if (externalReference == null) {
            throw new IllegalArgumentException("External reference cannot be null");
        }
        if (payerReference == null) {
            throw new IllegalArgumentException("Payer reference cannot be null");
        }
        if (payeeReference == null) {
            throw new IllegalArgumentException("Payee reference cannot be null");
        }
        if (instructedAmount == null) {
            throw new IllegalArgumentException("Instructed amount cannot be null");
        }
        if (requestedExecutionDate == null) {
            throw new IllegalArgumentException("Requested execution date cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at timestamp cannot be null");
        }
    }
    
    /**
     * Initiates the payment order by setting status to INITIATED.
     * 
     * @return a new PaymentOrder instance with status INITIATED
     */
    public PaymentOrder initiate() {
        if (this.status != null && this.status != PaymentStatus.INITIATED) {
            throw new IllegalStateException(
                "Cannot initiate payment order. Current status: " + this.status
            );
        }
        return this.toBuilder()
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * Changes the status of the payment order, respecting valid transitions.
     * 
     * Valid transitions:
     * - INITIATED → PENDING
     * - INITIATED → CANCELLED
     * - PENDING → PROCESSED
     * - PENDING → CANCELLED
     * - PENDING → FAILED
     * - PROCESSED → COMPLETED
     * - PROCESSED → FAILED
     * 
     * @param newStatus the new status to transition to
     * @return a new PaymentOrder instance with the new status
     * @throws IllegalStateException if the transition is not valid
     */
    public PaymentOrder changeStatus(PaymentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        
        if (this.status == newStatus) {
            return this; // No change needed
        }
        
        validateStatusTransition(this.status, newStatus);
        
        return this.toBuilder()
            .status(newStatus)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * Validates if a status transition is allowed.
     * 
     * @param currentStatus the current status
     * @param newStatus the new status
     * @throws IllegalStateException if the transition is not valid
     */
    private void validateStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        if (currentStatus == null) {
            // If no current status, allow any initial status
            return;
        }
        
        switch (currentStatus) {
            case INITIATED:
                if (newStatus != PaymentStatus.PENDING && newStatus != PaymentStatus.CANCELLED) {
                    throw new IllegalStateException(
                        String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
                    );
                }
                break;
                
            case PENDING:
                if (newStatus != PaymentStatus.PROCESSED && 
                    newStatus != PaymentStatus.CANCELLED && 
                    newStatus != PaymentStatus.FAILED) {
                    throw new IllegalStateException(
                        String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
                    );
                }
                break;
                
            case PROCESSED:
                if (newStatus != PaymentStatus.COMPLETED && newStatus != PaymentStatus.FAILED) {
                    throw new IllegalStateException(
                        String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
                    );
                }
                break;
                
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                // Terminal states - no transitions allowed
                throw new IllegalStateException(
                    String.format("Cannot transition from terminal status %s to %s", currentStatus, newStatus)
                );
                
            default:
                throw new IllegalStateException("Unknown status: " + currentStatus);
        }
    }
}
