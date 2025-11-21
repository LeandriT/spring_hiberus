package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Caso de uso para recuperar una orden de pago completa.
 * 
 * <p>Este puerto define la operación de recuperar una orden de pago
 * existente por su referencia.
 */
public interface RetrievePaymentOrderUseCase {

    /**
     * Recupera una orden de pago por su referencia.
     *
     * @param paymentOrderReference la referencia de la orden de pago
     * @return la orden de pago encontrada
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException
     *         si la orden no se encuentra
     */
    PaymentOrder retrieve(String paymentOrderReference);
}

