package com.bank.paymentinitiation.domain.exception;

/**
 * Excepción de dominio que se lanza cuando una orden de pago no se encuentra.
 * 
 * Esta excepción se utiliza cuando se intenta acceder a una orden de pago que
 * no existe en el repositorio. Es una excepción de negocio que será mapeada
 * a un error HTTP 404 en el adaptador REST.
 * 
 * @author Payment Initiation Service Team
 */
public class PaymentOrderNotFoundException extends RuntimeException {
    
    /**
     * Constructor con mensaje.
     * 
     * @param message El mensaje descriptivo del error
     */
    public PaymentOrderNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message El mensaje descriptivo del error
     * @param cause La causa del error
     */
    public PaymentOrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

