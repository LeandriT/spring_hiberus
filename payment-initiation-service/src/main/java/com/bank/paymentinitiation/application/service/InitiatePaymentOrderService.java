package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;

/**
 * Application service implementing the InitiatePaymentOrderUseCase.
 * 
 * This service orchestrates the initiation of a payment order:
 * 1. Validates the payment order using domain service
 * 2. Generates a payment order reference
 * 3. Sets initial status to INITIATED
 * 4. Persists the payment order
 * 
 * This is the implementation of the use case defined in the domain layer.
 * It coordinates between domain services and output ports (repositories).
 */
public class InitiatePaymentOrderService implements InitiatePaymentOrderUseCase {
    
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentOrderDomainService paymentOrderDomainService;
    
    public InitiatePaymentOrderService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentOrderDomainService paymentOrderDomainService) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentOrderDomainService = paymentOrderDomainService;
    }
    
    @Override
    public PaymentOrder initiate(PaymentOrder order) {
        // Generate payment order reference if not provided
        PaymentOrder orderToSave = order;
        if (order.getPaymentOrderReference() == null || order.getPaymentOrderReference().isBlank()) {
            var reference = paymentOrderDomainService.generateReference();
            orderToSave = order.toBuilder()
                .paymentOrderReference(reference.getValue())
                .build();
        }
        
        // Initiate the order (set status to INITIATED and timestamps)
        // This must be done BEFORE validation because validate() requires status and createdAt
        PaymentOrder initiatedOrder = orderToSave.initiate();
        
        // Validate the payment order (after initiate() sets status and timestamps)
        try {
            paymentOrderDomainService.validate(initiatedOrder);
            initiatedOrder.validate();
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentException("Invalid payment order: " + e.getMessage(), e);
        }
        
        // Save and return
        return paymentOrderRepository.save(initiatedOrder);
    }
}
