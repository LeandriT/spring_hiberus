package com.bank.paymentinitiation.adapter.in.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bank.paymentinitiation.adapter.in.rest.mapper.PaymentOrderRestMapper;
import com.bank.paymentinitiation.application.service.PaymentOrderReferenceGenerator;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.in.InitiatePaymentOrderUseCase;
import com.bank.paymentinitiation.domain.port.in.RetrievePaymentOrderUseCase;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentOrdersController Tests")
class PaymentOrdersControllerTest {

    @Mock
    private InitiatePaymentOrderUseCase initiatePaymentOrderUseCase;

    @Mock
    private RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;

    @Mock
    private PaymentOrderRestMapper mapper;

    @Mock
    private PaymentOrderReferenceGenerator referenceGenerator;

    @InjectMocks
    private PaymentOrdersController controller;

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
    @DisplayName("Should initiate payment order successfully")
    void shouldInitiatePaymentOrderSuccessfully() {
        // Arrange
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        String generatedReference = "PO-1234567890123456";
        PaymentOrder domainOrder = createValidPaymentOrder();
        PaymentOrder initiatedOrder = createValidPaymentOrder();
        InitiatePaymentOrderResponse response = new InitiatePaymentOrderResponse();
        response.setPaymentOrderId("PO-1234567890123456");
        response.setStatus(com.bank.paymentinitiation.generated.model.PaymentStatus.INITIATED);

        when(referenceGenerator.generate()).thenReturn(generatedReference);
        when(mapper.toDomain(request, generatedReference)).thenReturn(domainOrder);
        when(initiatePaymentOrderUseCase.initiate(domainOrder)).thenReturn(initiatedOrder);
        when(mapper.toInitiateResponse(initiatedOrder)).thenReturn(response);

        // Act
        ResponseEntity<InitiatePaymentOrderResponse> result = controller.initiatePaymentOrder(request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getPaymentOrderId()).isEqualTo("PO-1234567890123456");
        verify(referenceGenerator).generate();
        verify(mapper).toDomain(request, generatedReference);
        verify(initiatePaymentOrderUseCase).initiate(domainOrder);
        verify(mapper).toInitiateResponse(initiatedOrder);
    }

    @Test
    @DisplayName("Should retrieve payment order successfully")
    void shouldRetrievePaymentOrderSuccessfully() {
        // Arrange
        String paymentOrderId = "PO-1234567890123456";
        PaymentOrder domainOrder = createValidPaymentOrder();
        RetrievePaymentOrderResponse response = new RetrievePaymentOrderResponse();
        response.setPaymentOrderId(paymentOrderId);

        when(retrievePaymentOrderUseCase.retrieve(paymentOrderId)).thenReturn(domainOrder);
        when(mapper.toRetrieveResponse(domainOrder)).thenReturn(response);

        // Act
        ResponseEntity<RetrievePaymentOrderResponse> result = controller.retrievePaymentOrder(paymentOrderId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getPaymentOrderId()).isEqualTo(paymentOrderId);
        verify(retrievePaymentOrderUseCase).retrieve(paymentOrderId);
        verify(mapper).toRetrieveResponse(domainOrder);
    }

    @Test
    @DisplayName("Should retrieve payment order status successfully")
    void shouldRetrievePaymentOrderStatusSuccessfully() {
        // Arrange
        String paymentOrderId = "PO-1234567890123456";
        PaymentOrder domainOrder = createValidPaymentOrder();
        PaymentOrderStatusResponse response = new PaymentOrderStatusResponse();
        response.setPaymentOrderId(paymentOrderId);
        response.setStatus(com.bank.paymentinitiation.generated.model.PaymentStatus.INITIATED);

        when(retrievePaymentOrderUseCase.retrieve(paymentOrderId)).thenReturn(domainOrder);
        when(mapper.toStatusResponse(domainOrder)).thenReturn(response);

        // Act
        ResponseEntity<PaymentOrderStatusResponse> result = controller.retrievePaymentOrderStatus(paymentOrderId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getPaymentOrderId()).isEqualTo(paymentOrderId);
        verify(retrievePaymentOrderUseCase).retrieve(paymentOrderId);
        verify(mapper).toStatusResponse(domainOrder);
    }
}

