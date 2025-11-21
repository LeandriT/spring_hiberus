package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Retrieve Payment Order Use Case (Input Port).
 * 
 * <p>Defines the use case for retrieving a complete payment order by its reference.
 * This is an input port (driving port) in the Hexagonal Architecture,
 * representing what external actors (e.g., REST controllers) can do with the domain.
 * 
 * <p>This interface will be implemented by an application service
 * (e.g., RetrievePaymentOrderService in application.service).
 * 
 * <p>The use case should:
 * <ul>
 *   <li>Retrieve the payment order by reference via PaymentOrderRepository</li>
 *   <li>Return the payment order if found</li>
 *   <li>Throw a domain exception if not found (e.g., PaymentOrderNotFoundException)</li>
 * </ul>
 */
public interface RetrievePaymentOrderUseCase {

    /**
     * Retrieves a payment order by its reference.
     * 
     * @param paymentOrderReference the payment order reference
     * @return the payment order if found
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException if not found
     */
    PaymentOrder retrieve(String paymentOrderReference);
}

