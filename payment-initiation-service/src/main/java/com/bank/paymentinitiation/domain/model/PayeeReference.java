package com.bank.paymentinitiation.domain.model;

import java.util.Objects;

/**
 * Value object que representa la referencia del beneficiario (acreedor).
 * 
 * <p>Invariantes:
 * <ul>
 *   <li>El valor no puede ser nulo ni vacío</li>
 * </ul>
 */
public final class PayeeReference {

    private final String value;

    public PayeeReference(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payee reference cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PayeeReference that = (PayeeReference) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PayeeReference{"
                + "value='" + value + '\''
                + '}';
    }
}

