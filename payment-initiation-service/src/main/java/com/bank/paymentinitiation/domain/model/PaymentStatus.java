package com.bank.paymentinitiation.domain.model;

/**
 * Enumeration representing the possible statuses of a Payment Order.
 * 
 * This enum follows the BIAN Payment Initiation lifecycle:
 * - INITIATED: Order has been created and accepted
 * - PENDING: Order is pending processing
 * - PROCESSED: Order has been processed
 * - COMPLETED: Order has been completed/settled
 * - FAILED: Order failed or was rejected
 * - CANCELLED: Order has been cancelled
 * 
 * Status transitions should be validated in the domain model.
 * Valid transitions:
 * - INITIATED → PENDING → PROCESSED → COMPLETED
 * - INITIATED → PENDING → FAILED
 * - INITIATED → CANCELLED
 * - PENDING → CANCELLED
 */
public enum PaymentStatus {
    INITIATED,
    PENDING,
    PROCESSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

