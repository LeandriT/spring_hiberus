package com.bank.paymentinitiation.domain.exception;

/**
 * Domain exception thrown when a payment order is not found.
 * 
 * This exception is part of the domain layer and should be thrown when
 * business logic requires a payment order that doesn't exist.
 * 
 * It should be caught in the adapter layer and converted to appropriate
 * HTTP responses (e.g., 404 Not Found).
 */
public class PaymentOrderNotFoundException extends DomainException {
    
    private final String paymentOrderReference;
    
    public PaymentOrderNotFoundException(String paymentOrderReference) {
        super("Payment order not found: " + paymentOrderReference);
        this.paymentOrderReference = paymentOrderReference;
    }
    
    public String getPaymentOrderReference() {
        return paymentOrderReference;
    }
}

