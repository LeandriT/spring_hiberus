package com.bank.paymentinitiation.domain.model;

/**
 * Enum que representa los estados posibles de una orden de pago.
 * 
 * Los estados siguen el ciclo de vida típico de una orden de pago:
 * - INITIATED: Orden creada e iniciada
 * - PENDING: Orden pendiente de procesamiento
 * - PROCESSED: Orden procesada (en tránsito)
 * - COMPLETED: Orden completada exitosamente
 * - FAILED: Orden fallida (por validación o procesamiento)
 * - CANCELLED: Orden cancelada
 * 
 * @author Payment Initiation Service Team
 */
public enum PaymentStatus {
    
    /**
     * Orden creada e iniciada. Estado inicial cuando se crea la orden.
     */
    INITIATED,
    
    /**
     * Orden pendiente de procesamiento. En cola para ser procesada.
     */
    PENDING,
    
    /**
     * Orden procesada. En tránsito hacia el banco destino.
     */
    PROCESSED,
    
    /**
     * Orden completada exitosamente. El pago se ha realizado correctamente.
     */
    COMPLETED,
    
    /**
     * Orden fallida. No se pudo procesar debido a un error.
     */
    FAILED,
    
    /**
     * Orden cancelada. Se canceló antes de completarse.
     */
    CANCELLED
}

