package com.bank.paymentinitiation.domain.port.out;

import java.util.Optional;

import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Output port (driven port) for persisting and retrieving payment orders.
 * 
 * This is a repository interface defined in the domain layer, following
 * Hexagonal Architecture principles. The implementation will be in the
 * adapter.out.persistence layer.
 * 
 * This port abstracts the persistence mechanism, allowing the domain and
 * application layers to be independent of the specific persistence technology
 * (JPA, JDBC, etc.).
 */
public interface PaymentOrderRepository {
    
    /**
     * Saves a payment order.
     * 
     * @param order the payment order to save
     * @return the saved payment order (may have generated fields updated)
     */
    PaymentOrder save(PaymentOrder order);
    
    /**
     * Finds a payment order by its reference.
     * 
     * @param paymentOrderReference the reference to search for (as String)
     * @return the payment order if found, empty otherwise
     */
    Optional<PaymentOrder> findByReference(String paymentOrderReference);
}

