package com.bank.paymentinitiation.domain.port.out;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import java.util.Optional;

/**
 * Payment Order Repository Output Port.
 * 
 * <p>Defines the interface for persisting and retrieving PaymentOrder domain objects.
 * This is an output port (driven port) in the Hexagonal Architecture, representing
 * what the domain needs from the persistence infrastructure.
 * 
 * <p>This interface is implemented by adapters in the infrastructure layer
 * (e.g., PaymentOrderRepositoryAdapter in adapter.out.persistence).
 * 
 * <p>The interface operates on domain objects, not persistence entities.
 * Conversion between domain objects and persistence entities is handled by
 * the adapter implementation.
 */
public interface PaymentOrderRepository {

    /**
     * Saves or updates a payment order.
     * 
     * @param order the payment order to save
     * @return the saved payment order (may have generated fields updated)
     */
    PaymentOrder save(PaymentOrder order);

    /**
     * Finds a payment order by its reference.
     * 
     * @param paymentOrderReference the payment order reference
     * @return the payment order if found, empty otherwise
     */
    Optional<PaymentOrder> findByReference(String paymentOrderReference);
}

