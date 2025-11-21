package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Initiate Payment Order Application Service.
 * 
 * <p>Implements the InitiatePaymentOrderUseCase use case.
 * Orchestrates the initiation of a new payment order by:
 * <ol>
 *   <li>Calling order.initiate() to set status and timestamps</li>
 *   <li>Validating the order</li>
 *   <li>Saving the order via repository</li>
 * </ol>
 * 
 * <p>⚠️ IMPORTANT: The order of operations must be:
 * 1. Call initiate() (sets status and createdAt)
 * 2. Call validate() (requires status and createdAt to be set)
 * 3. Save to repository
 * 
 * <p>Note: paymentOrderReference is generated in the controller/adapter layer
 * before calling this service, so it's already set when this method is called.
 */
@Service
@RequiredArgsConstructor
public class InitiatePaymentOrderService implements InitiatePaymentOrderUseCase {

    private final PaymentOrderRepository repository;
    private final PaymentOrderDomainService paymentOrderDomainService;

    /**
     * {@inheritDoc}
     * 
     * <p>Order of operations:
     * <ol>
     *   <li>Call order.initiate() to set status = INITIATED and timestamps</li>
     *   <li>Call domain service validation and aggregate validation</li>
     *   <li>Save to repository</li>
     * </ol>
     * 
     * <p>Note: paymentOrderReference should already be set before calling this method.
     */
    @Override
    public PaymentOrder initiate(final PaymentOrder order) {
        // 1. Call initiate() to set status = INITIATED and createdAt/updatedAt
        order.initiate();

        // 2. Validate the order (requires status and createdAt to be set)
        paymentOrderDomainService.validate(order);
        order.validate();

        // 3. Save to repository
        return repository.save(order);
    }
}
