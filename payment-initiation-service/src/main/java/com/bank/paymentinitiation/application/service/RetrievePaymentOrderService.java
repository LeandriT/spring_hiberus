package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

/**
 * Implementación del caso de uso para recuperar una orden de pago completa.
 * 
 * Este servicio de aplicación implementa RetrievePaymentOrderUseCase y coordina
 * la búsqueda y recuperación de una orden de pago por su referencia.
 * 
 * Responsabilidades:
 * - Buscar la orden en el repositorio
 * - Validar que la orden existe
 * - Lanzar excepción de dominio si no se encuentra
 * 
 * @author Payment Initiation Service Team
 */
public class RetrievePaymentOrderService implements RetrievePaymentOrderUseCase {
    
    private final PaymentOrderRepository paymentOrderRepository;
    
    /**
     * Constructor que recibe el repositorio de órdenes de pago.
     * 
     * @param paymentOrderRepository El repositorio de dominio (no debe ser null)
     */
    public RetrievePaymentOrderService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }
    
    @Override
    public PaymentOrder retrieve(String paymentOrderReference) {
        // TODO: Implementar búsqueda y validación
        return paymentOrderRepository.findByReference(paymentOrderReference)
            .orElseThrow(() -> new com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException(
                "Payment order not found with reference: " + paymentOrderReference
            ));
    }
}

