package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.Account;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PaymentOrderRestMapper.
 * 
 * Tests cover:
 * - DTO to Domain mapping
 * - Domain to Response DTO mapping
 * - Key field mappings
 * - Value object conversions
 */
@SpringBootTest
@DisplayName("PaymentOrderRestMapper Tests")
class PaymentOrderRestMapperTest {
    
    @Autowired
    private PaymentOrderRestMapper mapper;
    
    private InitiatePaymentOrderRequest request;
    private PaymentOrder domainOrder;
    
    @BeforeEach
    void setUp() {
        // Setup request
        Account debtorAccount = new Account();
        debtorAccount.setIban("EC12DEBTOR");
        
        Account creditorAccount = new Account();
        creditorAccount.setIban("EC98CREDITOR");
        
        com.bank.paymentinitiation.generated.model.PaymentAmount amount = 
            new com.bank.paymentinitiation.generated.model.PaymentAmount();
        amount.setAmount(150.75);
        amount.setCurrency(com.bank.paymentinitiation.generated.model.PaymentAmount.CurrencyEnum.USD);
        
        request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        request.setDebtorAccount(debtorAccount);
        request.setCreditorAccount(creditorAccount);
        request.setInstructedAmount(amount);
        request.setRemittanceInformation("Factura 001-123");
        request.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        
        // Setup domain order
        domainOrder = PaymentOrder.builder()
            .paymentOrderReference("PO-12345")
            .externalReference(ExternalReference.of("EXT-1"))
            .payerReference(PayerReference.of("EC12DEBTOR"))
            .payeeReference(PayeeReference.of("EC98CREDITOR"))
            .instructedAmount(PaymentAmount.of(new BigDecimal("150.75"), "USD"))
            .remittanceInformation("Factura 001-123")
            .requestedExecutionDate(LocalDate.now().plusDays(1))
            .status(PaymentStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("Should map Request to Domain")
    void shouldMapRequestToDomain() {
        // Arrange
        String paymentOrderReference = "PO-GENERATED-123";
        
        // Act
        PaymentOrder result = mapper.toDomain(request, paymentOrderReference);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo(paymentOrderReference);
        assertThat(result.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(result.getPayerReference().getValue()).isEqualTo("EC12DEBTOR");
        assertThat(result.getPayeeReference().getValue()).isEqualTo("EC98CREDITOR");
        assertThat(result.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(result.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(result.getRemittanceInformation()).isEqualTo("Factura 001-123");
        assertThat(result.getRequestedExecutionDate()).isEqualTo(request.getRequestedExecutionDate());
    }
    
    @Test
    @DisplayName("Should map Domain to InitiateResponse")
    void shouldMapDomainToInitiateResponse() {
        // Act
        InitiatePaymentOrderResponse result = mapper.toInitiateResponse(domainOrder);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-12345");
        assertThat(result.getPaymentOrderStatus()).isNotNull();
        assertThat(result.getDebtorAccount()).isNotNull();
        assertThat(result.getDebtorAccount().getIban()).isEqualTo("EC12DEBTOR");
        assertThat(result.getCreditorAccount()).isNotNull();
        assertThat(result.getCreditorAccount().getIban()).isEqualTo("EC98CREDITOR");
        assertThat(result.getInstructedAmount()).isNotNull();
        assertThat(result.getInstructedAmount().getAmount()).isEqualTo(150.75);
        assertThat(result.getRemittanceInformation()).isEqualTo("Factura 001-123");
        assertThat(result.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should map Domain to RetrieveResponse")
    void shouldMapDomainToRetrieveResponse() {
        // Act
        RetrievePaymentOrderResponse result = mapper.toRetrieveResponse(domainOrder);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-12345");
        assertThat(result.getPaymentOrderStatus()).isNotNull();
        assertThat(result.getExternalReference()).isEqualTo("EXT-1");
        assertThat(result.getDebtorAccount()).isNotNull();
        assertThat(result.getCreditorAccount()).isNotNull();
        assertThat(result.getInstructedAmount()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getLastUpdated()).isNotNull();
    }
    
    @Test
    @DisplayName("Should map Domain to StatusResponse")
    void shouldMapDomainToStatusResponse() {
        // Act
        PaymentOrderStatusResponse result = mapper.toStatusResponse(domainOrder);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentOrderReference()).isEqualTo("PO-12345");
        assertThat(result.getPaymentOrderStatus()).isNotNull();
        assertThat(result.getLastUpdated()).isNotNull();
    }
}

