package com.bank.paymentinitiation.config;

import com.bank.paymentinitiation.application.service.InitiatePaymentOrderService;
import com.bank.paymentinitiation.application.service.RetrievePaymentOrderService;
import com.bank.paymentinitiation.application.service.RetrievePaymentOrderStatusService;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.domain.service.PaymentOrderDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 * 
 * This class contains Spring configuration for application services and
 * other application-level beans. It wires together the use cases (application
 * services) with their dependencies (repositories, domain services).
 * 
 * This configuration ensures that:
 * - Use cases are properly instantiated
 * - Dependencies are injected correctly
 * - The application layer is properly configured
 * 
 * Note: PaymentOrderRepository is automatically injected by Spring since
 * PaymentOrderRepositoryAdapter is annotated with @Component.
 */
@Configuration
public class ApplicationConfig {
    
    /**
     * Creates a bean for PaymentOrderDomainService.
     * 
     * @param paymentOrderRepository the payment order repository (injected by Spring)
     * @return the PaymentOrderDomainService bean
     */
    @Bean
    public PaymentOrderDomainService paymentOrderDomainService(PaymentOrderRepository paymentOrderRepository) {
        return new PaymentOrderDomainService(paymentOrderRepository);
    }
    
    /**
     * Creates a bean for InitiatePaymentOrderService.
     * 
     * @param paymentOrderRepository the payment order repository (injected by Spring)
     * @param paymentOrderDomainService the domain service (injected by Spring)
     * @return the InitiatePaymentOrderService bean
     */
    @Bean
    public InitiatePaymentOrderService initiatePaymentOrderService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentOrderDomainService paymentOrderDomainService) {
        return new InitiatePaymentOrderService(paymentOrderRepository, paymentOrderDomainService);
    }
    
    /**
     * Creates a bean for RetrievePaymentOrderService.
     * 
     * @param paymentOrderRepository the payment order repository (injected by Spring)
     * @return the RetrievePaymentOrderService bean
     */
    @Bean
    public RetrievePaymentOrderService retrievePaymentOrderService(
            PaymentOrderRepository paymentOrderRepository) {
        return new RetrievePaymentOrderService(paymentOrderRepository);
    }
    
    /**
     * Creates a bean for RetrievePaymentOrderStatusService.
     * 
     * @param paymentOrderRepository the payment order repository (injected by Spring)
     * @return the RetrievePaymentOrderStatusService bean
     */
    @Bean
    public RetrievePaymentOrderStatusService retrievePaymentOrderStatusService(
            PaymentOrderRepository paymentOrderRepository) {
        return new RetrievePaymentOrderStatusService(paymentOrderRepository);
    }
}
