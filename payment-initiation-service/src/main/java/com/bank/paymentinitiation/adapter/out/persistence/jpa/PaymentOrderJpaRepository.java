package com.bank.paymentinitiation.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;

/**
 * Spring Data JPA repository for PaymentOrderEntity.
 * 
 * This repository provides CRUD operations and custom queries for
 * the PaymentOrderEntity. It is part of the persistence adapter.
 * 
 * This interface extends JpaRepository, which provides standard
 * CRUD methods. Custom query methods can be added here as needed.
 */
@Repository
public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> {
    
    /**
     * Finds a payment order by its reference.
     * 
     * @param paymentOrderReference the payment order reference
     * @return the payment order entity if found
     */
    Optional<PaymentOrderEntity> findByPaymentOrderReference(String paymentOrderReference);
    
    /**
     * Checks if a payment order exists with the given reference.
     * 
     * @param paymentOrderReference the payment order reference
     * @return true if exists, false otherwise
     */
    boolean existsByPaymentOrderReference(String paymentOrderReference);
}

