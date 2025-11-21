package com.bank.paymentinitiation.adapter.out.persistence.jpa;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Payment Order JPA Repository.
 * 
 * <p>Spring Data JPA repository for PaymentOrderEntity.
 * Provides standard CRUD operations and custom query methods.
 * 
 * <p>This repository is part of the infrastructure layer and is used
 * by PaymentOrderRepositoryAdapter to persist and retrieve payment orders.
 * 
 * <p>Note: The findByPaymentOrderReference method uses the business identifier
 * (paymentOrderReference) rather than the primary key (id). This allows
 * querying by the business identifier that is exposed in the API.
 */
@Repository
public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> {

    /**
     * Finds a payment order entity by its business reference.
     * 
     * <p>This method queries by paymentOrderReference (the unique business identifier),
     * not by the primary key (id). This is the identifier used in the REST API
     * and domain model.
     * 
     * <p>Example: findByPaymentOrderReference("PO-A1B2C3D4E5F6G7H8")
     * 
     * @param paymentOrderReference the payment order reference (business identifier)
     * @return the payment order entity if found, empty otherwise
     */
    Optional<PaymentOrderEntity> findByPaymentOrderReference(String paymentOrderReference);
}

