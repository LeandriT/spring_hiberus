/**
 * Entidades JPA para persistencia (JPA Entities).
 * 
 * Este paquete contiene las entidades JPA que representan la estructura de datos
 * en la base de datos. Estas entidades están anotadas con @Entity y @Table y son
 * mapeadas a objetos de dominio mediante MapStruct.
 * 
 * Principios:
 * - Las entidades JPA solo existen en la capa de persistencia
 * - No deben exponerse fuera del adaptador de persistencia
 * - El dominio NO conoce estas entidades
 * - Los mappers transforman entre entidades JPA y objetos de dominio
 * 
 * Ejemplo: PaymentOrderEntity representa la tabla payment_order en la BD.
 * 
 * @author Payment Initiation Service Team
 */
package com.bank.paymentinitiation.adapter.out.persistence.entity;

