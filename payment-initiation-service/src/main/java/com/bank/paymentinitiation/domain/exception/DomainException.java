package com.bank.paymentinitiation.domain.exception;

/**
 * Base exception for all domain exceptions.
 * 
 * All domain-specific exceptions should extend this class to maintain
 * a clear exception hierarchy and facilitate exception handling in
 * the adapter layers.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

