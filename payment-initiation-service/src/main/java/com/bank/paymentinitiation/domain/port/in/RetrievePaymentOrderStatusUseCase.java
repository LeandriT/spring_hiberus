package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentStatus;

/**
 * Puerto de entrada (caso de uso) para recuperar solo el estado de una orden de pago.
 * 
 * Este puerto define el contrato para obtener únicamente la información de estado
 * de una orden de pago existente, optimizado para consultas que solo requieren el estado.
 * 
 * La implementación de este puerto deberá:
 * - Buscar la orden en el repositorio por su referencia de negocio
 * - Retornar solo el estado de la orden
 * - Lanzar excepción si la orden no existe
 * 
 * @author Payment Initiation Service Team
 */
public interface RetrievePaymentOrderStatusUseCase {
    
    /**
     * Recupera el estado de una orden de pago por su referencia.
     * 
     * @param paymentOrderReference La referencia de la orden de pago (no debe ser null ni vacío)
     * @return El estado actual de la orden de pago
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException 
     *         Si la orden no existe
     */
    PaymentStatus retrieveStatus(String paymentOrderReference);
}

