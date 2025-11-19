package com.bank.paymentinitiation.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que representa una orden de pago en la base de datos.
 * 
 * Esta entidad es la representación persistente del agregado PaymentOrder del dominio.
 * Se mapea a la tabla payment_order en la base de datos H2.
 * 
 * Características:
 * - UUID como identificador técnico primario
 * - paymentOrderReference como identificador de negocio único
 * - Campos equivalentes al dominio PaymentOrder
 * - Conversión entre value objects del dominio y tipos primitivos/strings
 * 
 * Esta entidad solo existe en la capa de persistencia y NO debe exponerse
 * fuera del adaptador. El mapeo a objetos de dominio se realiza mediante
 * PaymentOrderPersistenceMapper.
 * 
 * @author Payment Initiation Service Team
 */
@Entity
@Table(
    name = "payment_order",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "payment_order_reference")
    }
)
public class PaymentOrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "payment_order_reference", nullable = false, unique = true, length = 100)
    private String paymentOrderReference;
    
    @Column(name = "external_reference", nullable = false, length = 100)
    private String externalReference;
    
    @Column(name = "payer_reference", nullable = false, length = 100)
    private String payerReference;
    
    @Column(name = "payee_reference", nullable = false, length = 100)
    private String payeeReference;
    
    @Column(name = "instructed_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal instructedAmount;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "remittance_information", length = 500)
    private String remittanceInformation;
    
    @Column(name = "requested_execution_date", nullable = false)
    private LocalDate requestedExecutionDate;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEntity status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Constructor por defecto requerido por JPA.
     */
    public PaymentOrderEntity() {
    }
    
    // Getters y Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getPaymentOrderReference() {
        return paymentOrderReference;
    }
    
    public void setPaymentOrderReference(String paymentOrderReference) {
        this.paymentOrderReference = paymentOrderReference;
    }
    
    public String getExternalReference() {
        return externalReference;
    }
    
    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }
    
    public String getPayerReference() {
        return payerReference;
    }
    
    public void setPayerReference(String payerReference) {
        this.payerReference = payerReference;
    }
    
    public String getPayeeReference() {
        return payeeReference;
    }
    
    public void setPayeeReference(String payeeReference) {
        this.payeeReference = payeeReference;
    }
    
    public BigDecimal getInstructedAmount() {
        return instructedAmount;
    }
    
    public void setInstructedAmount(BigDecimal instructedAmount) {
        this.instructedAmount = instructedAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getRemittanceInformation() {
        return remittanceInformation;
    }
    
    public void setRemittanceInformation(String remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }
    
    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }
    
    public void setRequestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
    
    public PaymentStatusEntity getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatusEntity status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Enum para el estado de la orden en la entidad JPA.
     * 
     * Este enum es específico de la capa de persistencia y se mapea
     * al enum PaymentStatus del dominio mediante el mapper.
     */
    public enum PaymentStatusEntity {
        INITIATED,
        PENDING,
        PROCESSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}

