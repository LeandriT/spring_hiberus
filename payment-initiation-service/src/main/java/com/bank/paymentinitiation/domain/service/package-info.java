/**
 * Domain Services Package.
 * 
 * <p>Contains domain services that encapsulate business logic that doesn't naturally
 * fit within a single entity or value object. Domain services operate on multiple
 * domain objects and enforce cross-entity business rules.
 * 
 * <p>Domain services should:
 * <ul>
 *   <li>Contain pure business logic</li>
 *   <li>Be stateless</li>
 *   <li>Be framework-agnostic</li>
 *   <li>Operate on domain objects (entities, value objects)</li>
 * </ul>
 * 
 * <p>Examples:
 * <ul>
 *   <li>PaymentOrderValidationService (validates complex business rules)</li>
 *   <li>PaymentOrderStateMachine (manages state transitions)</li>
 * </ul>
 */
package com.bank.paymentinitiation.domain.service;

