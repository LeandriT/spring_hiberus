package com.bank.paymentinitiation.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment Order JPA Entity.
 * 
 * <p>Represents the persistence model for PaymentOrder domain objects.
 * This entity is used by JPA to map PaymentOrder data to the database.
 * 
 * <p>Important: This entity is part of the infrastructure layer and should
 * not contain business logic. All business logic resides in the domain model
 * (PaymentOrder aggregate root).
 * 
 * <p>Field mappings:
 * <ul>
 *   <li>id: UUID primary key (generated)</li>
 *   <li>paymentOrderReference: Unique business identifier (e.g., "PO-XXXXXXXX")</li>
 *   <li>externalReference: External reference from client (String)</li>
 *   <li>payerReference: Payer account reference (String, typically IBAN)</li>
 *   <li>payeeReference: Payee account reference (String, typically IBAN)</li>
 *   <li>amount: Payment amount (BigDecimal)</li>
 *   <li>currency: Currency code (String, e.g., "USD", "EUR")</li>
 *   <li>remittanceInformation: Remittance information (String, optional)</li>
 *   <li>requestedExecutionDate: Requested execution date (LocalDate)</li>
 *   <li>status: Payment status (String, enum name: INITIATED, PENDING, etc.)</li>
 *   <li>createdAt: Creation timestamp (LocalDateTime)</li>
 *   <li>updatedAt: Last update timestamp (LocalDateTime)</li>
 * </ul>
 */
@Entity
@Table(name = "payment_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderEntity {

    /**
     * Primary key (UUID).
     * Generated automatically when saving a new entity.
     */
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Unique business identifier for the payment order.
     * This is the paymentOrderReference from the domain model.
     * Example: "PO-A1B2C3D4E5F6G7H8..."
     */
    @Column(name = "payment_order_reference", unique = true, nullable = false, length = 100)
    private String paymentOrderReference;

    /**
     * External reference provided by the client.
     */
    @Column(name = "external_reference", nullable = false, length = 255)
    private String externalReference;

    /**
     * Payer (debtor) account reference (typically IBAN).
     */
    @Column(name = "payer_reference", nullable = false, length = 50)
    private String payerReference;

    /**
     * Payee (creditor) account reference (typically IBAN).
     */
    @Column(name = "payee_reference", nullable = false, length = 50)
    private String payeeReference;

    /**
     * Payment amount.
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code (ISO 4217, e.g., "USD", "EUR").
     */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Remittance information (optional).
     */
    @Column(name = "remittance_information", length = 500)
    private String remittanceInformation;

    /**
     * Requested execution date.
     */
    @Column(name = "requested_execution_date", nullable = false)
    private LocalDate requestedExecutionDate;

    /**
     * Payment status (enum name as String: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED).
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Creation timestamp.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

