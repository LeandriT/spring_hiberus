package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentStatus;

/**
 * Input port (driving port) for retrieving the status of a payment order.
 * 
 * This use case represents the "Retrieve Status" operation from BIAN Payment Initiation.
 * It is called by the REST adapter to get only the status of a payment order.
 * 
 * Following Hexagonal Architecture principles, this is a port in the domain layer
 * that defines the contract for retrieving payment order status. The implementation
 * will be in the application layer.
 */
public interface RetrievePaymentOrderStatusUseCase {
    
    /**
     * Retrieves the status of a payment order by its reference.
     * 
     * @param paymentOrderReference the reference of the payment order (as String)
     * @return the current status of the payment order
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException 
     *         if the payment order is not found
     */
    PaymentStatus retrieveStatus(String paymentOrderReference);
}

