/**
 * Application Services Package.
 * 
 * <p>Contains application services that orchestrate use cases and coordinate between
 * domain objects and infrastructure adapters. These services implement the input ports
 * (use cases) defined in domain.port.in.
 * 
 * <p>Application services should:
 * <ul>
 *   <li>Orchestrate domain objects and infrastructure adapters</li>
 *   <li>Be stateless</li>
 *   <li>Coordinate transactions</li>
 *   <li>Map between DTOs (from adapters) and domain objects</li>
 *   <li>NOT contain business logic (that belongs in domain)</li>
 * </ul>
 * 
 * <p>Examples:
 * <ul>
 *   <li>InitiatePaymentOrderService (implements InitiatePaymentOrderUseCase)</li>
 *   <li>RetrievePaymentOrderService (implements RetrievePaymentOrderUseCase)</li>
 * </ul>
 */
package com.bank.paymentinitiation.application.service;

