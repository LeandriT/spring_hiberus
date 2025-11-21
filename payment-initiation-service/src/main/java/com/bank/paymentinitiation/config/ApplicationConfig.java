package com.bank.paymentinitiation.config;

import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration.
 * 
 * <p>Spring configuration class for application-level beans.
 * 
 * <p>Note: Application services are annotated with @Service and will be
 * automatically detected by Spring component scanning. This configuration
 * class can be used for any additional bean definitions if needed.
 * 
 * <p>Current services (auto-detected via @Service):
 * <ul>
 *   <li>InitiatePaymentOrderService</li>
 *   <li>RetrievePaymentOrderService</li>
 *   <li>RetrievePaymentOrderStatusService</li>
 *   <li>PaymentOrderReferenceGenerator</li>
 * </ul>
 */
@Configuration
public class ApplicationConfig {
    
    // Application services are auto-detected via @Service annotation
    // Additional beans can be defined here if needed
}

