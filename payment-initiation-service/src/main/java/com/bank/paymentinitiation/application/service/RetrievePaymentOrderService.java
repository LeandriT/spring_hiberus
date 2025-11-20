package com.bank.paymentinitiation.application.service;

import java.util.Optional;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

/**
 * Application service implementing the RetrievePaymentOrderUseCase.
 * 
 * This service retrieves a payment order by its reference.
 * It delegates to the repository and handles the case when the order
 * is not found by throwing a domain exception.
 * 
 * This is the implementation of the use case defined in the domain layer.
 */
public class RetrievePaymentOrderService implements RetrievePaymentOrderUseCase {
    
    private final PaymentOrderRepository paymentOrderRepository;
    
    public RetrievePaymentOrderService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }
    
    @Override
    public PaymentOrder retrieve(String paymentOrderReference) {
        if (paymentOrderReference == null || paymentOrderReference.isBlank()) {
            throw new IllegalArgumentException("Payment order reference cannot be null or empty");
        }
        
        Optional<PaymentOrder> order = paymentOrderRepository.findByReference(paymentOrderReference);
        
        if (order.isEmpty()) {
            throw new PaymentOrderNotFoundException(paymentOrderReference);
        }
        
        return order.get();
    }
}
