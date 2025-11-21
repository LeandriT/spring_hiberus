package com.bank.paymentinitiation.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
 * Entidad JPA que representa una orden de pago en la base de datos.
 * 
 * <p>Esta entidad mapea el agregado PaymentOrder del dominio a una representación
 * persistible en base de datos. Usa UUID como clave primaria técnica y
 * paymentOrderReference como identificador de negocio único.
 */
@Entity
@Table(name = "payment_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "payment_order_reference", unique = true, nullable = false, length = 100)
    private String paymentOrderReference;

    @Column(name = "external_reference", nullable = false, length = 100)
    private String externalReference;

    @Column(name = "payer_reference", nullable = false, length = 100)
    private String payerReference;

    @Column(name = "payee_reference", nullable = false, length = 100)
    private String payeeReference;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "remittance_information", length = 500)
    private String remittanceInformation;

    @Column(name = "requested_execution_date", nullable = false)
    private LocalDate requestedExecutionDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

