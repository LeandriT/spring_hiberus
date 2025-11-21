package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentStatus;

/**
 * Retrieve Payment Order Status Use Case (Input Port).
 * 
 * <p>Defines the use case for retrieving only the status of a payment order by its reference.
 * This is an input port (driving port) in the Hexagonal Architecture,
 * representing what external actors (e.g., REST controllers) can do with the domain.
 * 
 * <p>This interface will be implemented by an application service
 * (e.g., RetrievePaymentOrderStatusService in application.service).
 * 
 * <p>The use case should:
 * <ul>
 *   <li>Retrieve the payment order by reference via PaymentOrderRepository</li>
 *   <li>Extract and return only the status</li>
 *   <li>Throw a domain exception if not found (e.g., PaymentOrderNotFoundException)</li>
 * </ul>
 * 
 * <p>This is a lightweight query operation that may be used for status polling
 * or quick status checks without loading the full payment order.
 */
public interface RetrievePaymentOrderStatusUseCase {

    /**
     * Retrieves the status of a payment order by its reference.
     * 
     * @param paymentOrderReference the payment order reference
     * @return the payment order status
     * @throws com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException if not found
     */
    PaymentStatus retrieveStatus(String paymentOrderReference);
}

