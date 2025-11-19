/**
 * Repositorios JPA (Spring Data JPA Repositories).
 * 
 * Este paquete contiene las interfaces de Spring Data JPA que extienden
 * JpaRepository para operaciones CRUD y consultas personalizadas.
 * 
 * Estos repositorios operan sobre las entidades JPA del paquete entity
 * y son utilizados por PaymentOrderRepositoryAdapter para implementar
 * el puerto de salida PaymentOrderRepository del dominio.
 * 
 * Ejemplo: PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID>
 * 
 * @author Payment Initiation Service Team
 */
package com.bank.paymentinitiation.adapter.out.persistence.jpa;

