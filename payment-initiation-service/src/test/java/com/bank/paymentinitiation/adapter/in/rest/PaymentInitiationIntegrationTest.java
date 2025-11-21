package com.bank.paymentinitiation.adapter.in.rest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("Payment Initiation Integration Tests")
class PaymentInitiationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentOrderJpaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // Limpiar estado entre tests
    }

    private Map<String, Object> createValidRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("externalReference", "EXT-1");
        
        Map<String, String> debtorAccount = new HashMap<>();
        debtorAccount.put("iban", "EC123456789012345678"); // minLength: 15
        request.put("debtorAccount", debtorAccount);
        
        Map<String, String> creditorAccount = new HashMap<>();
        creditorAccount.put("iban", "EC987654321098765432"); // minLength: 15
        request.put("creditorAccount", creditorAccount);
        
        Map<String, Object> instructedAmount = new HashMap<>();
        instructedAmount.put("amount", 150.75);
        instructedAmount.put("currency", "USD");
        request.put("instructedAmount", instructedAmount);
        
        request.put("remittanceInformation", "Factura 001-123");
        request.put("requestedExecutionDate", LocalDate.now().plusDays(1).toString()); // Fecha futura
        
        return request;
    }

    @Test
    @DisplayName("Should create payment order successfully via POST")
    void shouldCreatePaymentOrderSuccessfully() throws Exception {
        // Arrange
        Map<String, Object> request = createValidRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        String[] paymentOrderId = new String[1];
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.paymentOrderId").exists()
                .jsonPath("$.paymentOrderId").isNotEmpty()
                .jsonPath("$.status").isEqualTo("INITIATED")
                .jsonPath("$").value(body -> {
                    Map<String, Object> response = objectMapper.convertValue(body, Map.class);
                    paymentOrderId[0] = (String) response.get("paymentOrderId");
                });

        // Verificar que el ID fue capturado
        assert paymentOrderId[0] != null && !paymentOrderId[0].isEmpty();
    }

    @Test
    @DisplayName("Should retrieve payment order completely via GET")
    void shouldRetrievePaymentOrderCompletely() throws Exception {
        // Arrange - Crear orden primero
        Map<String, Object> request = createValidRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        
        String[] paymentOrderId = new String[1];
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$").value(body -> {
                    Map<String, Object> response = objectMapper.convertValue(body, Map.class);
                    paymentOrderId[0] = (String) response.get("paymentOrderId");
                });

        // Act & Assert - Recuperar orden
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}", paymentOrderId[0])
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.paymentOrderId").isEqualTo(paymentOrderId[0])
                .jsonPath("$.externalReference").isEqualTo("EXT-1")
                .jsonPath("$.debtorAccount.iban").isEqualTo("EC123456789012345678")
                .jsonPath("$.creditorAccount.iban").isEqualTo("EC987654321098765432")
                .jsonPath("$.instructedAmount.amount").isEqualTo(150.75)
                .jsonPath("$.instructedAmount.currency").isEqualTo("USD")
                .jsonPath("$.remittanceInformation").isEqualTo("Factura 001-123")
                .jsonPath("$.requestedExecutionDate").isEqualTo(LocalDate.now().plusDays(1).toString())
                .jsonPath("$.status").isEqualTo("INITIATED")
                .jsonPath("$.lastUpdate").exists();
    }

    @Test
    @DisplayName("Should retrieve payment order status via GET /status")
    void shouldRetrievePaymentOrderStatus() throws Exception {
        // Arrange - Crear orden primero
        Map<String, Object> request = createValidRequest();
        String requestJson = objectMapper.writeValueAsString(request);
        
        String[] paymentOrderId = new String[1];
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$").value(body -> {
                    Map<String, Object> response = objectMapper.convertValue(body, Map.class);
                    paymentOrderId[0] = (String) response.get("paymentOrderId");
                });

        // Act & Assert - Recuperar solo status
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}/status", paymentOrderId[0])
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.paymentOrderId").isEqualTo(paymentOrderId[0])
                .jsonPath("$.status").isEqualTo("INITIATED")
                .jsonPath("$.lastUpdate").exists();
    }

    @Test
    @DisplayName("Should return 404 when payment order not found")
    void shouldReturn404WhenPaymentOrderNotFound() {
        // Arrange
        String nonExistentId = "PO-99999999999999999999999999999999"; // Cumple patrón ^PO-[0-9]+$

        // Act & Assert
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}", nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.title").isEqualTo("Payment Order Not Found")
                .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 when request body is invalid - missing debtorAccount")
    void shouldReturn400WhenMissingDebtorAccount() throws Exception {
        // Arrange
        Map<String, Object> request = createValidRequest();
        request.remove("debtorAccount"); // Remover campo requerido
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 when request body is invalid - missing instructedAmount")
    void shouldReturn400WhenMissingInstructedAmount() throws Exception {
        // Arrange
        Map<String, Object> request = createValidRequest();
        request.remove("instructedAmount"); // Remover campo requerido
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 when amount is zero or negative")
    void shouldReturn400WhenAmountIsZeroOrNegative() throws Exception {
        // Arrange
        Map<String, Object> request = createValidRequest();
        Map<String, Object> instructedAmount = (Map<String, Object>) request.get("instructedAmount");
        instructedAmount.put("amount", 0.0); // Valor inválido
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 when IBAN is too short")
    void shouldReturn400WhenIbanIsTooShort() throws Exception {
        // Arrange
        Map<String, Object> request = createValidRequest();
        Map<String, String> debtorAccount = (Map<String, String>) request.get("debtorAccount");
        debtorAccount.put("iban", "EC12DEBTOR"); // Solo 10 caracteres, debe ser >= 15
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 when requested execution date is in the past")
    void shouldReturn400WhenRequestedExecutionDateIsInPast() throws Exception {
        // Arrange
        Map<String, Object> request = createValidRequest();
        request.put("requestedExecutionDate", LocalDate.now().minusDays(1).toString()); // Fecha en el pasado
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Invalid Payment Order")
                .jsonPath("$.detail").exists();
    }
}

