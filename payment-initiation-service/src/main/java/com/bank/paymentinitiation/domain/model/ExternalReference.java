package com.bank.paymentinitiation.domain.model;

import java.util.Objects;

/**
 * Value object que representa una referencia externa proporcionada por el cliente.
 * 
 * <p>Invariantes:
 * <ul>
 *   <li>El valor no puede ser nulo ni vacío</li>
 * </ul>
 */
public final class ExternalReference {

    private final String value;

    public ExternalReference(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("External reference cannot be null or blank");
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
        ExternalReference that = (ExternalReference) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ExternalReference{"
                + "value='" + value + '\''
                + '}';
    }
}

