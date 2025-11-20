package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Input port (driving port) for retrieving a payment order.
 * 
 * This use case represents the "Retrieve" operation from BIAN Payment Initiation.
 * It is called by the REST adapter to get the full details of a payment order.
 * 
 * Following Hexagonal Architecture principles, this is a port in the domain layer
 * that defines the contract for retrieving payment orders. The implementation
 * will be in the application layer.
 */
public interface RetrievePaymentOrderUseCase {
    
    /**
     * Retrieves a payment order by its reference.
     * 
     * @param paymentOrderReference the reference of the payment order to retrieve (as String)
     * @return the payment order if found
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException 
     *         if the payment order is not found
     */
    PaymentOrder retrieve(String paymentOrderReference);
}

