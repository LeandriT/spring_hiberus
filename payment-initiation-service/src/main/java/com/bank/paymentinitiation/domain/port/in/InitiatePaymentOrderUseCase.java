package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Caso de uso para iniciar una nueva orden de pago.
 * 
 * <p>Este puerto define la operación de iniciar una orden de pago,
 * que incluye generar la referencia, establecer el estado inicial
 * y persistir la orden.
 */
public interface InitiatePaymentOrderUseCase {

    /**
     * Inicia una nueva orden de pago.
     *
     * @param order la orden de pago a iniciar
     * @return la orden de pago iniciada y persistida
     */
    PaymentOrder initiate(PaymentOrder order);
}

