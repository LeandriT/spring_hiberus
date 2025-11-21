/**
 * Domain Output Ports Package.
 * 
 * <p>Contains output ports (driven ports) that define interfaces for infrastructure
 * services that the domain needs, such as persistence, external APIs, message brokers, etc.
 * 
 * <p>These ports represent what the domain needs from external systems, and are implemented
 * by adapters in the infrastructure layer.
 * 
 * <p>Examples:
 * <ul>
 *   <li>PaymentOrderRepository (for persistence)</li>
 *   <li>LegacyPaymentOrderClient (for calling legacy SOAP service, if needed)</li>
 *   <li>NotificationService (for sending notifications, if needed)</li>
 * </ul>
 */
package com.bank.paymentinitiation.domain.port.out;

