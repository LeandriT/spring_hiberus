package com.bank.paymentinitiation.config;

import com.bank.paymentinitiation.application.service.InitiatePaymentOrderService;
import com.bank.paymentinitiation.application.service.RetrievePaymentOrderService;
import com.bank.paymentinitiation.application.service.RetrievePaymentOrderStatusService;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para los beans de aplicación.
 * 
 * Este archivo configura los servicios de aplicación que implementan
 * los casos de uso del dominio.
 * 
 * @author Payment Initiation Service Team
 */
@Configuration
public class ApplicationConfig {
    
    @Bean
    public InitiatePaymentOrderService initiatePaymentOrderService(PaymentOrderRepository repository) {
        return new InitiatePaymentOrderService(repository);
    }
    
    @Bean
    public RetrievePaymentOrderService retrievePaymentOrderService(PaymentOrderRepository repository) {
        return new RetrievePaymentOrderService(repository);
    }
    
    @Bean
    public RetrievePaymentOrderStatusService retrievePaymentOrderStatusService(PaymentOrderRepository repository) {
        return new RetrievePaymentOrderStatusService(repository);
    }
}

