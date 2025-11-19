package com.bank.paymentinitiation.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object que representa un monto de pago con su moneda.
 * 
 * Este value object encapsula el monto y la moneda, asegurando que el monto
 * sea siempre positivo mediante validación en la factoría estática.
 * 
 * Es inmutable y no tiene identidad propia.
 * 
 * @author Payment Initiation Service Team
 */
public final class PaymentAmount {
    
    private final BigDecimal value;
    private final String currency;
    
    /**
     * Constructor privado. Usar la factoría estática of() para crear instancias.
     * 
     * @param value El monto (debe ser mayor que cero)
     * @param currency El código de moneda ISO 4217 (no debe ser null ni vacío)
     */
    private PaymentAmount(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }
    
    /**
     * Factoría estática que valida y crea una instancia de PaymentAmount.
     * 
     * @param value El monto (debe ser mayor que cero)
     * @param currency El código de moneda ISO 4217 (no debe ser null ni vacío)
     * @return Una instancia válida de PaymentAmount
     * @throws IllegalArgumentException Si el monto es null, menor o igual a cero, o si la moneda es null o vacía
     */
    public static PaymentAmount of(BigDecimal value, String currency) {
        if (value == null) {
            throw new IllegalArgumentException("Payment amount value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        return new PaymentAmount(value, currency.trim().toUpperCase());
    }
    
    /**
     * Obtiene el valor del monto.
     * 
     * @return El monto (siempre positivo)
     */
    public BigDecimal getValue() {
        return value;
    }
    
    /**
     * Obtiene el código de moneda.
     * 
     * @return El código de moneda ISO 4217
     */
    public String getCurrency() {
        return currency;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentAmount that = (PaymentAmount) o;
        return Objects.equals(value, that.value) && Objects.equals(currency, that.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }
    
    @Override
    public String toString() {
        return "PaymentAmount{" +
                "value=" + value +
                ", currency='" + currency + '\'' +
                '}';
    }
}

