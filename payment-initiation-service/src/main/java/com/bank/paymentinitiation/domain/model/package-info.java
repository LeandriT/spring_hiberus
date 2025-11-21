/**
 * Domain Model Package.
 * 
 * <p>Contains the core business domain entities, value objects, and aggregates.
 * These classes represent the business concepts and rules, independent of any
 * technical framework or infrastructure.
 * 
 * <p>Classes in this package should:
 * <ul>
 *   <li>Contain business logic and domain rules</li>
 *   <li>Be framework-agnostic (no Spring annotations)</li>
 *   <li>Be immutable when possible</li>
 *   <li>Enforce business invariants</li>
 * </ul>
 * 
 * <p>Examples: PaymentOrder (aggregate root), PaymentOrderStatus (value object),
 * Account (value object), etc.
 */
package com.bank.paymentinitiation.domain.model;

