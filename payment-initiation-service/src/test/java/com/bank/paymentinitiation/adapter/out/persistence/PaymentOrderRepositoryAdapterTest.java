package com.bank.paymentinitiation.adapter.out.persistence;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;
import com.bank.paymentinitiation.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PaymentOrderRepositoryAdapter.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentOrderRepositoryAdapter Tests")
class PaymentOrderRepositoryAdapterTest {

    @Mock
    private PaymentOrderJpaRepository jpaRepository;

    @Mock
    private PaymentOrderPersistenceMapper mapper;

    @InjectMocks
    private PaymentOrderRepositoryAdapter adapter;

    private PaymentOrder createValidPaymentOrder() {
        PaymentOrder order = PaymentOrder.builder()
            .paymentOrderReference("PO-1234567890123456")
            .externalReference(new ExternalReference("EXT-123"))
            .payerReference(new PayerReference("EC123456789012345678"))
            .payeeReference(new PayeeReference("EC987654321098765432"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return order;
    }

    private PaymentOrderEntity createValidPaymentOrderEntity() {
        return PaymentOrderEntity.builder()
            .id(UUID.randomUUID())
            .paymentOrderReference("PO-1234567890123456")
            .externalReference("EXT-123")
            .payerReference("EC123456789012345678")
            .payeeReference("EC987654321098765432")
            .amount(new BigDecimal("150.75"))
            .currency("USD")
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status("INITIATED")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should save new payment order successfully")
    void shouldSaveNewPaymentOrderSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrderEntity entity = createValidPaymentOrderEntity();
        PaymentOrderEntity savedEntity = createValidPaymentOrderEntity();
        PaymentOrder savedOrder = createValidPaymentOrder();

        when(jpaRepository.findByPaymentOrderReference(order.getPaymentOrderReference()))
            .thenReturn(Optional.empty());
        when(mapper.toEntity(order)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedOrder);

        // Act
        PaymentOrder result = adapter.save(order);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentOrderReference()).isEqualTo(order.getPaymentOrderReference());
        verify(jpaRepository).findByPaymentOrderReference(order.getPaymentOrderReference());
        verify(mapper).toEntity(order);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should update existing payment order successfully")
    void shouldUpdateExistingPaymentOrderSuccessfully() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrderEntity existingEntity = createValidPaymentOrderEntity();
        UUID existingId = existingEntity.getId();
        PaymentOrderEntity updatedEntity = createValidPaymentOrderEntity();
        PaymentOrderEntity savedEntity = createValidPaymentOrderEntity();
        PaymentOrder savedOrder = createValidPaymentOrder();

        when(jpaRepository.findByPaymentOrderReference(order.getPaymentOrderReference()))
            .thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(order)).thenReturn(updatedEntity);
        when(jpaRepository.save(any(PaymentOrderEntity.class))).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedOrder);

        // Act
        PaymentOrder result = adapter.save(order);

        // Assert
        Assertions.assertThat(result).isNotNull();
        verify(jpaRepository).findByPaymentOrderReference(order.getPaymentOrderReference());
        verify(mapper).toEntity(order);
        verify(jpaRepository).save(any(PaymentOrderEntity.class));
        // Verify that the ID was preserved
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should find payment order by reference when exists")
    void shouldFindPaymentOrderByReferenceWhenExists() {
        // Arrange
        String reference = "PO-1234567890123456";
        PaymentOrderEntity entity = createValidPaymentOrderEntity();
        PaymentOrder order = createValidPaymentOrder();

        when(jpaRepository.findByPaymentOrderReference(reference))
            .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);

        // Act
        Optional<PaymentOrder> result = adapter.findByReference(reference);

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getPaymentOrderReference()).isEqualTo(reference);
        verify(jpaRepository).findByPaymentOrderReference(reference);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty when payment order not found")
    void shouldReturnEmptyWhenPaymentOrderNotFound() {
        // Arrange
        String reference = "PO-NONEXISTENT";

        when(jpaRepository.findByPaymentOrderReference(reference))
            .thenReturn(Optional.empty());

        // Act
        Optional<PaymentOrder> result = adapter.findByReference(reference);

        // Assert
        Assertions.assertThat(result).isEmpty();
        verify(jpaRepository).findByPaymentOrderReference(reference);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should preserve ID when updating existing entity")
    void shouldPreserveIdWhenUpdatingExistingEntity() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrderEntity existingEntity = createValidPaymentOrderEntity();
        UUID existingId = existingEntity.getId();
        PaymentOrderEntity newEntity = createValidPaymentOrderEntity();
        PaymentOrderEntity savedEntity = createValidPaymentOrderEntity();
        PaymentOrder savedOrder = createValidPaymentOrder();

        when(jpaRepository.findByPaymentOrderReference(order.getPaymentOrderReference()))
            .thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(order)).thenReturn(newEntity);
        when(jpaRepository.save(any(PaymentOrderEntity.class))).thenAnswer(invocation -> {
            PaymentOrderEntity entity = invocation.getArgument(0);
            // Verify that the ID was set
            Assertions.assertThat(entity.getId()).isEqualTo(existingId);
            return savedEntity;
        });
        when(mapper.toDomain(savedEntity)).thenReturn(savedOrder);

        // Act
        adapter.save(order);

        // Assert - ID preservation is verified in the mock answer
        verify(jpaRepository).save(any(PaymentOrderEntity.class));
    }

    @Test
    @DisplayName("Should correctly handle update flow with ID preservation")
    void shouldCorrectlyHandleUpdateFlowWithIdPreservation() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrderEntity existingEntity = createValidPaymentOrderEntity();
        UUID existingId = existingEntity.getId();
        
        // Create a new entity that will be returned by mapper
        PaymentOrderEntity newEntity = PaymentOrderEntity.builder()
            .id(UUID.randomUUID()) // Different ID initially
            .paymentOrderReference(order.getPaymentOrderReference())
            .externalReference("EXT-123")
            .payerReference("EC123456789012345678")
            .payeeReference("EC987654321098765432")
            .amount(new BigDecimal("150.75"))
            .currency("USD")
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status("INITIATED")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        PaymentOrderEntity savedEntity = createValidPaymentOrderEntity();
        PaymentOrder savedOrder = createValidPaymentOrder();

        when(jpaRepository.findByPaymentOrderReference(order.getPaymentOrderReference()))
            .thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(order)).thenReturn(newEntity);
        when(jpaRepository.save(any(PaymentOrderEntity.class))).thenAnswer(invocation -> {
            PaymentOrderEntity entity = invocation.getArgument(0);
            // Verify that the ID was preserved from existing entity
            Assertions.assertThat(entity.getId()).isEqualTo(existingId);
            return savedEntity;
        });
        when(mapper.toDomain(savedEntity)).thenReturn(savedOrder);

        // Act
        PaymentOrder result = adapter.save(order);

        // Assert
        Assertions.assertThat(result).isNotNull();
        verify(jpaRepository).findByPaymentOrderReference(order.getPaymentOrderReference());
        verify(mapper).toEntity(order);
        verify(jpaRepository).save(any(PaymentOrderEntity.class));
        verify(mapper).toDomain(savedEntity);
    }
}

