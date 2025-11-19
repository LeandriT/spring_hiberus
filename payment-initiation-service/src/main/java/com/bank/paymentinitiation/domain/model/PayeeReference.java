package com.bank.paymentinitiation.domain.model;

import java.util.Objects;

/**
 * Value object que representa la referencia del beneficiario (acreedor) de una orden de pago.
 * 
 * Esta referencia identifica al beneficiario/acreedor en la transacción.
 * 
 * Es inmutable y garantiza que el valor no sea null ni vacío.
 * 
 * @author Payment Initiation Service Team
 */
public final class PayeeReference {
    
    private final String value;
    
    /**
     * Constructor privado. Usar la factoría estática of() para crear instancias.
     * 
     * @param value El valor de la referencia (no debe ser null ni vacío)
     */
    private PayeeReference(String value) {
        this.value = value;
    }
    
    /**
     * Factoría estática que valida y crea una instancia de PayeeReference.
     * 
     * @param value El valor de la referencia (no debe ser null ni vacío)
     * @return Una instancia válida de PayeeReference
     * @throws IllegalArgumentException Si el valor es null o vacío
     */
    public static PayeeReference of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Payee reference cannot be null or empty");
        }
        return new PayeeReference(value.trim());
    }
    
    /**
     * Obtiene el valor de la referencia del beneficiario.
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
        PayeeReference that = (PayeeReference) o;
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

