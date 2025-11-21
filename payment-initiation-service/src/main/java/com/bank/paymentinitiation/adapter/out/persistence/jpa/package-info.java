/**
 * JPA Repositories Package.
 * 
 * <p>Contains Spring Data JPA repository interfaces that extend JpaRepository.
 * These repositories provide standard CRUD operations for persistence entities.
 * 
 * <p>Repositories in this package should:
 * <ul>
 *   <li>Extend JpaRepository or other Spring Data interfaces</li>
 *   <li>Define query methods for custom queries</li>
 *   <li>Operate on persistence entities (not domain objects)</li>
 *   <li>Be used by PaymentOrderRepositoryAdapter to implement domain.port.out.PaymentOrderRepository</li>
 * </ul>
 * 
 * <p>Examples:
 * <ul>
 *   <li>PaymentOrderJpaRepository (extends JpaRepository<PaymentOrderEntity, String>)</li>
 * </ul>
 */
package com.bank.paymentinitiation.adapter.out.persistence.jpa;

