package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

/**
 * Implementación del caso de uso para recuperar solo el estado de una orden de pago.
 * 
 * Este servicio de aplicación implementa RetrievePaymentOrderStatusUseCase y coordina
 * la búsqueda y recuperación del estado de una orden de pago, optimizado para consultas
 * que solo requieren el estado sin cargar toda la orden.
 * 
 * Responsabilidades:
 * - Buscar la orden en el repositorio
 * - Extraer solo el estado de la orden
 * - Lanzar excepción de dominio si no se encuentra
 * 
 * @author Payment Initiation Service Team
 */
public class RetrievePaymentOrderStatusService implements RetrievePaymentOrderStatusUseCase {
    
    private final PaymentOrderRepository paymentOrderRepository;
    
    /**
     * Constructor que recibe el repositorio de órdenes de pago.
     * 
     * @param paymentOrderRepository El repositorio de dominio (no debe ser null)
     */
    public RetrievePaymentOrderStatusService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }
    
    @Override
    public com.bank.paymentinitiation.domain.model.PaymentStatus retrieveStatus(String paymentOrderReference) {
        // TODO: Implementar búsqueda y retorno del estado
        return paymentOrderRepository.findByReference(paymentOrderReference)
            .map(com.bank.paymentinitiation.domain.model.PaymentOrder::getStatus)
            .orElseThrow(() -> new com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException(
                "Payment order not found with reference: " + paymentOrderReference
            ));
    }
}

