package com.bank.paymentinitiation;

import com.bank.paymentinitiation.domain.model.PaymentOrder;
import com.bank.paymentinitiation.domain.model.ExternalReference;
import com.bank.paymentinitiation.domain.model.PayerReference;
import com.bank.paymentinitiation.domain.model.PayeeReference;
import com.bank.paymentinitiation.domain.model.PaymentAmount;
import com.bank.paymentinitiation.domain.model.PaymentStatus;
import com.bank.paymentinitiation.domain.port.out.PaymentOrderRepository;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderRequest;
import com.bank.paymentinitiation.generated.model.InitiatePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.RetrievePaymentOrderResponse;
import com.bank.paymentinitiation.generated.model.PaymentOrderStatusResponse;
import com.bank.paymentinitiation.generated.model.Account;
import com.bank.paymentinitiation.generated.model.ProblemDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración para la API REST de Payment Initiation.
 * 
 * Estos tests verifican el comportamiento completo del sistema desde las
 * peticiones HTTP REST hasta la persistencia en base de datos, usando
 * WebTestClient para hacer peticiones reales al servidor.
 * 
 * Características:
 * - Usa SpringBootTest con puerto aleatorio
 * - Usa H2 en memoria (configuración por defecto)
 * - Verifica respuestas HTTP, códigos de estado y contenido JSON
 * - Alineado con la colección Postman
 * 
 * @author Payment Initiation Service Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("Payment Initiation Integration Tests")
class PaymentInitiationIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PaymentOrderRepository paymentOrderRepository;
    
    @Test
    @DisplayName("POST /payment-initiation/payment-orders debería crear una orden válida y retornar 201")
    void shouldCreatePaymentOrderAndReturn201() throws Exception {
        // Given - Request alineado con colección Postman
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        
        Account debtorAccount = new Account("EC12DEBTOR");
        request.setDebtorAccount(debtorAccount);
        
        Account creditorAccount = new Account("EC98CREDITOR");
        request.setCreditorAccount(creditorAccount);
        
        com.bank.paymentinitiation.generated.model.PaymentAmount amount = 
            new com.bank.paymentinitiation.generated.model.PaymentAmount(new BigDecimal("150.75"), "USD");
        request.setInstructedAmount(amount);
        request.setRemittanceInformation("Factura 001-123");
        request.setRequestedExecutionDate(LocalDate.now().plusDays(1));
        
        // When/Then
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(InitiatePaymentOrderResponse.class)
            .value(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getPaymentOrderReference()).isNotBlank();
                assertThat(response.getPaymentOrderReference()).startsWith("PO-");
                assertThat(response.getExternalReference()).isEqualTo("EXT-1");
                assertThat(response.getDebtorAccount().getIban()).isEqualTo("EC12DEBTOR");
                assertThat(response.getCreditorAccount().getIban()).isEqualTo("EC98CREDITOR");
                assertThat(response.getInstructedAmount().getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
                assertThat(response.getInstructedAmount().getCurrency()).isEqualTo("USD");
                assertThat(response.getRemittanceInformation()).isEqualTo("Factura 001-123");
                assertThat(response.getStatus()).isEqualTo(com.bank.paymentinitiation.generated.model.PaymentStatus.INITIATED);
                assertThat(response.getCreatedAt()).isNotNull();
                assertThat(response.getUpdatedAt()).isNotNull();
            });
    }
    
    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id} debería retornar 200 con la orden completa")
    void shouldRetrievePaymentOrderAndReturn200() {
        // Given - Crear una orden primero
        PaymentOrder order = PaymentOrder.create(
            "PO-TEST-001",
            ExternalReference.of("EXT-TEST"),
            PayerReference.of("PAYER-TEST"),
            PayeeReference.of("PAYEE-TEST"),
            PaymentAmount.of(new BigDecimal("100.00"), "USD"),
            "Test order",
            LocalDate.now().plusDays(1)
        );
        paymentOrderRepository.save(order);
        
        // When/Then
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", "PO-TEST-001")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(RetrievePaymentOrderResponse.class)
            .value(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getPaymentOrderReference()).isEqualTo("PO-TEST-001");
                assertThat(response.getExternalReference()).isEqualTo("EXT-TEST");
                assertThat(response.getDebtorAccount().getIban()).isEqualTo("PAYER-TEST");
                assertThat(response.getCreditorAccount().getIban()).isEqualTo("PAYEE-TEST");
                assertThat(response.getInstructedAmount().getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
                assertThat(response.getStatus()).isEqualTo(com.bank.paymentinitiation.generated.model.PaymentStatus.INITIATED);
            });
    }
    
    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id}/status debería retornar 200 con el estado")
    void shouldRetrievePaymentOrderStatusAndReturn200() {
        // Given - Crear una orden primero
        PaymentOrder order = PaymentOrder.create(
            "PO-TEST-002",
            ExternalReference.of("EXT-TEST-2"),
            PayerReference.of("PAYER-TEST-2"),
            PayeeReference.of("PAYEE-TEST-2"),
            PaymentAmount.of(new BigDecimal("200.00"), "USD"),
            "Test order 2",
            LocalDate.now().plusDays(1)
        );
        order.changeStatus(PaymentStatus.PENDING);
        paymentOrderRepository.save(order);
        
        // When/Then
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}/status", "PO-TEST-002")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(PaymentOrderStatusResponse.class)
            .value(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getPaymentOrderReference()).isEqualTo("PO-TEST-002");
                assertThat(response.getPaymentOrderStatus()).isEqualTo(com.bank.paymentinitiation.generated.model.PaymentStatus.PENDING);
                assertThat(response.getLastUpdated()).isNotNull();
            });
    }
    
    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id} con id inexistente debería retornar 404")
    void shouldReturn404WhenOrderNotFound() {
        // When/Then
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", "PO-NONEXISTENT")
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody(ProblemDetail.class)
            .value(problem -> {
                assertThat(problem).isNotNull();
                assertThat(problem.getTitle()).isEqualTo("Payment Order Not Found");
                assertThat(problem.getStatus()).isEqualTo(404);
            });
    }
    
    @Test
    @DisplayName("POST /payment-initiation/payment-orders con body inválido debería retornar 400")
    void shouldReturn400WhenRequestIsInvalid() {
        // Given - Request inválido (sin campos requeridos)
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        // No se establecen campos requeridos
        
        // When/Then
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody(ProblemDetail.class)
            .value(problem -> {
                assertThat(problem).isNotNull();
                assertThat(problem.getStatus()).isEqualTo(400);
            });
    }
    
    @Test
    @DisplayName("POST /payment-initiation/payment-orders con fecha en el pasado debería retornar 400")
    void shouldReturn400WhenExecutionDateIsInPast() {
        // Given - Request con fecha en el pasado
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        request.setDebtorAccount(new Account("EC12DEBTOR"));
        request.setCreditorAccount(new Account("EC98CREDITOR"));
        request.setInstructedAmount(new com.bank.paymentinitiation.generated.model.PaymentAmount(new BigDecimal("100.00"), "USD"));
        request.setRemittanceInformation("Test");
        request.setRequestedExecutionDate(LocalDate.now().minusDays(1)); // Fecha en el pasado
        
        // When/Then
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }
    
    @Test
    @DisplayName("POST /payment-initiation/payment-orders y luego GET debería retornar la misma orden")
    void shouldCreateAndThenRetrieveSameOrder() {
        // Given
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-FLOW");
        request.setDebtorAccount(new Account("EC12DEBTOR"));
        request.setCreditorAccount(new Account("EC98CREDITOR"));
        request.setInstructedAmount(new com.bank.paymentinitiation.generated.model.PaymentAmount(new BigDecimal("250.50"), "EUR"));
        request.setRemittanceInformation("Factura FLOW-001");
        request.setRequestedExecutionDate(LocalDate.now().plusDays(2));
        
        // When - Crear orden
        String paymentOrderReference = webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(InitiatePaymentOrderResponse.class)
            .returnResult()
            .getResponseBody()
            .getPaymentOrderReference();
        
        // Then - Recuperar la misma orden
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", paymentOrderReference)
            .exchange()
            .expectStatus().isOk()
            .expectBody(RetrievePaymentOrderResponse.class)
            .value(response -> {
                assertThat(response.getPaymentOrderReference()).isEqualTo(paymentOrderReference);
                assertThat(response.getExternalReference()).isEqualTo("EXT-FLOW");
                assertThat(response.getInstructedAmount().getAmount()).isEqualByComparingTo(new BigDecimal("250.50"));
                assertThat(response.getInstructedAmount().getCurrency()).isEqualTo("EUR");
            });
    }
}

