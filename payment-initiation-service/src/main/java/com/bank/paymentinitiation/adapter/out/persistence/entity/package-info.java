/**
 * Persistence Entities Package.
 * 
 * <p>Contains JPA entities that represent database tables. These are infrastructure
 * representations of domain objects, used for persistence.
 * 
 * <p>Entities in this package should:
 * <ul>
 *   <li>Use JPA annotations (@Entity, @Table, @Id, etc.)</li>
 *   <li>Be mapped to domain objects via mappers</li>
 *   <li>Contain persistence-specific logic only (e.g., lazy loading configurations)</li>
 *   <li>NOT contain business logic (that belongs in domain.model)</li>
 * </ul>
 * 
 * <p>Examples:
 * <ul>
 *   <li>PaymentOrderEntity (maps to PaymentOrder domain aggregate)</li>
 * </ul>
 */
package com.bank.paymentinitiation.adapter.out.persistence.entity;

