package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Retrieve Payment Order Status Application Service.
 * 
 * <p>Implements the RetrievePaymentOrderStatusUseCase use case.
 * Retrieves only the status of a payment order by its reference.
 * 
 * <p>This is a lightweight query operation that may be used for status polling
 * or quick status checks without loading the full payment order.
 * 
 * <p>Throws PaymentOrderNotFoundException if the order is not found.
 */
@Service
@RequiredArgsConstructor
public class RetrievePaymentOrderStatusService implements RetrievePaymentOrderStatusUseCase {

    private final PaymentOrderRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentStatus retrieveStatus(final String paymentOrderReference) {
        PaymentOrder order = repository.findByReference(paymentOrderReference)
            .orElseThrow(() -> new PaymentOrderNotFoundException(
                String.format("Payment order with reference '%s' not found", paymentOrderReference)));
        return order.getStatus();
    }
}

