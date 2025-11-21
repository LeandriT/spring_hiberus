package com.bank.paymentinitiation.adapter.in.rest.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.CreditorAccount;
import com.bank.paymentinitiation.generated.model.DebtorAccount;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("PaymentOrderRestMapper Tests")
class PaymentOrderRestMapperTest {

    @Autowired
    private PaymentOrderRestMapper mapper;

    @Test
    @DisplayName("Should map request to domain")
    void shouldMapRequestToDomain() {
        // Arrange
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        request.setDebtorAccount(new DebtorAccount("EC123456789012345678"));
        request.setCreditorAccount(new CreditorAccount("EC987654321098765432"));
        com.bank.paymentinitiation.generated.model.PaymentAmount amount =
                new com.bank.paymentinitiation.generated.model.PaymentAmount(
                        new BigDecimal("150.75"),
                        com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum.USD);
        request.setInstructedAmount(amount);
        request.setRemittanceInformation("Factura 001-123");
        request.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        String paymentOrderReference = "PO-1234567890123456";

        // Act
        PaymentOrder domain = mapper.toDomain(request, paymentOrderReference);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getPaymentOrderReference()).isEqualTo(paymentOrderReference);
        assertThat(domain.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(domain.getPayerReference().getValue()).isEqualTo("EC123456789012345678");
        assertThat(domain.getPayeeReference().getValue()).isEqualTo("EC987654321098765432");
        assertThat(domain.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(domain.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(domain.getRemittanceInformation()).isEqualTo("Factura 001-123");
        assertThat(domain.getRequestedExecutionDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("Should map domain to initiate response")
    void shouldMapDomainToInitiateResponse() {
        // Arrange
        PaymentOrder domain = PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        InitiatePaymentOrderResponse response = mapper.toInitiateResponse(domain);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderId()).isEqualTo("PO-1234567890123456");
        assertThat(response.getStatus()).isNotNull();
        assertThat(response.getStatus().getValue()).isEqualTo("INITIATED");
    }

    @Test
    @DisplayName("Should map domain to retrieve response")
    void shouldMapDomainToRetrieveResponse() {
        // Arrange
        PaymentOrder domain = PaymentOrder.builder()
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

        // Act
        RetrievePaymentOrderResponse response = mapper.toRetrieveResponse(domain);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderId()).isEqualTo("PO-1234567890123456");
        assertThat(response.getExternalReference()).isEqualTo("EXT-1");
        assertThat(response.getDebtorAccount().getIban()).isEqualTo("EC123456789012345678");
        assertThat(response.getCreditorAccount().getIban()).isEqualTo("EC987654321098765432");
        assertThat(response.getInstructedAmount().getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(response.getRemittanceInformation()).isEqualTo("Factura 001-123");
        assertThat(response.getStatus().getValue()).isEqualTo("INITIATED");
        assertThat(response.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("Should map domain to status response")
    void shouldMapDomainToStatusResponse() {
        // Arrange
        PaymentOrder domain = PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Act
        PaymentOrderStatusResponse response = mapper.toStatusResponse(domain);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderId()).isEqualTo("PO-1234567890123456");
        assertThat(response.getStatus().getValue()).isEqualTo("PENDING");
        assertThat(response.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null LocalDateTime in localDateTimeToOffsetDateTime")
    void shouldHandleNullLocalDateTimeInLocalDateTimeToOffsetDateTime() {
        // Arrange
        PaymentOrder domain = PaymentOrder.builder()
                .paymentOrderReference("PO-1234567890123456")
                .externalReference(new ExternalReference("EXT-1"))
                .payerReference(new PayerReference("EC123456789012345678"))
                .payeeReference(new PayeeReference("EC987654321098765432"))
                .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
                .requestedExecutionDate(LocalDate.now().plusDays(1))
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(null) // null updatedAt
                .build();

        // Act
        RetrievePaymentOrderResponse response = mapper.toRetrieveResponse(domain);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getLastUpdate()).isNull();
    }
}

