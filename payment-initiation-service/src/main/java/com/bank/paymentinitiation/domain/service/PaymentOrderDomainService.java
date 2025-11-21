package com.bank.paymentinitiation.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;

/**
 * Servicio de dominio para PaymentOrder.
 * 
 * <p>Este servicio encapsula lógica de negocio que no pertenece
 * naturalmente al agregado PaymentOrder, como:
 * <ul>
 *   <li>Generación de referencias únicas</li>
 *   <li>Validaciones de negocio complejas</li>
 * </ul>
 */
@Component
public class PaymentOrderDomainService {

    /**
     * Genera una referencia única para una orden de pago.
     * 
     * <p>El formato es "PO-{número}" donde el número es generado
     * usando timestamp y número aleatorio para garantizar unicidad.
     *
     * @return una referencia única en formato "PO-{número}"
     */
    public String generateReference() {
        // Genera un identificador numérico usando timestamp (últimos 10 dígitos) + aleatorio (6 dígitos)
        // Esto asegura unicidad mientras cumple el patrón ^PO-[0-9]+$
        long timestamp = System.currentTimeMillis();
        long random = (long) (Math.random() * 1000000); // 6 dígitos
        String numericId = String.format("%010d%06d", timestamp % 10000000000L, random);
        return "PO-" + numericId;
    }

    /**
     * Valida reglas de negocio adicionales de una orden de pago.
     * 
     * <p>Este método valida reglas de negocio que van más allá
     * de las invariantes del agregado, como:
     * <ul>
     *   <li>La fecha de ejecución solicitada no debe estar en el pasado</li>
     *   <li>Otras reglas de negocio específicas</li>
     * </ul>
     *
     * @param order la orden de pago a validar
     * @throws InvalidPaymentException si la orden no cumple las reglas de negocio
     */
    public void validate(final PaymentOrder order) {
        if (order == null) {
            throw new InvalidPaymentException("Payment order cannot be null");
        }

        // Validar que la fecha de ejecución solicitada no esté en el pasado
        LocalDate requestedDate = order.getRequestedExecutionDate();
        if (requestedDate != null && requestedDate.isBefore(LocalDate.now())) {
            throw new InvalidPaymentException(
                    "Requested execution date cannot be in the past: " + requestedDate);
        }
    }
}

