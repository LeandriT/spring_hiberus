package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

/**
 * Implementación del caso de uso para iniciar una orden de pago.
 * 
 * Este servicio de aplicación implementa InitiatePaymentOrderUseCase y coordina
 * las operaciones necesarias para crear e iniciar una nueva orden de pago.
 * 
 * Responsabilidades:
 * - Aplicar reglas de negocio de nivel de aplicación
 * - Delegar en el repositorio para persistir la orden
 * - Manejar transacciones (si aplica)
 * - Lanzar excepciones de dominio cuando corresponda
 * 
 * Esta clase pertenece a la capa de aplicación y puede usar anotaciones de Spring
 * para inyección de dependencias, pero no debe depender de frameworks en la lógica de negocio.
 * 
 * @author Payment Initiation Service Team
 */
public class InitiatePaymentOrderService implements InitiatePaymentOrderUseCase {
    
    private final PaymentOrderRepository paymentOrderRepository;
    
    /**
     * Constructor que recibe el repositorio de órdenes de pago.
     * 
     * @param paymentOrderRepository El repositorio de dominio (no debe ser null)
     */
    public InitiatePaymentOrderService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }
    
    @Override
    public PaymentOrder initiate(PaymentOrder order) {
        // TODO: Implementar lógica de negocio y validaciones
        // Validar la orden
        order.validate();
        
        // Persistir la orden
        return paymentOrderRepository.save(order);
    }
}

