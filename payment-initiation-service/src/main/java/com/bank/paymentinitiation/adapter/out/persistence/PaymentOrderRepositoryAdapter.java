package com.bank.paymentinitiation.adapter.out.persistence;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;
import com.bank.paymentinitiation.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Payment Order Repository Adapter.
 * 
 * <p>This adapter implements the {@link PaymentOrderRepository} output port,
 * providing persistence functionality for PaymentOrder domain objects.
 * 
 * <p>This adapter follows the Hexagonal Architecture pattern:
 * <ul>
 *   <li>Implements the domain output port (domain.port.out.PaymentOrderRepository)</li>
 *   <li>Uses JPA repositories to interact with the database</li>
 *   <li>Maps between domain objects and persistence entities</li>
 *   <li>Handles persistence-specific concerns (transactions, entity lifecycle, etc.)</li>
 * </ul>
 * 
 * <p>Responsibilities:
 * <ul>
 *   <li>Save PaymentOrder domain objects to the database</li>
 *   <li>Retrieve PaymentOrder domain objects by reference</li>
 *   <li>Query PaymentOrder domain objects (e.g., findByExternalReference)</li>
 *   <li>Map between PaymentOrder (domain) and PaymentOrderEntity (persistence)</li>
 *   <li>Handle persistence exceptions and convert to domain exceptions if needed</li>
 * </ul>
 * 
 * <p>This adapter uses:
 * <ul>
 *   <li>JPA repositories (from jpa package) for database access</li>
 *   <li>Mappers (from mapper package) for domain <-> entity conversion</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;
    private final PaymentOrderPersistenceMapper mapper;

    @Override
    public PaymentOrder save(final PaymentOrder order) {
        // Check if entity already exists by paymentOrderReference
        Optional<PaymentOrderEntity> existingEntity = jpaRepository
            .findByPaymentOrderReference(order.getPaymentOrderReference());

        PaymentOrderEntity entity;
        if (existingEntity.isPresent()) {
            // Update existing entity: preserve ID and update all other fields
            PaymentOrderEntity existing = existingEntity.get();
            entity = mapper.toEntity(order);
            entity.setId(existing.getId()); // Preserve the existing ID
        } else {
            // Create new entity
            entity = mapper.toEntity(order);
        }

        // Save entity (insert or update)
        PaymentOrderEntity savedEntity = jpaRepository.save(entity);

        // Map back to domain and return
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PaymentOrder> findByReference(final String paymentOrderReference) {
        // Find entity by paymentOrderReference (business identifier)
        Optional<PaymentOrderEntity> entity = jpaRepository
            .findByPaymentOrderReference(paymentOrderReference);

        // Map entity to domain if found
        return entity.map(mapper::toDomain);
    }
}

