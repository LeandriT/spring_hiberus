package com.bank.paymentinitiation.domain.model;

import java.util.Objects;

/**
 * Value object que representa la referencia del pagador (deudor) de una orden de pago.
 * 
 * Esta referencia identifica al pagador/deudor en la transacción.
 * 
 * Es inmutable y garantiza que el valor no sea null ni vacío.
 * 
 * @author Payment Initiation Service Team
 */
public final class PayerReference {
    
    private final String value;
    
    /**
     * Constructor privado. Usar la factoría estática of() para crear instancias.
     * 
     * @param value El valor de la referencia (no debe ser null ni vacío)
     */
    private PayerReference(String value) {
        this.value = value;
    }
    
    /**
     * Factoría estática que valida y crea una instancia de PayerReference.
     * 
     * @param value El valor de la referencia (no debe ser null ni vacío)
     * @return Una instancia válida de PayerReference
     * @throws IllegalArgumentException Si el valor es null o vacío
     */
    public static PayerReference of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Payer reference cannot be null or empty");
        }
        return new PayerReference(value.trim());
    }
    
    /**
     * Obtiene el valor de la referencia del pagador.
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
        PayerReference that = (PayerReference) o;
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

