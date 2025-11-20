package com.bank.paymentinitiation.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a Payment Order in the database.
 * 
 * This is the persistence model (infrastructure concern) that maps to
 * the database table. It should NOT be used outside the persistence layer.
 * 
 * The mapping between this entity and the domain model (PaymentOrder)
 * is handled by PaymentOrderPersistenceMapper.
 * 
 * This entity is part of the driven adapter (outbound adapter) in the
 * Hexagonal Architecture.
 * 
 * Design decisions:
 * - UUID as technical ID (primary key)
 * - paymentOrderReference as business identifier (unique, indexed)
 * - Fields flattened from domain value objects (payerReference.value, etc.)
 * - Status stored as String enum
 */
@Entity
@Table(
    name = "payment_orders",
    indexes = {
        @Index(name = "idx_payment_order_reference", columnList = "payment_order_reference", unique = true)
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderEntity {
    
    /**
     * Technical ID (primary key).
     * Generated automatically by JPA.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * Business identifier for the payment order.
     * Unique constraint enforced at database level.
     * Format: PO-{identifier}
     */
    @Column(name = "payment_order_reference", unique = true, nullable = false, length = 100)
    private String paymentOrderReference;
    
    /**
     * External reference provided by the client.
     */
    @Column(name = "external_reference", nullable = false, length = 50)
    private String externalReference;
    
    /**
     * Payer (debtor) reference.
     * Extracted from PayerReference value object in domain.
     */
    @Column(name = "payer_reference", nullable = false, length = 100)
    private String payerReference;
    
    /**
     * Payee (creditor) reference.
     * Extracted from PayeeReference value object in domain.
     */
    @Column(name = "payee_reference", nullable = false, length = 100)
    private String payeeReference;
    
    /**
     * Payment amount.
     * Extracted from PaymentAmount value object in domain.
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    /**
     * Currency code (ISO 4217).
     * Extracted from PaymentAmount value object in domain.
     */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    /**
     * Remittance information (payment purpose/description).
     * Optional field.
     */
    @Column(name = "remittance_information", length = 500)
    private String remittanceInformation;
    
    /**
     * Requested execution date for the payment.
     */
    @Column(name = "requested_execution_date", nullable = false)
    private LocalDate requestedExecutionDate;
    
    /**
     * Current status of the payment order.
     * Stored as String enum (INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentOrderStatusEntity status;
    
    /**
     * Timestamp when the payment order was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp of the last update.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * JPA lifecycle callback to set createdAt before persisting.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * JPA lifecycle callback to set updatedAt before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
