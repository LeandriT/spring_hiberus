package com.bank.paymentinitiation.domain.exception;

/**
 * Domain exception thrown when a payment order is invalid.
 * 
 * This exception is part of the domain layer and should be thrown when
 * business rules are violated (e.g., invalid amount, invalid dates,
 * invalid account information).
 * 
 * It should be caught in the adapter layer and converted to appropriate
 * HTTP responses (e.g., 400 Bad Request).
 */
public class InvalidPaymentException extends DomainException {
    
    public InvalidPaymentException(String message) {
        super(message);
    }
    
    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}

