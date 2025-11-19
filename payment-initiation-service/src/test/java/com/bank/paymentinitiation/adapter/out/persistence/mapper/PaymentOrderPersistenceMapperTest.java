package com.bank.paymentinitiation.adapter.out.persistence.mapper;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración para PaymentOrderPersistenceMapper.
 * 
 * Verifica que los mapeos entre objetos de dominio y entidades JPA funcionan correctamente.
 */
@SpringBootTest
@DisplayName("PaymentOrderPersistenceMapper Tests")
class PaymentOrderPersistenceMapperTest {
    
    @Autowired
    private PaymentOrderPersistenceMapper mapper;
    
    @Test
    @DisplayName("Debería mapear PaymentOrder del dominio a PaymentOrderEntity")
    void shouldMapDomainToEntity() {
        // Given
        PaymentOrder domain = PaymentOrder.create(
            "PO-0001",
            ExternalReference.of("EXT-1"),
            PayerReference.of("PAYER-123"),
            PayeeReference.of("PAYEE-456"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            "Factura 001",
            LocalDate.now().plusDays(1)
        );
        
        // When
        PaymentOrderEntity entity = mapper.toEntity(domain);
        
        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getPaymentOrderReference()).isEqualTo("PO-0001");
        assertThat(entity.getExternalReference()).isEqualTo("EXT-1");
        assertThat(entity.getPayerReference()).isEqualTo("PAYER-123");
        assertThat(entity.getPayeeReference()).isEqualTo("PAYEE-456");
        assertThat(entity.getInstructedAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getRemittanceInformation()).isEqualTo("Factura 001");
        assertThat(entity.getStatus()).isEqualTo(PaymentOrderEntity.PaymentStatusEntity.INITIATED);
        assertThat(entity.getId()).isNull(); // ID debe ser ignorado en el mapeo
    }
    
    @Test
    @DisplayName("Debería mapear PaymentOrderEntity a PaymentOrder del dominio")
    void shouldMapEntityToDomain() {
        // Given
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setPaymentOrderReference("PO-0001");
        entity.setExternalReference("EXT-1");
        entity.setPayerReference("PAYER-123");
        entity.setPayeeReference("PAYEE-456");
        entity.setInstructedAmount(new BigDecimal("150.75"));
        entity.setCurrency("USD");
        entity.setRemittanceInformation("Factura 001");
        entity.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        entity.setStatus(PaymentOrderEntity.PaymentStatusEntity.INITIATED);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // When
        PaymentOrder domain = mapper.toDomain(entity);
        
        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getPaymentOrderReference()).isEqualTo("PO-0001");
        assertThat(domain.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(domain.getPayerReference().getValue()).isEqualTo("PAYER-123");
        assertThat(domain.getPayeeReference().getValue()).isEqualTo("PAYEE-456");
        assertThat(domain.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(domain.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(domain.getRemittanceInformation()).isEqualTo("Factura 001");
        assertThat(domain.getStatus()).isEqualTo(PaymentStatus.INITIATED);
    }
    
    @Test
    @DisplayName("Debería mapear correctamente cuando el estado es diferente de INITIATED")
    void shouldMapCorrectlyWhenStatusIsNotInitiated() {
        // Given
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setPaymentOrderReference("PO-0001");
        entity.setExternalReference("EXT-1");
        entity.setPayerReference("PAYER-123");
        entity.setPayeeReference("PAYEE-456");
        entity.setInstructedAmount(new BigDecimal("150.75"));
        entity.setCurrency("USD");
        entity.setRemittanceInformation("Factura 001");
        entity.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        entity.setStatus(PaymentOrderEntity.PaymentStatusEntity.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // When
        PaymentOrder domain = mapper.toDomain(entity);
        
        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }
    
    @Test
    @DisplayName("Debería mapear correctamente cuando remittanceInformation es null")
    void shouldMapCorrectlyWhenRemittanceInformationIsNull() {
        // Given
        PaymentOrder domain = PaymentOrder.create(
            "PO-0001",
            ExternalReference.of("EXT-1"),
            PayerReference.of("PAYER-123"),
            PayeeReference.of("PAYEE-456"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            null,
            LocalDate.now().plusDays(1)
        );
        
        // When
        PaymentOrderEntity entity = mapper.toEntity(domain);
        
        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getRemittanceInformation()).isNull();
    }
    
    @Test
    @DisplayName("Debería mantener la integridad en mapeo bidireccional")
    void shouldMaintainIntegrityInBidirectionalMapping() {
        // Given
        PaymentOrder originalDomain = PaymentOrder.create(
            "PO-0001",
            ExternalReference.of("EXT-1"),
            PayerReference.of("PAYER-123"),
            PayeeReference.of("PAYEE-456"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            "Factura 001",
            LocalDate.now().plusDays(1)
        );
        originalDomain.changeStatus(PaymentStatus.PENDING);
        
        // When - Mapeo dominio → entidad → dominio
        PaymentOrderEntity entity = mapper.toEntity(originalDomain);
        PaymentOrder restoredDomain = mapper.toDomain(entity);
        
        // Then
        assertThat(restoredDomain.getPaymentOrderReference()).isEqualTo(originalDomain.getPaymentOrderReference());
        assertThat(restoredDomain.getExternalReference().getValue()).isEqualTo(originalDomain.getExternalReference().getValue());
        assertThat(restoredDomain.getPayerReference().getValue()).isEqualTo(originalDomain.getPayerReference().getValue());
        assertThat(restoredDomain.getPayeeReference().getValue()).isEqualTo(originalDomain.getPayeeReference().getValue());
        assertThat(restoredDomain.getInstructedAmount().getValue()).isEqualByComparingTo(originalDomain.getInstructedAmount().getValue());
        assertThat(restoredDomain.getInstructedAmount().getCurrency()).isEqualTo(originalDomain.getInstructedAmount().getCurrency());
        assertThat(restoredDomain.getRemittanceInformation()).isEqualTo(originalDomain.getRemittanceInformation());
        assertThat(restoredDomain.getRequestedExecutionDate()).isEqualTo(originalDomain.getRequestedExecutionDate());
        assertThat(restoredDomain.getStatus()).isEqualTo(originalDomain.getStatus());
    }
}

