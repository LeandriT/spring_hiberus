package com.bank.paymentinitiation.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;
import com.bank.paymentinitiation.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        return PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .remittanceInformation("Factura 001-123")
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save new payment order")
    void shouldSaveNewPaymentOrder() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setPaymentOrderReference("PO-1234567890123456");
        
        when(jpaRepository.findByPaymentOrderReference("PO-1234567890123456"))
                .thenReturn(Optional.empty());
        when(mapper.toEntity(order)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(order);

        // Act
        PaymentOrder result = adapter.save(order);

        // Assert
        assertThat(result).isNotNull();
        verify(jpaRepository).findByPaymentOrderReference("PO-1234567890123456");
        verify(mapper).toEntity(order);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should update existing payment order preserving ID")
    void shouldUpdateExistingPaymentOrderPreservingId() {
        // Arrange
        PaymentOrder order = createValidPaymentOrder();
        UUID existingId = UUID.randomUUID();
        PaymentOrderEntity existingEntity = new PaymentOrderEntity();
        existingEntity.setId(existingId);
        existingEntity.setPaymentOrderReference("PO-1234567890123456");
        
        PaymentOrderEntity newEntity = new PaymentOrderEntity();
        newEntity.setPaymentOrderReference("PO-1234567890123456");
        
        when(jpaRepository.findByPaymentOrderReference("PO-1234567890123456"))
                .thenReturn(Optional.of(existingEntity));
        when(mapper.toEntity(order)).thenReturn(newEntity);
        when(jpaRepository.save(any(PaymentOrderEntity.class))).thenAnswer(invocation -> {
            PaymentOrderEntity saved = invocation.getArgument(0);
            saved.setId(existingId);
            return saved;
        });
        when(mapper.toDomain(any(PaymentOrderEntity.class))).thenReturn(order);

        // Act
        PaymentOrder result = adapter.save(order);

        // Assert
        assertThat(result).isNotNull();
        verify(jpaRepository).findByPaymentOrderReference("PO-1234567890123456");
        verify(mapper).toEntity(order);
        verify(jpaRepository).save(any(PaymentOrderEntity.class));
        assertThat(newEntity.getId()).isEqualTo(existingId); // ID preservado
    }

    @Test
    @DisplayName("Should find payment order by reference")
    void shouldFindPaymentOrderByReference() {
        // Arrange
        String reference = "PO-1234567890123456";
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setPaymentOrderReference(reference);
        PaymentOrder order = createValidPaymentOrder();
        
        when(jpaRepository.findByPaymentOrderReference(reference))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);

        // Act
        var result = adapter.findByReference(reference);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
        verify(jpaRepository).findByPaymentOrderReference(reference);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty when payment order not found")
    void shouldReturnEmptyWhenPaymentOrderNotFound() {
        // Arrange
        String reference = "PO-NOT-FOUND";
        when(jpaRepository.findByPaymentOrderReference(reference))
                .thenReturn(Optional.empty());

        // Act
        var result = adapter.findByReference(reference);

        // Assert
        assertThat(result).isEmpty();
        verify(jpaRepository).findByPaymentOrderReference(reference);
    }
}

