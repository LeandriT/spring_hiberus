package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentAccount;
import com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatus;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Integration tests for PaymentOrderRestMapper (MapStruct generated implementation).
 * 
 * <p>Tests verify that mappings between REST DTOs and Domain Objects work correctly.
 */
@SpringBootTest
@DisplayName("PaymentOrderRestMapper Tests")
class PaymentOrderRestMapperTest {

    @Autowired
    private PaymentOrderRestMapper mapper;

    private InitiatePaymentOrderRequest validRequest;
    private PaymentOrder validPaymentOrder;

    @BeforeEach
    void setUp() {
        // Setup valid request
        validRequest = new InitiatePaymentOrderRequest();
        validRequest.setExternalReference("EXT-123");
        
        PaymentAccount debtorAccount = new PaymentAccount();
        debtorAccount.setIban("EC123456789012345678");
        validRequest.setDebtorAccount(debtorAccount);
        
        PaymentAccount creditorAccount = new PaymentAccount();
        creditorAccount.setIban("EC987654321098765432");
        validRequest.setCreditorAccount(creditorAccount);
        
        com.bank.paymentinitiation.generated.model.PaymentAmount instructedAmount =
            new com.bank.paymentinitiation.generated.model.PaymentAmount();
        instructedAmount.setAmount(new BigDecimal("150.75"));
        instructedAmount.setCurrency(CurrencyEnum.USD);
        validRequest.setInstructedAmount(instructedAmount);
        
        validRequest.setRemittanceInformation("Factura 001-123");
        validRequest.setRequestedExecutionDate(LocalDate.now().plusDays(1));

        // Setup valid domain object
        validPaymentOrder = PaymentOrder.builder()
            .paymentOrderReference("PO-TEST123456")
            .externalReference(new ExternalReference("EXT-123"))
            .payerReference(new PayerReference("EC123456789012345678"))
            .payeeReference(new PayeeReference("EC987654321098765432"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentStatus.INITIATED)
            .build();
        validPaymentOrder.initiate();
    }

    @Test
    @DisplayName("Should map InitiatePaymentOrderRequest to PaymentOrder domain")
    void shouldMapRequestToDomain() {
        // Arrange
        String paymentOrderReference = "PO-TEST123456";

        // Act
        PaymentOrder result = mapper.toDomain(validRequest, paymentOrderReference);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentOrderReference()).isEqualTo(paymentOrderReference);
        Assertions.assertThat(result.getExternalReference().getValue()).isEqualTo("EXT-123");
        Assertions.assertThat(result.getPayerReference().getValue()).isEqualTo("EC123456789012345678");
        Assertions.assertThat(result.getPayeeReference().getValue()).isEqualTo("EC987654321098765432");
        Assertions.assertThat(result.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        Assertions.assertThat(result.getInstructedAmount().getCurrency()).isEqualTo("USD");
        Assertions.assertThat(result.getRemittanceInformation()).isEqualTo("Factura 001-123");
    }

    @Test
    @DisplayName("Should map PaymentOrder domain to InitiatePaymentOrderResponse")
    void shouldMapDomainToInitiateResponse() {
        // Act
        InitiatePaymentOrderResponse result = mapper.toInitiateResponse(validPaymentOrder);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentOrderId()).isEqualTo("PO-TEST123456");
        Assertions.assertThat(result.getStatus()).isEqualTo(PaymentOrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should map PaymentOrder domain to RetrievePaymentOrderResponse")
    void shouldMapDomainToRetrieveResponse() {
        // Act
        RetrievePaymentOrderResponse result = mapper.toRetrieveResponse(validPaymentOrder);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentOrderId()).isEqualTo("PO-TEST123456");
        Assertions.assertThat(result.getExternalReference()).isEqualTo("EXT-123");
        Assertions.assertThat(result.getDebtorAccount().getIban()).isEqualTo("EC123456789012345678");
        Assertions.assertThat(result.getCreditorAccount().getIban()).isEqualTo("EC987654321098765432");
        Assertions.assertThat(result.getInstructedAmount().getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        Assertions.assertThat(result.getStatus()).isEqualTo(PaymentOrderStatus.ACCEPTED);
        Assertions.assertThat(result.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("Should map PaymentOrder domain to PaymentOrderStatusResponse")
    void shouldMapDomainToStatusResponse() {
        // Act
        PaymentOrderStatusResponse result = mapper.toStatusResponse(validPaymentOrder);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPaymentOrderId()).isEqualTo("PO-TEST123456");
        Assertions.assertThat(result.getStatus()).isEqualTo(PaymentOrderStatus.ACCEPTED);
        Assertions.assertThat(result.getLastUpdate()).isNotNull();
    }

    @Test
    @DisplayName("Should map all payment statuses correctly")
    void shouldMapAllStatuses() {
        // Test INITIATED → ACCEPTED
        PaymentOrder order1 = validPaymentOrder.toBuilder().status(PaymentStatus.INITIATED).build();
        InitiatePaymentOrderResponse response1 = mapper.toInitiateResponse(order1);
        Assertions.assertThat(response1.getStatus()).isEqualTo(PaymentOrderStatus.ACCEPTED);

        // Test PENDING → PENDING
        PaymentOrder order2 = validPaymentOrder.toBuilder().status(PaymentStatus.PENDING).build();
        InitiatePaymentOrderResponse response2 = mapper.toInitiateResponse(order2);
        Assertions.assertThat(response2.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);

        // Test PROCESSED → PROCESSING
        PaymentOrder order3 = validPaymentOrder.toBuilder().status(PaymentStatus.PROCESSED).build();
        InitiatePaymentOrderResponse response3 = mapper.toInitiateResponse(order3);
        Assertions.assertThat(response3.getStatus()).isEqualTo(PaymentOrderStatus.PROCESSING);

        // Test COMPLETED → SETTLED
        PaymentOrder order4 = validPaymentOrder.toBuilder().status(PaymentStatus.COMPLETED).build();
        InitiatePaymentOrderResponse response4 = mapper.toInitiateResponse(order4);
        Assertions.assertThat(response4.getStatus()).isEqualTo(PaymentOrderStatus.SETTLED);

        // Test FAILED → FAILED
        PaymentOrder order5 = validPaymentOrder.toBuilder().status(PaymentStatus.FAILED).build();
        InitiatePaymentOrderResponse response5 = mapper.toInitiateResponse(order5);
        Assertions.assertThat(response5.getStatus()).isEqualTo(PaymentOrderStatus.FAILED);

        // Test CANCELLED → CANCELLED
        PaymentOrder order6 = validPaymentOrder.toBuilder().status(PaymentStatus.CANCELLED).build();
        InitiatePaymentOrderResponse response6 = mapper.toInitiateResponse(order6);
        Assertions.assertThat(response6.getStatus()).isEqualTo(PaymentOrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should convert LocalDateTime to OffsetDateTime correctly")
    void shouldConvertLocalDateTimeToOffsetDateTimeCorrectly() {
        // Arrange
        LocalDateTime localDateTime = LocalDateTime.of(2025, 10, 30, 16, 25, 30);
        PaymentOrder orderWithDate = validPaymentOrder.toBuilder()
            .updatedAt(localDateTime)
            .build();

        // Act
        PaymentOrderStatusResponse result = mapper.toStatusResponse(orderWithDate);

        // Assert
        Assertions.assertThat(result.getLastUpdate()).isNotNull();
        OffsetDateTime expected = localDateTime.atOffset(ZoneOffset.UTC);
        Assertions.assertThat(result.getLastUpdate()).isEqualTo(expected);
    }
}

