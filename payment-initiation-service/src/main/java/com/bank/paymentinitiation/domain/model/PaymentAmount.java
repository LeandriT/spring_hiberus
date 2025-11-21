package com.bank.paymentinitiation.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object que representa un monto de pago con su moneda.
 * 
 * <p>Invariantes:
 * <ul>
 *   <li>El valor debe ser mayor que cero</li>
 *   <li>La moneda no puede ser nula ni vacía</li>
 * </ul>
 */
public final class PaymentAmount {

    private final BigDecimal value;
    private final String currency;

    private PaymentAmount(final BigDecimal value, final String currency) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
        this.value = value;
        this.currency = currency;
    }

    /**
     * Factoría estática que crea un PaymentAmount validando que el valor sea mayor que cero.
     *
     * @param value    el monto (debe ser > 0)
     * @param currency la moneda (no puede ser nula ni vacía)
     * @return un PaymentAmount válido
     * @throws IllegalArgumentException si el valor es <= 0 o la moneda es inválida
     */
    public static PaymentAmount of(final BigDecimal value, final String currency) {
        return new PaymentAmount(value, currency);
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentAmount that = (PaymentAmount) o;
        return Objects.equals(value, that.value) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }

    @Override
    public String toString() {
        return "PaymentAmount{"
                + "value=" + value
                + ", currency='" + currency + '\''
                + '}';
    }
}

