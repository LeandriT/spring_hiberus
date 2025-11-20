package com.bank.paymentinitiation.application.service;

import java.util.Optional;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

/**
 * Application service implementing the RetrievePaymentOrderStatusUseCase.
 * 
 * This service retrieves only the status of a payment order by its reference.
 * It delegates to the repository to get the full order, then extracts the status.
 * 
 * This is the implementation of the use case defined in the domain layer.
 */
public class RetrievePaymentOrderStatusService implements RetrievePaymentOrderStatusUseCase {
    
    private final PaymentOrderRepository paymentOrderRepository;
    
    public RetrievePaymentOrderStatusService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }
    
    @Override
    public PaymentStatus retrieveStatus(String paymentOrderReference) {
        if (paymentOrderReference == null || paymentOrderReference.isBlank()) {
            throw new IllegalArgumentException("Payment order reference cannot be null or empty");
        }
        
        Optional<PaymentOrder> order = paymentOrderRepository.findByReference(paymentOrderReference);
        
        if (order.isEmpty()) {
            throw new PaymentOrderNotFoundException(paymentOrderReference);
        }
        
        return order.get().getStatus();
    }
}
