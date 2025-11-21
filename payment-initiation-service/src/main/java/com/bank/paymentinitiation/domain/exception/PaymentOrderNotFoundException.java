package com.bank.paymentinitiation.domain.exception;

/**
 * Domain exception thrown when a payment order is not found.
 * 
 * <p>This is a business exception that represents the domain concept of
 * a missing payment order. It is thrown by domain services or repositories
 * when attempting to retrieve a payment order that doesn't exist.
 * 
 * <p>This exception is framework-agnostic and belongs to the domain layer.
 * It should be caught by adapters (e.g., REST controllers) and converted
 * to appropriate HTTP responses (e.g., 404 Not Found).
 */
public class PaymentOrderNotFoundException extends RuntimeException {

    /**
     * Constructs a new PaymentOrderNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public PaymentOrderNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a new PaymentOrderNotFoundException with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public PaymentOrderNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

