package com.bank.paymentinitiation.adapter.out.persistence.jpa;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para PaymentOrderEntity.
 * 
 * <p>Este repositorio proporciona operaciones CRUD básicas y métodos de consulta
 * personalizados para la entidad PaymentOrderEntity.
 */
@Repository
public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> {

    /**
     * Busca una entidad por su paymentOrderReference (identificador de negocio).
     * 
     * <p>⚠️ IMPORTANTE: Este método busca por paymentOrderReference, NO por id (UUID).
     * El paymentOrderReference es el identificador de negocio único.
     *
     * @param paymentOrderReference la referencia de la orden de pago
     * @return la entidad encontrada, o Optional.empty() si no existe
     */
    @Query("SELECT e FROM PaymentOrderEntity e WHERE e.paymentOrderReference = :reference")
    Optional<PaymentOrderEntity> findByPaymentOrderReference(@Param("reference") String paymentOrderReference);
}

