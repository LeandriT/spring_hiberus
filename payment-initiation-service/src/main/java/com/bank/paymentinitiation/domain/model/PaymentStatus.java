package com.bank.paymentinitiation.domain.model;

/**
 * Estado de una orden de pago según el estándar BIAN Payment Initiation.
 * 
 * <p>Estados posibles y transiciones válidas:
 * <ul>
 *   <li>INITIATED: Orden creada y aceptada inicialmente</li>
 *   <li>PENDING: Orden pendiente de procesamiento</li>
 *   <li>PROCESSED: Orden procesada pero no completada</li>
 *   <li>COMPLETED: Orden completada exitosamente</li>
 *   <li>FAILED: Orden fallida durante el procesamiento</li>
 *   <li>CANCELLED: Orden cancelada</li>
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

