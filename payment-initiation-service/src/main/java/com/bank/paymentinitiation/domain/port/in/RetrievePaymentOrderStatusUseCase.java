package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentStatus;

/**
 * Caso de uso para recuperar solo el estado de una orden de pago.
 * 
 * <p>Este puerto define la operación de recuperar únicamente el estado
 * de una orden de pago existente por su referencia.
 */
public interface RetrievePaymentOrderStatusUseCase {

    /**
     * Recupera el estado de una orden de pago por su referencia.
     *
     * @param paymentOrderReference la referencia de la orden de pago
     * @return el estado de la orden de pago
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException
     *         si la orden no se encuentra
     */
    PaymentStatus retrieveStatus(String paymentOrderReference);
}

