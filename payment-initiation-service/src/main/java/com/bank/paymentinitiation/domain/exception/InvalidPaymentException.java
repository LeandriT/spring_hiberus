package com.bank.paymentinitiation.domain.exception;

/**
 * Excepción de dominio que se lanza cuando una orden de pago no cumple
 * las reglas de negocio o validaciones del dominio.
 * 
 * Esta excepción se utiliza cuando una orden de pago no puede ser procesada
 * debido a validaciones de negocio (monto inválido, cuentas incorrectas, etc.).
 * Es una excepción de negocio que será mapeada a un error HTTP 400 en el adaptador REST.
 * 
 * @author Payment Initiation Service Team
 */
public class InvalidPaymentException extends RuntimeException {
    
    /**
     * Constructor con mensaje.
     * 
     * @param message El mensaje descriptivo del error
     */
    public InvalidPaymentException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param message El mensaje descriptivo del error
     * @param cause La causa del error
     */
    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}

