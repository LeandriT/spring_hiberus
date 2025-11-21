/**
 * Domain Input Ports Package.
 * 
 * <p>Contains input ports (driving ports) that define use cases and business operations
 * from the perspective of the domain. These are interfaces that external actors
 * (REST controllers, message handlers, etc.) can call to interact with the domain.
 * 
 * <p>These ports represent what the domain can do (use cases), and are implemented
 * by application services.
 * 
 * <p>Examples:
 * <ul>
 *   <li>InitiatePaymentOrderUseCase</li>
 *   <li>RetrievePaymentOrderUseCase</li>
 *   <li>RetrievePaymentOrderStatusUseCase</li>
 * </ul>
 */
package com.bank.paymentinitiation.domain.port.in;

