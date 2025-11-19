package com.bank.paymentinitiation.domain.model;

import java.util.Objects;

/**
 * Value object que representa una referencia externa de una orden de pago.
 * 
 * Esta referencia es proporcionada por el cliente externo y se utiliza para
 * identificar la orden desde fuera del sistema.
 * 
 * Es inmutable y garantiza que el valor no sea null ni vacío.
 * 
 * @author Payment Initiation Service Team
 */
public final class ExternalReference {
    
    private final String value;
    
    /**
     * Constructor privado. Usar la factoría estática of() para crear instancias.
     * 
     * @param value El valor de la referencia (no debe ser null ni vacío)
     */
    private ExternalReference(String value) {
        this.value = value;
    }
    
    /**
     * Factoría estática que valida y crea una instancia de ExternalReference.
     * 
     * @param value El valor de la referencia (no debe ser null ni vacío)
     * @return Una instancia válida de ExternalReference
     * @throws IllegalArgumentException Si el valor es null o vacío
     */
    public static ExternalReference of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("External reference cannot be null or empty");
        }
        return new ExternalReference(value.trim());
    }
    
    /**
     * Obtiene el valor de la referencia externa.
     * 
     * @return El valor de la referencia (nunca null ni vacío)
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalReference that = (ExternalReference) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

