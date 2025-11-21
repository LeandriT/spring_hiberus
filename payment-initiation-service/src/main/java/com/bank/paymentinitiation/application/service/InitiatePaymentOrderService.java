package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que implementa el caso de uso de iniciar una orden de pago.
 * 
 * <p>Este servicio orquesta las operaciones necesarias para iniciar una orden:
 * <ol>
 *   <li>Genera la referencia si no existe</li>
 *   <li>Inicia la orden (establece status y createdAt)</li>
 *   <li>Valida la orden (reglas de negocio e invariantes)</li>
 *   <li>Persiste la orden</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class InitiatePaymentOrderService implements InitiatePaymentOrderUseCase {

    private final PaymentOrderRepository repository;
    private final PaymentOrderDomainService paymentOrderDomainService;

    @Override
    public PaymentOrder initiate(final PaymentOrder order) {
        // 1. Generar paymentOrderReference si no existe
        if (order.getPaymentOrderReference() == null
                || order.getPaymentOrderReference().isBlank()) {
            String reference = paymentOrderDomainService.generateReference();
            PaymentOrder orderWithReference = order.toBuilder()
                    .paymentOrderReference(reference)
                    .build();
            return initiateOrder(orderWithReference);
        }

        return initiateOrder(order);
    }

    private PaymentOrder initiateOrder(final PaymentOrder order) {
        // 2. Llamar a order.initiate() para establecer status = INITIATED y createdAt
        PaymentOrder initiatedOrder = order.initiate();

        // 3. Validar la orden (reglas de negocio e invariantes)
        paymentOrderDomainService.validate(initiatedOrder);
        initiatedOrder.validate();

        // 4. Guardar en el repositorio
        return repository.save(initiatedOrder);
    }
}

