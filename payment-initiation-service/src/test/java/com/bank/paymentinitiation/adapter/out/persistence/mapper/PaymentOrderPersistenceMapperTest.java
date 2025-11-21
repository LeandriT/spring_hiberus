package com.bank.paymentinitiation.adapter.out.persistence.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bank.paymentinitiation.adapter.out.persistence.entity.PaymentOrderEntity;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("PaymentOrderPersistenceMapper Tests")
class PaymentOrderPersistenceMapperTest {

    @Autowired
    private PaymentOrderPersistenceMapper mapper;

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
    @DisplayName("Should map domain to entity")
    void shouldMapDomainToEntity() {
        // Arrange
        PaymentOrder domain = createValidPaymentOrder();

        // Act
        PaymentOrderEntity entity = mapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getPaymentOrderReference()).isEqualTo("PO-1234567890123456");
        assertThat(entity.getExternalReference()).isEqualTo("EXT-1");
        assertThat(entity.getPayerReference()).isEqualTo("EC123456789012345678");
        assertThat(entity.getPayeeReference()).isEqualTo("EC987654321098765432");
        assertThat(entity.getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getStatus()).isEqualTo("INITIATED");
        assertThat(entity.getRemittanceInformation()).isEqualTo("Factura 001-123");
        assertThat(entity.getRequestedExecutionDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("Should map entity to domain")
    void shouldMapEntityToDomain() {
        // Arrange
        PaymentOrderEntity entity = new PaymentOrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setPaymentOrderReference("PO-1234567890123456");
        entity.setExternalReference("EXT-1");
        entity.setPayerReference("EC123456789012345678");
        entity.setPayeeReference("EC987654321098765432");
        entity.setAmount(new BigDecimal("150.75"));
        entity.setCurrency("USD");
        entity.setRemittanceInformation("Factura 001-123");
        entity.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        entity.setStatus("INITIATED");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // Act
        PaymentOrder domain = mapper.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getPaymentOrderReference()).isEqualTo("PO-1234567890123456");
        assertThat(domain.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(domain.getPayerReference().getValue()).isEqualTo("EC123456789012345678");
        assertThat(domain.getPayeeReference().getValue()).isEqualTo("EC987654321098765432");
        assertThat(domain.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(domain.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(domain.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(domain.getRemittanceInformation()).isEqualTo("Factura 001-123");
        assertThat(domain.getRequestedExecutionDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("Should map all statuses correctly")
    void shouldMapAllStatusesCorrectly() {
        // Arrange
        PaymentStatus[] statuses = PaymentStatus.values();

        // Act & Assert
        for (PaymentStatus status : statuses) {
            PaymentOrder order = createValidPaymentOrder().toBuilder()
                    .status(status)
                    .build();
            PaymentOrderEntity entity = mapper.toEntity(order);
            assertThat(entity.getStatus()).isEqualTo(status.name());

            entity.setStatus(status.name());
            PaymentOrder mappedOrder = mapper.toDomain(entity);
            assertThat(mappedOrder.getStatus()).isEqualTo(status);
        }
    }
}

