package com.bank.paymentinitiation.application.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Servicio para generar referencias únicas de órdenes de pago.
 * 
 * Este servicio genera identificadores únicos en formato "PO-" seguido
 * de un UUID compacto o número secuencial.
 * 
 * Para simplificar, usamos UUIDs compactos. En producción, se podría
 * usar un generador secuencial o basado en base de datos.
 * 
 * @author Payment Initiation Service Team
 */
@Component
public class PaymentOrderReferenceGenerator {
    
    private static final String PREFIX = "PO-";
    
    /**
     * Genera una nueva referencia única para una orden de pago.
     * 
     * Formato: PO-{UUID compacto}
     * Ejemplo: PO-a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
     * 
     * @return Una referencia única en formato PO-{uuid}
     */
    public String generate() {
        return PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}

