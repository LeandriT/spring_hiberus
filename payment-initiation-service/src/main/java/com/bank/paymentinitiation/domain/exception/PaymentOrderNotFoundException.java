package com.bank.paymentinitiation.domain.exception;

/**
 * Excepción lanzada cuando una orden de pago no se encuentra en el repositorio.
 */
public class PaymentOrderNotFoundException extends RuntimeException {

    public PaymentOrderNotFoundException(final String message) {
        super(message);
    }

    public PaymentOrderNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

