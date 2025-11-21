package com.bank.paymentinitiation.domain.service;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import org.springframework.stereotype.Component;

/**
 * Payment Order Domain Service.
 * 
 * <p>Contains domain logic that doesn't naturally fit within the PaymentOrder aggregate.
 * This service operates on domain objects and enforces cross-entity business rules.
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Validate payment orders (additional validation beyond aggregate.validate())</li>
 *   <li>Other domain logic that operates on multiple domain objects</li>
 * </ul>
 * 
 * <p>Note: Payment order reference generation has been moved to
 * PaymentOrderReferenceGenerator in the application layer, as it's more of an
 * infrastructure/application concern rather than pure domain logic.
 * 
 * <p>This service is annotated with @Component to be detected by Spring component scanning,
 * as it's used by application services that are managed by Spring.
 */
@Component
public class PaymentOrderDomainService {

    /**
     * Validates a payment order using domain service logic.
     * 
     * <p>This method performs additional validations beyond the aggregate's validate() method.
     * It can check cross-entity rules or more complex business logic.
     * 
     * <p>Note: This method should be called AFTER order.initiate() has been called,
     * as it may depend on status and createdAt being set.
     * 
     * @param order the payment order to validate
     * @throws IllegalStateException if validation fails
     */
    public void validate(final PaymentOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Payment order must not be null");
        }

        // Additional domain validations can be added here
        // For now, the aggregate's validate() method covers the main rules
        
        // Example: Could validate that requestedExecutionDate is not too far in the future
        // Example: Could validate payer/payee references are not the same
        // Example: Could validate currency is supported
    }
}

