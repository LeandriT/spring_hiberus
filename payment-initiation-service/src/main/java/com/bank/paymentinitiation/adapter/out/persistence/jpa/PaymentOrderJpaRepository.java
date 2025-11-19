package com.bank.paymentinitiation.adapter.out.persistence.jpa;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para operaciones de persistencia de PaymentOrderEntity.
 * 
 * Este repositorio extiende JpaRepository y proporciona métodos para acceder
 * a la base de datos usando Spring Data JPA.
 * 
 * Métodos disponibles:
 * - CRUD estándar heredados de JpaRepository
 * - findByPaymentOrderReference: busca por referencia de negocio
 * 
 * Este repositorio opera sobre PaymentOrderEntity (entidad JPA) y es utilizado
 * por PaymentOrderRepositoryAdapter para implementar el puerto de dominio
 * PaymentOrderRepository.
 * 
 * @author Payment Initiation Service Team
 */
@Repository
public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> {
    
    /**
     * Busca una orden de pago por su referencia de negocio.
     * 
     * @param paymentOrderReference La referencia de negocio de la orden (no debe ser null)
     * @return Optional con la entidad encontrada, o vacío si no existe
     */
    Optional<PaymentOrderEntity> findByPaymentOrderReference(String paymentOrderReference);
}

