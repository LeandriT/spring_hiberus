package com.bank.paymentinitiation.adapter.out.persistence;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;
import com.bank.paymentinitiation.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementing the PaymentOrderRepository port.
 * 
 * This adapter bridges the domain layer (which defines the repository interface)
 * and the persistence layer (which uses JPA). It:
 * 1. Implements the domain repository interface (PaymentOrderRepository)
 * 2. Uses JPA repository (PaymentOrderJpaRepository) for persistence operations
 * 3. Uses mapper (PaymentOrderPersistenceMapper) to convert between domain and entity
 * 
 * This is the driven adapter (outbound adapter) in the Hexagonal Architecture.
 * It adapts the infrastructure (JPA) to the domain's needs.
 */
@Component
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepository {
    
    private final PaymentOrderJpaRepository jpaRepository;
    private final PaymentOrderPersistenceMapper persistenceMapper;
    
    public PaymentOrderRepositoryAdapter(
            PaymentOrderJpaRepository jpaRepository,
            PaymentOrderPersistenceMapper persistenceMapper) {
        this.jpaRepository = jpaRepository;
        this.persistenceMapper = persistenceMapper;
    }
    
    @Override
    public PaymentOrder save(PaymentOrder order) {
        // 1. Convert domain to entity
        PaymentOrderEntity entity = persistenceMapper.toEntity(order);
        
        // 2. Save entity
        PaymentOrderEntity savedEntity = jpaRepository.save(entity);
        
        // 3. Convert entity back to domain
        PaymentOrder savedOrder = persistenceMapper.toDomain(savedEntity);
        
        // 4. Return domain model
        return savedOrder;
    }
    
    @Override
    public Optional<PaymentOrder> findByReference(String paymentOrderReference) {
        // 1. Find entity by reference string
        Optional<PaymentOrderEntity> entityOptional = jpaRepository.findByPaymentOrderReference(paymentOrderReference);
        
        // 2. Convert entity to domain if found
        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }
        
        PaymentOrder domain = persistenceMapper.toDomain(entityOptional.get());
        
        // 3. Return Optional
        return Optional.of(domain);
    }
}
