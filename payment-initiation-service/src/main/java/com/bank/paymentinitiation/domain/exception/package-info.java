/**
 * Domain Exceptions Package.
 * 
 * <p>Contains domain-specific exceptions that represent business errors and rule violations.
 * These exceptions are thrown by domain entities, value objects, and domain services
 * when business rules are violated.
 * 
 * <p>These exceptions should be:
 * <ul>
 *   <li>Business-meaningful (e.g., PaymentOrderNotFound, InvalidPaymentAmount)</li>
 *   <li>Framework-agnostic (no Spring-specific exceptions)</li>
 *   <li>Self-documenting (clear names and messages)</li>
 * </ul>
 */
package com.bank.paymentinitiation.domain.exception;

