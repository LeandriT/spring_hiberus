package com.bank.paymentinitiation.adapter.out.persistence;

import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;
import com.bank.paymentinitiation.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia que implementa el puerto de salida PaymentOrderRepository.
 * 
 * Este adaptador es responsable de:
 * - Implementar PaymentOrderRepository (puerto de dominio)
 * - Usar repositorios JPA para operaciones de base de datos
 * - Transformar entre objetos de dominio y entidades JPA usando MapStruct
 * - Manejar transacciones y operaciones de persistencia
 * 
 * Este es un adaptador secundario (outbound) que conecta el dominio con la
 * capa de persistencia, siguiendo los principios de arquitectura hexagonal.
 * 
 * La implementación debe:
 * - Inyectar PaymentOrderJpaRepository y PaymentOrderPersistenceMapper
 * - Convertir dominio → entidad antes de guardar
 * - Convertir entidad → dominio después de recuperar
 * - Manejar Optional correctamente
 * 
 * @author Payment Initiation Service Team
 */
@Component
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepository {
    
    private final PaymentOrderJpaRepository jpaRepository;
    private final PaymentOrderPersistenceMapper mapper;
    
    /**
     * Constructor que recibe el repositorio JPA y el mapper.
     * 
     * @param jpaRepository El repositorio JPA (no debe ser null)
     * @param mapper El mapper de persistencia (no debe ser null)
     */
    public PaymentOrderRepositoryAdapter(
            PaymentOrderJpaRepository jpaRepository,
            PaymentOrderPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public com.bank.paymentinitiation.domain.model.PaymentOrder save(com.bank.paymentinitiation.domain.model.PaymentOrder order) {
        var entity = mapper.toEntity(order);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public java.util.Optional<com.bank.paymentinitiation.domain.model.PaymentOrder> findByReference(String paymentOrderReference) {
        return jpaRepository.findByPaymentOrderReference(paymentOrderReference)
            .map(mapper::toDomain);
    }
}

