package com.bank.paymentinitiation.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Agregado raíz que representa una orden de pago según el estándar BIAN Payment Initiation.
 * 
 * <p>Este agregado encapsula la lógica de negocio relacionada con órdenes de pago,
 * incluyendo validaciones, transiciones de estado y reglas de dominio.
 * 
 * <p>Invariantes:
 * <ul>
 *   <li>paymentOrderReference no puede ser nulo ni vacío</li>
 *   <li>externalReference no puede ser nulo</li>
 *   <li>payerReference no puede ser nulo</li>
 *   <li>payeeReference no puede ser nulo</li>
 *   <li>instructedAmount no puede ser nulo</li>
 *   <li>requestedExecutionDate no puede ser nulo</li>
 *   <li>status no puede ser nulo (se establece en initiate())</li>
 *   <li>createdAt no puede ser nulo (se establece en initiate())</li>
 *   <li>updatedAt no puede ser nulo (se actualiza en cada cambio de estado)</li>
 * </ul>
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
     * Inicia la orden de pago estableciendo el estado a INITIATED y la fecha de creación.
     * 
     * <p>Este método debe llamarse antes de validar la orden, ya que establece
     * los campos requeridos por validate() (status y createdAt).
     *
     * @return una nueva instancia de PaymentOrder con status INITIATED y createdAt establecido
     */
    public PaymentOrder initiate() {
        LocalDateTime now = LocalDateTime.now();
        return this.toBuilder()
                .status(PaymentStatus.INITIATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Valida las invariantes del agregado.
     * 
     * <p>Este método debe llamarse DESPUÉS de initiate(), ya que requiere
     * que status y createdAt sean no-null.
     *
     * @throws IllegalStateException si alguna invariante no se cumple
     */
    public void validate() {
        if (paymentOrderReference == null || paymentOrderReference.isBlank()) {
            throw new IllegalStateException("Payment order reference cannot be null or blank");
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
            throw new IllegalStateException("Status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalStateException("Created at cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalStateException("Updated at cannot be null");
        }
    }

    /**
     * Cambia el estado de la orden respetando las transiciones válidas.
     * 
     * <p>Transiciones válidas:
     * <ul>
     *   <li>INITIATED → PENDING, CANCELLED</li>
     *   <li>PENDING → PROCESSED, FAILED, CANCELLED</li>
     *   <li>PROCESSED → COMPLETED, FAILED</li>
     *   <li>COMPLETED, FAILED, CANCELLED → (estados finales, no permiten más cambios)</li>
     * </ul>
     *
     * @param newStatus el nuevo estado
     * @return una nueva instancia de PaymentOrder con el nuevo estado y updatedAt actualizado
     * @throws IllegalStateException si la transición no es válida
     */
    public PaymentOrder changeStatus(final PaymentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        if (this.status == newStatus) {
            return this; // No hay cambio
        }

        // Estados finales no permiten cambios
        if (this.status == PaymentStatus.COMPLETED
                || this.status == PaymentStatus.FAILED
                || this.status == PaymentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot change status from final state: " + this.status);
        }

        // Validar transiciones
        boolean isValidTransition = switch (this.status) {
            case INITIATED -> newStatus == PaymentStatus.PENDING
                    || newStatus == PaymentStatus.CANCELLED;
            case PENDING -> newStatus == PaymentStatus.PROCESSED
                    || newStatus == PaymentStatus.FAILED
                    || newStatus == PaymentStatus.CANCELLED;
            case PROCESSED -> newStatus == PaymentStatus.COMPLETED
                    || newStatus == PaymentStatus.FAILED;
            default -> false;
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    "Invalid status transition from " + this.status + " to " + newStatus);
        }

        return this.toBuilder()
                .status(newStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

