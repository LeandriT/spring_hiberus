package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Puerto de entrada (caso de uso) para iniciar una orden de pago.
 * 
 * Este puerto define el contrato para crear e iniciar una nueva orden de pago
 * en el dominio de negocio, siguiendo los principios de arquitectura hexagonal.
 * 
 * La implementación de este puerto deberá:
 * - Validar la orden de pago según reglas de negocio
 * - Persistir la orden en el repositorio
 * - Inicializar el estado de la orden como INITIATED
 * 
 * @author Payment Initiation Service Team
 */
public interface InitiatePaymentOrderUseCase {
    
    /**
     * Inicia una nueva orden de pago.
     * 
     * @param order La orden de pago a iniciar (no debe ser null)
     * @return La orden de pago iniciada con su referencia generada
     * @throws com.bank.paymentinitiation.domain.exception.InvalidPaymentException 
     *         Si la orden no cumple las reglas de negocio
     */
    PaymentOrder initiate(PaymentOrder order);
}

