package com.bank.paymentinitiation.domain.service;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentOrderReference;

import java.time.LocalDate;
import java.util.UUID;

import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;

/**
 * Domain service for Payment Order business logic.
 * 
 * This service contains business logic that doesn't naturally fit
 * within the PaymentOrder aggregate itself, or that requires coordination
 * between multiple aggregates.
 * 
 * Examples of logic that might go here:
 * - Complex validation rules
 * - Business rule calculations
 * - Status transition validations
 * - Cross-aggregate business logic
 * 
 * This class should remain framework-agnostic (no Spring annotations).
 */
public class PaymentOrderDomainService {
    
    private final PaymentOrderRepository paymentOrderRepository;
    
    public PaymentOrderDomainService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }
    
    /**
     * Validates a payment order according to business rules.
     * 
     * @param paymentOrder the payment order to validate
     * @throws InvalidPaymentException if validation fails
     */
    public void validate(PaymentOrder paymentOrder) {
        if (paymentOrder == null) {
            throw new InvalidPaymentException("Payment order cannot be null");
        }
        
        // Validate requested execution date is not in the past
        if (paymentOrder.getRequestedExecutionDate() != null) {
            LocalDate today = LocalDate.now();
            if (paymentOrder.getRequestedExecutionDate().isBefore(today)) {
                throw new InvalidPaymentException(
                    "Requested execution date cannot be in the past: " + 
                    paymentOrder.getRequestedExecutionDate()
                );
            }
        }
        
        // Additional business validations can be added here
    }
    
    /**
     * Generates a new payment order reference.
     * 
     * Format: PO-{UUID} (e.g., PO-550e8400-e29b-41d4-a716-446655440000)
     * 
     * @return a new unique payment order reference
     */
    public PaymentOrderReference generateReference() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String reference = "PO-" + uuid;
        return PaymentOrderReference.of(reference);
    }
}
