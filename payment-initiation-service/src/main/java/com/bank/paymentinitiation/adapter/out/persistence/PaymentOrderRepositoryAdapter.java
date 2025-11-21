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
 * Adaptador de persistencia que implementa PaymentOrderRepository usando JPA.
 * 
 * <p>Este adaptador:
 * <ul>
 *   <li>Convierte entre PaymentOrder (dominio) y PaymentOrderEntity (JPA)</li>
 *   <li>Maneja la preservación del ID técnico (UUID) al actualizar entidades existentes</li>
 *   <li>Busca entidades por paymentOrderReference (identificador de negocio), no por UUID</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;
    private final PaymentOrderPersistenceMapper mapper;

    @Override
    public PaymentOrder save(final PaymentOrder order) {
        // Verificar si la entidad ya existe por paymentOrderReference
        Optional<PaymentOrderEntity> existingEntity = jpaRepository
                .findByPaymentOrderReference(order.getPaymentOrderReference());

        PaymentOrderEntity entity;
        if (existingEntity.isPresent()) {
            // Actualizar entidad existente: preservar ID y actualizar todos los demás campos
            PaymentOrderEntity existing = existingEntity.get();
            entity = mapper.toEntity(order);
            entity.setId(existing.getId()); // Preservar el ID existente
        } else {
            // Crear nueva entidad
            entity = mapper.toEntity(order);
        }

        // Guardar entidad (insert o update)
        PaymentOrderEntity savedEntity = jpaRepository.save(entity);

        // Mapear de vuelta a dominio y retornar
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PaymentOrder> findByReference(final String paymentOrderReference) {
        return jpaRepository.findByPaymentOrderReference(paymentOrderReference)
                .map(mapper::toDomain);
    }
}

