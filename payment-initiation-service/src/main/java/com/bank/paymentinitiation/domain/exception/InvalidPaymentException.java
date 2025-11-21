package com.bank.paymentinitiation.domain.exception;

/**
 * Excepción lanzada cuando una orden de pago es inválida según las reglas de negocio.
 */
public class InvalidPaymentException extends RuntimeException {

    public InvalidPaymentException(final String message) {
        super(message);
    }

    public InvalidPaymentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

