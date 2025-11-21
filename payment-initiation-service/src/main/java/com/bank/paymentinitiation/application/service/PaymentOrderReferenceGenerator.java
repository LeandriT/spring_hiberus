package com.bank.paymentinitiation.application.service;

import org.springframework.stereotype.Component;

/**
 * Generador de referencias únicas para órdenes de pago.
 * 
 * <p>Este componente genera referencias en formato "PO-{número}" que cumplen
 * con el patrón de validación del OpenAPI: `^PO-[0-9]+$`.
 * 
 * <p>⚠️ CRÍTICO: El formato DEBE cumplir con el patrón del OpenAPI:
 * `^PO-[0-9]+$` (solo números después de "PO-"). NO se pueden usar letras
 * (como en UUIDs) ya que no cumplirán el patrón y causarán errores de validación.
 */
@Component
public class PaymentOrderReferenceGenerator {

    /**
     * Genera una referencia única para una orden de pago.
     * 
     * <p>El formato es "PO-{número}" donde el número es generado usando
     * timestamp y número aleatorio para garantizar unicidad.
     * 
     * <p>Ejemplo de referencia generada: "PO-1234567890123456" (16 dígitos numéricos).
     *
     * @return una referencia única en formato "PO-{número}" que cumple el patrón ^PO-[0-9]+$
     */
    public String generate() {
        // Genera un identificador numérico usando timestamp (últimos 10 dígitos) + aleatorio (6 dígitos)
        // Esto asegura unicidad mientras cumple el patrón ^PO-[0-9]+$
        long timestamp = System.currentTimeMillis();
        long random = (long) (Math.random() * 1000000); // 6 dígitos
        String numericId = String.format("%010d%06d", timestamp % 10000000000L, random);
        return "PO-" + numericId;
    }
}

