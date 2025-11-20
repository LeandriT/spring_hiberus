package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Input port (driving port) for initiating a new payment order.
 * 
 * This use case represents the "Initiate" operation from BIAN Payment Initiation.
 * It is called by the REST adapter to create a new payment order.
 * 
 * Following Hexagonal Architecture principles, this is a port in the domain layer
 * that defines the contract for initiating payment orders. The implementation
 * will be in the application layer.
 */
public interface InitiatePaymentOrderUseCase {
    
    /**
     * Initiates a new payment order.
     * 
     * @param paymentOrder the payment order to initiate
     * @return the initiated payment order with generated reference and status
     * @throws com.bank.paymentinitiation.domain.exception.InvalidPaymentException 
     *         if the payment order is invalid
     */
    PaymentOrder initiate(PaymentOrder paymentOrder);
}

