package com.bank.paymentinitiation.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Aggregate root que representa una orden de pago en el dominio de negocio.
 * 
 * PaymentOrder es el agregado principal del dominio Payment Initiation. Contiene
 * toda la información necesaria para procesar una orden de pago y gestiona su
 * ciclo de vida mediante cambios de estado controlados.
 * 
 * Este agregado:
 * - Valida sus invariantes de negocio
 * - Controla las transiciones de estado
 * - Protege su integridad mediante métodos de dominio
 * 
 * No depende de frameworks (Spring, JPA, etc.) y es puro Java.
 * 
 * @author Payment Initiation Service Team
 */
public final class PaymentOrder {
    
    private final String paymentOrderReference;
    private final ExternalReference externalReference;
    private final PayerReference payerReference;
    private final PayeeReference payeeReference;
    private final PaymentAmount instructedAmount;
    private final String remittanceInformation;
    private final LocalDate requestedExecutionDate;
    private PaymentStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Constructor privado. Usar el builder o métodos estáticos para crear instancias.
     */
    private PaymentOrder(String paymentOrderReference,
                        ExternalReference externalReference,
                        PayerReference payerReference,
                        PayeeReference payeeReference,
                        PaymentAmount instructedAmount,
                        String remittanceInformation,
                        LocalDate requestedExecutionDate,
                        PaymentStatus status,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.paymentOrderReference = paymentOrderReference;
        this.externalReference = externalReference;
        this.payerReference = payerReference;
        this.payeeReference = payeeReference;
        this.instructedAmount = instructedAmount;
        this.remittanceInformation = remittanceInformation;
        this.requestedExecutionDate = requestedExecutionDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Crea una nueva orden de pago y la inicializa con estado INITIATED.
     * 
     * @param paymentOrderReference La referencia única de la orden (generada por el sistema)
     * @param externalReference La referencia externa proporcionada por el cliente
     * @param payerReference La referencia del pagador/deudor
     * @param payeeReference La referencia del beneficiario/acreedor
     * @param instructedAmount El monto instruido del pago
     * @param remittanceInformation La información de remesas (opcional)
     * @param requestedExecutionDate La fecha solicitada de ejecución
     * @return Una nueva orden de pago con estado INITIATED
     * @throws IllegalArgumentException Si algún parámetro requerido es inválido
     */
    public static PaymentOrder create(String paymentOrderReference,
                                     ExternalReference externalReference,
                                     PayerReference payerReference,
                                     PayeeReference payeeReference,
                                     PaymentAmount instructedAmount,
                                     String remittanceInformation,
                                     LocalDate requestedExecutionDate) {
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder order = new PaymentOrder(
            paymentOrderReference,
            externalReference,
            payerReference,
            payeeReference,
            instructedAmount,
            remittanceInformation,
            requestedExecutionDate,
            PaymentStatus.INITIATED,
            now,
            now
        );
        order.validate();
        return order;
    }
    
    /**
     * Valida los invariantes del agregado PaymentOrder.
     * 
     * @throws IllegalStateException Si algún invariante no se cumple
     */
    public void validate() {
        if (paymentOrderReference == null || paymentOrderReference.trim().isEmpty()) {
            throw new IllegalStateException("Payment order reference cannot be null or empty");
        }
        if (externalReference == null) {
            throw new IllegalStateException("External reference cannot be null");
        }
        if (payerReference == null) {
            throw new IllegalStateException("Payer reference cannot be null");
        }
        if (payeeReference == null) {
            throw new IllegalStateException("Payee reference cannot be null");
        }
        if (instructedAmount == null) {
            throw new IllegalStateException("Instructed amount cannot be null");
        }
        if (requestedExecutionDate == null) {
            throw new IllegalStateException("Requested execution date cannot be null");
        }
        if (status == null) {
            throw new IllegalStateException("Payment status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalStateException("Created at timestamp cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalStateException("Updated at timestamp cannot be null");
        }
        // Validar que la fecha de ejecución solicitada no sea en el pasado
        if (requestedExecutionDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Requested execution date cannot be in the past");
        }
    }
    
    /**
     * Cambia el estado de la orden respetando las transiciones válidas.
     * 
     * Transiciones válidas:
     * - INITIATED → PENDING
     * - PENDING → PROCESSED
     * - PROCESSED → COMPLETED
     * - Cualquier estado → FAILED (si hay error)
     * - Cualquier estado (excepto COMPLETED, FAILED) → CANCELLED
     * 
     * @param newStatus El nuevo estado deseado
     * @throws IllegalStateException Si la transición no es válida
     */
    public void changeStatus(PaymentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        
        if (this.status == newStatus) {
            return; // No hay cambio
        }
        
        // Validar transiciones
        if (!isValidTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid status transition from %s to %s", this.status, newStatus)
            );
        }
        
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Valida si una transición de estado es válida.
     * 
     * @param currentStatus El estado actual
     * @param newStatus El nuevo estado
     * @return true si la transición es válida, false en caso contrario
     */
    private boolean isValidTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        // Transiciones hacia FAILED siempre son válidas (excepto desde terminales)
        if (newStatus == PaymentStatus.FAILED) {
            return currentStatus != PaymentStatus.COMPLETED && currentStatus != PaymentStatus.CANCELLED;
        }
        
        // Transiciones hacia CANCELLED son válidas desde estados no terminales
        if (newStatus == PaymentStatus.CANCELLED) {
            return currentStatus != PaymentStatus.COMPLETED && currentStatus != PaymentStatus.FAILED;
        }
        
        // Transiciones normales del flujo
        switch (currentStatus) {
            case INITIATED:
                return newStatus == PaymentStatus.PENDING;
            case PENDING:
                return newStatus == PaymentStatus.PROCESSED;
            case PROCESSED:
                return newStatus == PaymentStatus.COMPLETED;
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                return false; // Estados terminales
            default:
                return false;
        }
    }
    
    // Getters
    
    public String getPaymentOrderReference() {
        return paymentOrderReference;
    }
    
    public ExternalReference getExternalReference() {
        return externalReference;
    }
    
    public PayerReference getPayerReference() {
        return payerReference;
    }
    
    public PayeeReference getPayeeReference() {
        return payeeReference;
    }
    
    public PaymentAmount getInstructedAmount() {
        return instructedAmount;
    }
    
    public String getRemittanceInformation() {
        return remittanceInformation;
    }
    
    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentOrder that = (PaymentOrder) o;
        return Objects.equals(paymentOrderReference, that.paymentOrderReference);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentOrderReference);
    }
    
    @Override
    public String toString() {
        return "PaymentOrder{" +
                "paymentOrderReference='" + paymentOrderReference + '\'' +
                ", status=" + status +
                ", instructedAmount=" + instructedAmount +
                ", requestedExecutionDate=" + requestedExecutionDate +
                '}';
    }
}

