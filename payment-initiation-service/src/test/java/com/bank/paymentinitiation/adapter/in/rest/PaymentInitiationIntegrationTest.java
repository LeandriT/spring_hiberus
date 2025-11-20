package com.bank.paymentinitiation.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository;

/**
 * Integration tests for Payment Initiation REST API.
 * 
 * These tests verify end-to-end functionality:
 * - Controllers
 * - Mappers
 * - Application services
 * - Repository
 * - JPA
 * - H2 database
 * - Validations
 * - JSON serialization
 * 
 * Uses real H2 database (no mocks).
 * Aligns with OpenAPI contract and Postman collection.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("Payment Initiation Integration Tests")
class PaymentInitiationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentOrderJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // Clean state between tests
    }

    // ==================== POST /payment-initiation/payment-orders ====================

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Should create payment order successfully")
    void shouldCreatePaymentOrderSuccessfully() {
        // Arrange
        String requestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "instructedAmount": { "amount": 150.75, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.paymentOrderReference").exists()
            .jsonPath("$.paymentOrderStatus").value(status -> {
                assert status.toString().equals("INITIATED");
            })
            .jsonPath("$.debtorAccount.iban").isEqualTo("EC123456789012345678")
            .jsonPath("$.creditorAccount.iban").isEqualTo("EC987654321098765432")
            .jsonPath("$.instructedAmount.amount").isEqualTo(150.75)
            .jsonPath("$.instructedAmount.currency").value(currency -> {
                assert currency.toString().equals("USD");
            })
            .jsonPath("$.remittanceInformation").isEqualTo("Factura 001-123")
            .jsonPath("$.requestedExecutionDate").isEqualTo("2025-12-31")
            .jsonPath("$.createdAt").exists();
    }

    // ==================== GET /payment-initiation/payment-orders/{id} ====================

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id} - Should retrieve payment order successfully")
    void shouldRetrievePaymentOrderSuccessfully() {
        // Arrange - Create order first
        String requestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "instructedAmount": { "amount": 150.75, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Create order and capture ID
        String[] paymentOrderReference = new String[1];
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.paymentOrderReference")
            .value(ref -> paymentOrderReference[0] = ref.toString());

        // Act & Assert - Retrieve the order
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", paymentOrderReference[0])
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.paymentOrderReference").isEqualTo(paymentOrderReference[0])
            .jsonPath("$.externalReference").isEqualTo("EXT-1")
            .jsonPath("$.debtorAccount.iban").isEqualTo("EC123456789012345678")
            .jsonPath("$.creditorAccount.iban").isEqualTo("EC987654321098765432")
            .jsonPath("$.instructedAmount.amount").isEqualTo(150.75)
            .jsonPath("$.instructedAmount.currency").isEqualTo("USD")
            .jsonPath("$.remittanceInformation").isEqualTo("Factura 001-123")
            .jsonPath("$.requestedExecutionDate").isEqualTo("2025-12-31")
            .jsonPath("$.paymentOrderStatus").isEqualTo("INITIATED")
            .jsonPath("$.createdAt").exists()
            .jsonPath("$.lastUpdated").exists();
    }

    // ==================== GET /payment-initiation/payment-orders/{id}/status ====================

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id}/status - "
            + "Should retrieve payment order status successfully")
    void shouldRetrievePaymentOrderStatusSuccessfully() {
        // Arrange - Create order first
        String requestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "instructedAmount": { "amount": 150.75, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Create order and capture ID
        String[] paymentOrderReference = new String[1];
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.paymentOrderReference")
            .value(ref -> paymentOrderReference[0] = ref.toString());

        // Act & Assert - Retrieve status
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}/status", paymentOrderReference[0])
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.paymentOrderReference").isEqualTo(paymentOrderReference[0])
            .jsonPath("$.paymentOrderStatus").isEqualTo("INITIATED")
            .jsonPath("$.lastUpdated").exists();
    }

    // ==================== Error Cases ====================

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id} - Should return 404 when order not found")
    void shouldReturn404WhenOrderNotFound() {
        // Arrange
        String nonExistentId = "PO-NON-EXISTENT-123";

        // Act & Assert
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", nonExistentId)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.title").isEqualTo("Payment Order Not Found")
            .jsonPath("$.detail").value(detail -> {
                assert detail.toString().contains("Payment order not found");
            })
            .jsonPath("$.paymentOrderReference").isEqualTo(nonExistentId);
    }

    @Test
    @DisplayName("GET /payment-initiation/payment-orders/{id}/status - Should return 404 when order not found")
    void shouldReturn404WhenOrderNotFoundForStatus() {
        // Arrange
        String nonExistentId = "PO-NON-EXISTENT-456";

        // Act & Assert
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}/status", nonExistentId)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.title").isEqualTo("Payment Order Not Found")
            .jsonPath("$.detail").value(detail -> {
                assert detail.toString().contains("Payment order not found");
            });
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Should return 400 when debtorAccount is missing")
    void shouldReturn400WhenDebtorAccountIsMissing() {
        // Arrange
        String invalidRequestJson = """
            {
              "externalReference": "EXT-1",
              "creditorAccount": { "iban": "EC98CREDITOR" },
              "instructedAmount": { "amount": 150.75, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Should return 400 when instructedAmount is missing")
    void shouldReturn400WhenInstructedAmountIsMissing() {
        // Arrange
        String invalidRequestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Should return 400 when amount is zero")
    void shouldReturn400WhenAmountIsZero() {
        // Arrange
        String invalidRequestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "instructedAmount": { "amount": 0, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").value(title -> {
                assert title.toString().contains("Invalid Payment Order") || 
                       title.toString().contains("Bad Request");
            });
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Should return 400 when amount is negative")
    void shouldReturn400WhenAmountIsNegative() {
        // Arrange
        String invalidRequestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "instructedAmount": { "amount": -100, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "2025-12-31"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").value(title -> {
                assert title.toString().contains("Invalid Payment Order") || 
                       title.toString().contains("Bad Request");
            });
    }

    @Test
    @DisplayName("POST /payment-initiation/payment-orders - Should return 400 when requestedExecutionDate is invalid")
    void shouldReturn400WhenRequestedExecutionDateIsInvalid() {
        // Arrange
        String invalidRequestJson = """
            {
              "externalReference": "EXT-1",
              "debtorAccount": { "iban": "EC123456789012345678" },
              "creditorAccount": { "iban": "EC987654321098765432" },
              "instructedAmount": { "amount": 150.75, "currency": "USD" },
              "remittanceInformation": "Factura 001-123",
              "requestedExecutionDate": "invalid-date"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest();
    }
}

