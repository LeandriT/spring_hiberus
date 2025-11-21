package com.bank.paymentinitiation.domain.port.in;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Initiate Payment Order Use Case (Input Port).
 * 
 * <p>Defines the use case for initiating (creating) a new payment order.
 * This is an input port (driving port) in the Hexagonal Architecture,
 * representing what external actors (e.g., REST controllers) can do with the domain.
 * 
 * <p>This interface will be implemented by an application service
 * (e.g., InitiatePaymentOrderService in application.service).
 * 
 * <p>The use case should:
 * <ul>
 *   <li>Generate paymentOrderReference if not provided</li>
 *   <li>Call order.initiate() to set status and timestamps</li>
 *   <li>Validate the payment order</li>
 *   <li>Persist the payment order via PaymentOrderRepository</li>
 *   <li>Return the created payment order</li>
 * </ul>
 */
public interface InitiatePaymentOrderUseCase {

    /**
     * Initiates a payment order.
     * 
     * <p>This method:
     * <ol>
     *   <li>Generates paymentOrderReference if not provided</li>
     *   <li>Calls order.initiate() to set status = INITIATED and timestamps</li>
     *   <li>Validates the order</li>
     *   <li>Saves the order to the repository</li>
     * </ol>
     * 
     * @param order the payment order to initiate (may not have paymentOrderReference yet)
     * @return the initiated payment order with generated reference and status
     */
    PaymentOrder initiate(PaymentOrder order);
}

