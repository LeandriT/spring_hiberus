package com.bank.paymentinitiation.domain.port.out;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import java.util.Optional;

/**
 * Puerto de salida (repositorio) para persistir y recuperar órdenes de pago.
 * 
 * Este puerto define el contrato del repositorio de dominio, independiente de la
 * tecnología de persistencia (JPA, JDBC, etc.). Es implementado por adaptadores
 * de persistencia siguiendo los principios de arquitectura hexagonal.
 * 
 * La implementación debe:
 * - Guardar órdenes de pago (crear o actualizar)
 * - Buscar órdenes por su referencia de negocio
 * - Manejar transacciones a nivel de adaptador
 * 
 * @author Payment Initiation Service Team
 */
public interface PaymentOrderRepository {
    
    /**
     * Guarda una orden de pago (crea nueva o actualiza existente).
     * 
     * @param order La orden de pago a guardar (no debe ser null)
     * @return La orden de pago guardada (con ID técnico si es nueva)
     */
    PaymentOrder save(PaymentOrder order);
    
    /**
     * Busca una orden de pago por su referencia de negocio.
     * 
     * @param paymentOrderReference La referencia de negocio de la orden (no debe ser null ni vacío)
     * @return Optional con la orden encontrada, o vacío si no existe
     */
    Optional<PaymentOrder> findByReference(String paymentOrderReference);
}

