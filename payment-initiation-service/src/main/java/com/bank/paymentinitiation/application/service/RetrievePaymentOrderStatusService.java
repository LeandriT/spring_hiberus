package com.bank.paymentinitiation.application.service;

import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que implementa el caso de uso de recuperar solo el estado de una orden de pago.
 * 
 * <p>Este servicio recupera una orden de pago completa y retorna solo su estado.
 * Reutiliza RetrievePaymentOrderUseCase para evitar duplicación de lógica.
 */
@Service
@RequiredArgsConstructor
public class RetrievePaymentOrderStatusService implements RetrievePaymentOrderStatusUseCase {

    private final RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;

    @Override
    public PaymentStatus retrieveStatus(final String paymentOrderReference) {
        return retrievePaymentOrderUseCase.retrieve(paymentOrderReference).getStatus();
    }
}

