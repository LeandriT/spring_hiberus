package com.bank.paymentinitiation.domain.model;

/**
 * Payment Order Status enumeration.
 * 
 * <p>Represents the possible states of a payment order throughout its lifecycle.
 * 
 * <p>State transitions follow a reasonable sequence:
 * <ul>
 *   <li>INITIATED → PENDING → PROCESSED → COMPLETED</li>
 *   <li>Any state can transition to FAILED or CANCELLED</li>
 * </ul>
 * 
 * <p>States:
 * <ul>
 *   <li>INITIATED: Order has been created and initialized</li>
 *   <li>PENDING: Order is pending processing</li>
 *   <li>PROCESSED: Order has been processed</li>
 *   <li>COMPLETED: Order has been completed successfully</li>
 *   <li>FAILED: Order processing failed</li>
 *   <li>CANCELLED: Order has been cancelled</li>
 * </ul>
 */
public enum PaymentStatus {
    INITIATED,
    PENDING,
    PROCESSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

