package com.bank.paymentinitiation.adapter.out.persistence.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderStatusEntity;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;

/**
 * Unit tests for PaymentOrderPersistenceMapper.
 * 
 * Tests cover:
 * - Domain to Entity mapping
 * - Entity to Domain mapping
 * - Value object flattening
 * - Status enum conversion
 */
@SpringBootTest
@DisplayName("PaymentOrderPersistenceMapper Tests")
class PaymentOrderPersistenceMapperTest {
    
    @Autowired
    private PaymentOrderPersistenceMapper mapper;
    
    private PaymentOrder domainOrder;
    private PaymentOrderEntity entity;
    
    @BeforeEach
    void setUp() {
        // Setup domain order
        domainOrder = PaymentOrder.builder()
            .paymentOrderReference("PO-12345")
            .externalReference(ExternalReference.of("EXT-1"))
            .payerReference(PayerReference.of("PAYER-123"))
            .payeeReference(PayeeReference.of("PAYEE-456"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("100.50"), "USD"))
            .remittanceInformation("Test payment")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        // Setup entity
        entity = PaymentOrderEntity.builder()
            .id(UUID.randomUUID())
            .paymentOrderReference("PO-12345")
            .externalReference("EXT-1")
            .payerReference("PAYER-123")
            .payeeReference("PAYEE-456")
            .amount(new BigDecimal("100.50"))
            .currency("USD")
            .remittanceInformation("Test payment")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentOrderStatusEntity.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("Should map Domain to Entity")
    void shouldMapDomainToEntity() {
        // Act
        PaymentOrderEntity result = mapper.toEntity(domainOrder);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-12345");
        assertThat(result.getExternalReference()).isEqualTo("EXT-1");
        assertThat(result.getPayerReference()).isEqualTo("PAYER-123");
        assertThat(result.getPayeeReference()).isEqualTo("PAYEE-456");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getRemittanceInformation()).isEqualTo("Test payment");
        assertThat(result.getStatus()).isEqualTo(PaymentOrderStatusEntity.INITIATED);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should map Entity to Domain")
    void shouldMapEntityToDomain() {
        // Act
        PaymentOrder result = mapper.toDomain(entity);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-12345");
        assertThat(result.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(result.getPayerReference().getValue()).isEqualTo("PAYER-123");
        assertThat(result.getPayeeReference().getValue()).isEqualTo("PAYEE-456");
        assertThat(result.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(result.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(result.getRemittanceInformation()).isEqualTo("Test payment");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should map all PaymentStatus values correctly")
    void shouldMapAllStatuses() {
        // Test all status values
        PaymentStatus[] statuses = PaymentStatus.values();
        
        for (PaymentStatus status : statuses) {
            // Arrange
            PaymentOrder order = domainOrder.toBuilder()
                .status(status)
                .build();
            
            // Act
            PaymentOrderEntity entity = mapper.toEntity(order);
            PaymentOrder mappedBack = mapper.toDomain(entity);
            
            // Assert
            assertThat(mappedBack.getStatus()).isEqualTo(status);
        }
    }
}

