package com.bank.paymentinitiation.domain.port.out;

import java.util.Optional;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Puerto de salida (repositorio) para persistir y recuperar órdenes de pago.
 * 
 * <p>Este puerto define las operaciones de persistencia del dominio,
 * sin depender de detalles de implementación (JPA, MongoDB, etc.).
 */
public interface PaymentOrderRepository {

    /**
     * Guarda una orden de pago (crea o actualiza).
     *
     * @param order la orden de pago a guardar
     * @return la orden de pago guardada
     */
    PaymentOrder save(PaymentOrder order);

    /**
     * Busca una orden de pago por su referencia.
     *
     * @param paymentOrderReference la referencia de la orden de pago
     * @return la orden de pago encontrada, o Optional.empty() si no existe
     */
    Optional<PaymentOrder> findByReference(String paymentOrderReference);
}

