package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Puerto de entrada (caso de uso) para recuperar una orden de pago por su referencia.
 * 
 * Este puerto define el contrato para obtener los datos completos de una orden de pago
 * existente en el dominio de negocio.
 * 
 * La implementación de este puerto deberá:
 * - Buscar la orden en el repositorio por su referencia de negocio
 * - Retornar la orden completa si existe
 * - Lanzar excepción si la orden no existe
 * 
 * @author Payment Initiation Service Team
 */
public interface RetrievePaymentOrderUseCase {
    
    /**
     * Recupera una orden de pago por su referencia.
     * 
     * @param paymentOrderReference La referencia de la orden de pago (no debe ser null ni vacío)
     * @return La orden de pago encontrada
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException 
     *         Si la orden no existe
     */
    PaymentOrder retrieve(String paymentOrderReference);
}

