package com.bank.paymentinitiation.adapter.in.rest.mapper;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración para PaymentOrderRestMapper.
 * 
 * Verifica que los mapeos entre DTOs REST y objetos de dominio funcionan correctamente.
 */
@SpringBootTest
@DisplayName("PaymentOrderRestMapper Tests")
class PaymentOrderRestMapperTest {
    
    @Autowired
    private PaymentOrderRestMapper mapper;
    
    @Test
    @DisplayName("Debería mapear InitiatePaymentOrderRequest a PaymentOrder del dominio")
    void shouldMapInitiatePaymentOrderRequestToDomain() {
        // Given
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        
        Account debtorAccount = new Account("DEBTOR-IBAN");
        request.setDebtorAccount(debtorAccount);
        
        Account creditorAccount = new Account("CREDITOR-IBAN");
        request.setCreditorAccount(creditorAccount);
        
        com.bank.paymentinitiation.generated.model.PaymentAmount dtoAmount = 
            new com.bank.paymentinitiation.generated.model.PaymentAmount(new BigDecimal("150.75"), "USD");
        request.setInstructedAmount(dtoAmount);
        request.setRemittanceInformation("Factura 001");
        request.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        
        String paymentOrderReference = "PO-0001";
        
        // When
        PaymentOrder domain = mapper.toDomain(request, paymentOrderReference);
        
        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getPaymentOrderReference()).isEqualTo(paymentOrderReference);
        assertThat(domain.getExternalReference().getValue()).isEqualTo("EXT-1");
        assertThat(domain.getPayerReference().getValue()).isEqualTo("DEBTOR-IBAN");
        assertThat(domain.getPayeeReference().getValue()).isEqualTo("CREDITOR-IBAN");
        assertThat(domain.getInstructedAmount().getValue()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(domain.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(domain.getRemittanceInformation()).isEqualTo("Factura 001");
        assertThat(domain.getStatus()).isEqualTo(PaymentStatus.INITIATED);
    }
    
    @Test
    @DisplayName("Debería mapear PaymentOrder del dominio a InitiatePaymentOrderResponse")
    void shouldMapDomainToInitiatePaymentOrderResponse() {
        // Given
        PaymentOrder domain = PaymentOrder.create(
            "PO-0001",
            ExternalReference.of("EXT-1"),
            PayerReference.of("DEBTOR-IBAN"),
            PayeeReference.of("CREDITOR-IBAN"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            "Factura 001",
            LocalDate.now().plusDays(1)
        );
        
        // When
        InitiatePaymentOrderResponse response = mapper.toInitiateResponse(domain);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderReference()).isEqualTo("PO-0001");
        assertThat(response.getExternalReference()).isEqualTo("EXT-1");
        assertThat(response.getDebtorAccount().getIban()).isEqualTo("DEBTOR-IBAN");
        assertThat(response.getCreditorAccount().getIban()).isEqualTo("CREDITOR-IBAN");
        assertThat(response.getInstructedAmount().getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(response.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(response.getRemittanceInformation()).isEqualTo("Factura 001");
        assertThat(response.getStatus()).isEqualTo(com.bank.paymentinitiation.generated.model.PaymentStatus.INITIATED);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Debería mapear PaymentOrder del dominio a RetrievePaymentOrderResponse")
    void shouldMapDomainToRetrievePaymentOrderResponse() {
        // Given
        PaymentOrder domain = PaymentOrder.create(
            "PO-0001",
            ExternalReference.of("EXT-1"),
            PayerReference.of("DEBTOR-IBAN"),
            PayeeReference.of("CREDITOR-IBAN"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            "Factura 001",
            LocalDate.now().plusDays(1)
        );
        
        // When
        RetrievePaymentOrderResponse response = mapper.toRetrieveResponse(domain);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderReference()).isEqualTo("PO-0001");
        assertThat(response.getExternalReference()).isEqualTo("EXT-1");
        assertThat(response.getDebtorAccount().getIban()).isEqualTo("DEBTOR-IBAN");
        assertThat(response.getCreditorAccount().getIban()).isEqualTo("CREDITOR-IBAN");
    }
    
    @Test
    @DisplayName("Debería mapear PaymentOrder del dominio a PaymentOrderStatusResponse")
    void shouldMapDomainToPaymentOrderStatusResponse() {
        // Given
        PaymentOrder domain = PaymentOrder.create(
            "PO-0001",
            ExternalReference.of("EXT-1"),
            PayerReference.of("DEBTOR-IBAN"),
            PayeeReference.of("CREDITOR-IBAN"),
            PaymentAmount.of(new BigDecimal("150.75"), "USD"),
            "Factura 001",
            LocalDate.now().plusDays(1)
        );
        domain.changeStatus(com.bank.paymentinitiation.domain.model.PaymentStatus.PENDING);
        
        // When
        PaymentOrderStatusResponse response = mapper.toStatusResponse(domain);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentOrderReference()).isEqualTo("PO-0001");
        assertThat(response.getPaymentOrderStatus()).isEqualTo(com.bank.paymentinitiation.generated.model.PaymentStatus.PENDING);
        assertThat(response.getLastUpdated()).isNotNull();
    }
    
    @Test
    @DisplayName("Debería mapear correctamente cuando remittanceInformation es null")
    void shouldMapCorrectlyWhenRemittanceInformationIsNull() {
        // Given
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        request.setDebtorAccount(new Account("DEBTOR-IBAN"));
        request.setCreditorAccount(new Account("CREDITOR-IBAN"));
        request.setInstructedAmount(new com.bank.paymentinitiation.generated.model.PaymentAmount(new BigDecimal("100.00"), "USD"));
        request.setRemittanceInformation(null);
        request.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        
        // When
        PaymentOrder domain = mapper.toDomain(request, "PO-0001");
        
        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getRemittanceInformation()).isNull();
    }
}

