package com.bank.paymentinitiation.adapter.out.persistence.entity;

/**
 * Enum for payment order status in the persistence layer.
 * 
 * This enum mirrors the domain PaymentStatus enum but is separate
 * to maintain independence between persistence and domain layers.
 * 
 * The mapping between this enum and the domain PaymentStatus enum
 * is handled by PaymentOrderPersistenceMapper.
 */
public enum PaymentOrderStatusEntity {
    INITIATED,
    PENDING,
    PROCESSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

