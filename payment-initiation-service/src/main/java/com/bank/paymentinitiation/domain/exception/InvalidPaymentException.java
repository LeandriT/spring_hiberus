package com.bank.paymentinitiation.domain.exception;

/**
 * Domain exception thrown when a payment order is invalid.
 * 
 * <p>This is a business exception that represents domain validation errors
 * or business rule violations for payment orders. It is thrown by domain
 * services or aggregates when payment order data is invalid according to
 * business rules.
 * 
 * <p>This exception is framework-agnostic and belongs to the domain layer.
 * It should be caught by adapters (e.g., REST controllers) and converted
 * to appropriate HTTP responses (e.g., 400 Bad Request).
 */
public class InvalidPaymentException extends RuntimeException {

    /**
     * Constructs a new InvalidPaymentException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidPaymentException(final String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidPaymentException with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidPaymentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

