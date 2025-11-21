package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Retrieve Payment Order Application Service.
 * 
 * <p>Implements the RetrievePaymentOrderUseCase use case.
 * Retrieves a complete payment order by its reference.
 * 
 * <p>Throws PaymentOrderNotFoundException if the order is not found.
 */
@Service
@RequiredArgsConstructor
public class RetrievePaymentOrderService implements RetrievePaymentOrderUseCase {

    private final PaymentOrderRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentOrder retrieve(final String paymentOrderReference) {
        return repository.findByReference(paymentOrderReference)
            .orElseThrow(() -> new PaymentOrderNotFoundException(
                String.format("Payment order with reference '%s' not found", paymentOrderReference)));
    }
}

