package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que implementa el caso de uso de recuperar una orden de pago completa.
 * 
 * <p>Este servicio busca una orden de pago por su referencia y la retorna,
 * lanzando una excepción si no se encuentra.
 */
@Service
@RequiredArgsConstructor
public class RetrievePaymentOrderService implements RetrievePaymentOrderUseCase {

    private final PaymentOrderRepository repository;

    @Override
    public PaymentOrder retrieve(final String paymentOrderReference) {
        if (paymentOrderReference == null || paymentOrderReference.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment order reference cannot be null or blank");
        }

        return repository.findByReference(paymentOrderReference)
                .orElseThrow(() -> new PaymentOrderNotFoundException(
                        "Payment order not found with reference: " + paymentOrderReference));
    }
}

