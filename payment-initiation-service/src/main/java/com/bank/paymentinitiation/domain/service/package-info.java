/**
 * Servicios de dominio (Domain Services).
 * 
 * Este paquete contiene servicios de dominio que encapsulan lógica de negocio
 * que no pertenece naturalmente a una entidad o value object específico.
 * 
 * Los servicios de dominio son puros (sin dependencias de framework) y operan
 * sobre objetos del dominio. Ejemplos de servicios de dominio podrían ser:
 * - Servicio para validar reglas de negocio complejas
 * - Servicio para calcular derivaciones de datos
 * - Servicio para coordinar operaciones entre múltiples agregados
 * 
 * Si no se requieren servicios de dominio, este paquete puede quedar vacío.
 * 
 * @author Payment Initiation Service Team
 */
package com.bank.paymentinitiation.domain.service;

