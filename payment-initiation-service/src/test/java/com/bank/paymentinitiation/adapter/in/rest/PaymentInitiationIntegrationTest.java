package com.bank.paymentinitiation.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

/**
 * Integration tests for Payment Initiation REST API.
 * 
 * <p>Tests the complete application stack end-to-end:
 * <ul>
 *   <li>REST Controllers</li>
 *   <li>Mappers (REST DTOs ↔ Domain)</li>
 *   <li>Application Services</li>
 *   <li>Domain Model</li>
 *   <li>Repository Adapters</li>
 *   <li>JPA Repositories</li>
 *   <li>H2 Database</li>
 *   <li>JSON Serialization/Deserialization</li>
 *   <li>Bean Validation</li>
 *   <li>Exception Handling (RFC 7807)</li>
 * </ul>
 * 
 * <p>Uses real H2 database (no mocks) to validate the complete integration.
 * 
 * <p>⚠️ IMPORTANT: IBANs must have at least 15 characters (as defined in OpenAPI spec).
 * 
 * <p>⚠️ NOTE: These tests require PaymentOrderRepositoryAdapter to be fully implemented
 * (with PaymentOrderEntity, PaymentOrderJpaRepository, and PaymentOrderPersistenceMapper).
 * Until then, tests that interact with persistence will fail with UnsupportedOperationException.
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
    private com.bank.paymentinitiation.adapter.out.persistence.jpa.PaymentOrderJpaRepository repository;

    @BeforeEach
    void setUp() {
        // Clean H2 state between tests
        repository.deleteAll();
    }

    // Test data helpers
    private static final String VALID_EXTERNAL_REF = "EXT-123";
    private static final String VALID_DEBTOR_IBAN = "EC123456789012345678"; // 20 chars (>= 15)
    private static final String VALID_CREDITOR_IBAN = "EC987654321098765432"; // 20 chars (>= 15)
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("150.75");
    private static final String VALID_CURRENCY = "USD";
    private static final String VALID_REMITTANCE_INFO = "Factura 001-123";
    private static final String VALID_EXECUTION_DATE = java.time.LocalDate.now().plusDays(1).toString(); // Fecha futura (mañana)

    private String createValidRequestJson() {
        return String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"%s\" }," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": %s, \"currency\": \"%s\" }," +
            "  \"remittanceInformation\": \"%s\"," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_DEBTOR_IBAN,
            VALID_CREDITOR_IBAN,
            VALID_AMOUNT,
            VALID_CURRENCY,
            VALID_REMITTANCE_INFO,
            VALID_EXECUTION_DATE
        );
    }

    // ==================== POST /payment-initiation/payment-orders - Success ====================

    @Test
    @DisplayName("Should create payment order successfully")
    void shouldCreatePaymentOrderSuccessfully() {
        // Arrange
        String requestJson = createValidRequestJson();
        String[] paymentOrderReference = new String[1];

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.paymentOrderId").exists()
            .jsonPath("$.paymentOrderId").value(ref -> paymentOrderReference[0] = ref.toString())
            .jsonPath("$.status").isEqualTo("ACCEPTED")
            .jsonPath("$.paymentOrderId").isNotEmpty();

        // Verify paymentOrderReference was captured and has correct format
        Assertions.assertThat(paymentOrderReference[0])
            .isNotNull()
            .isNotEmpty()
            .startsWith("PO-");
    }

    // ==================== GET /payment-initiation/payment-orders/{id} - Success ====================

    @Test
    @DisplayName("Should retrieve payment order completely")
    void shouldRetrievePaymentOrderCompletely() {
        // Arrange - Create order first
        String requestJson = createValidRequestJson();
        String[] paymentOrderReference = new String[1];

        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.paymentOrderId").value(ref -> paymentOrderReference[0] = ref.toString());

        String createdId = paymentOrderReference[0];

        // Act & Assert - Retrieve the order
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", createdId)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.paymentOrderId").isEqualTo(createdId)
            .jsonPath("$.externalReference").isEqualTo(VALID_EXTERNAL_REF)
            .jsonPath("$.debtorAccount.iban").isEqualTo(VALID_DEBTOR_IBAN)
            .jsonPath("$.creditorAccount.iban").isEqualTo(VALID_CREDITOR_IBAN)
            .jsonPath("$.instructedAmount.amount").isEqualTo(VALID_AMOUNT.doubleValue())
            .jsonPath("$.instructedAmount.currency").isEqualTo(VALID_CURRENCY)
            .jsonPath("$.remittanceInformation").isEqualTo(VALID_REMITTANCE_INFO)
            .jsonPath("$.requestedExecutionDate").isEqualTo(VALID_EXECUTION_DATE)
            .jsonPath("$.status").isEqualTo("ACCEPTED")
            .jsonPath("$.lastUpdate").exists();
    }

    // ==================== GET /payment-initiation/payment-orders/{id}/status - Success ====================

    @Test
    @DisplayName("Should retrieve payment order status only")
    void shouldRetrievePaymentOrderStatusOnly() {
        // Arrange - Create order first
        String requestJson = createValidRequestJson();
        String[] paymentOrderReference = new String[1];

        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.paymentOrderId").value(ref -> paymentOrderReference[0] = ref.toString());

        String createdId = paymentOrderReference[0];

        // Act & Assert - Retrieve status only
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}/status", createdId)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.paymentOrderId").isEqualTo(createdId)
            .jsonPath("$.status").isEqualTo("ACCEPTED")
            .jsonPath("$.lastUpdate").exists();
    }

    // ==================== Error Cases (404, 400) ====================

    @Test
    @DisplayName("Should return 404 NOT FOUND when payment order does not exist")
    void shouldReturn404WhenPaymentOrderDoesNotExist() {
        // Arrange
        String nonExistentId = "PO-9999999999999999"; // Must match pattern ^PO-[0-9]+$

        // Act & Assert
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}", nonExistentId)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.title").isEqualTo("Payment Order Not Found")
            .jsonPath("$.detail").value(detail -> Assertions.assertThat(detail.toString())
                .contains(nonExistentId));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when status query for non-existent order")
    void shouldReturn404WhenStatusQueryForNonExistentOrder() {
        // Arrange
        String nonExistentId = "PO-9999999999999999"; // Must match pattern ^PO-[0-9]+$

        // Act & Assert
        webTestClient.get()
            .uri("/payment-initiation/payment-orders/{id}/status", nonExistentId)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.title").isEqualTo("Payment Order Not Found")
            .jsonPath("$.detail").value(detail -> Assertions.assertThat(detail.toString())
                .contains(nonExistentId));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when debtorAccount is missing")
    void shouldReturn400WhenDebtorAccountIsMissing() {
        // Arrange
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": %s, \"currency\": \"%s\" }," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_CREDITOR_IBAN,
            VALID_AMOUNT,
            VALID_CURRENCY,
            VALID_EXECUTION_DATE
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when instructedAmount is missing")
    void shouldReturn400WhenInstructedAmountIsMissing() {
        // Arrange
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"%s\" }," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_DEBTOR_IBAN,
            VALID_CREDITOR_IBAN,
            VALID_EXECUTION_DATE
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when amount is zero")
    void shouldReturn400WhenAmountIsZero() {
        // Arrange
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"%s\" }," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": 0, \"currency\": \"%s\" }," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_DEBTOR_IBAN,
            VALID_CREDITOR_IBAN,
            VALID_CURRENCY,
            VALID_EXECUTION_DATE
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when amount is negative")
    void shouldReturn400WhenAmountIsNegative() {
        // Arrange
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"%s\" }," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": -10.50, \"currency\": \"%s\" }," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_DEBTOR_IBAN,
            VALID_CREDITOR_IBAN,
            VALID_CURRENCY,
            VALID_EXECUTION_DATE
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when currency is invalid")
    void shouldReturn400WhenCurrencyIsInvalid() {
        // Arrange
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"%s\" }," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": %s, \"currency\": \"INVALID\" }," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_DEBTOR_IBAN,
            VALID_CREDITOR_IBAN,
            VALID_AMOUNT,
            VALID_EXECUTION_DATE
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when IBAN is too short (less than 15 characters)")
    void shouldReturn400WhenIbanIsTooShort() {
        // Arrange - IBAN with less than 15 characters (minLength: 15 in OpenAPI spec)
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"EC12DEBTOR\" }," + // Only 10 chars (< 15 minLength)
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": %s, \"currency\": \"%s\" }," +
            "  \"requestedExecutionDate\": \"%s\"" +
            "}",
            VALID_EXTERNAL_REF,
            VALID_CREDITOR_IBAN,
            VALID_AMOUNT,
            VALID_CURRENCY,
            VALID_EXECUTION_DATE
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when date format is invalid")
    void shouldReturn400WhenDateFormatIsInvalid() {
        // Arrange
        String invalidRequestJson = String.format(
            "{" +
            "  \"externalReference\": \"%s\"," +
            "  \"debtorAccount\": { \"iban\": \"%s\" }," +
            "  \"creditorAccount\": { \"iban\": \"%s\" }," +
            "  \"instructedAmount\": { \"amount\": %s, \"currency\": \"%s\" }," +
            "  \"requestedExecutionDate\": \"2025-13-45\"" + // Invalid date format
            "}",
            VALID_EXTERNAL_REF,
            VALID_DEBTOR_IBAN,
            VALID_CREDITOR_IBAN,
            VALID_AMOUNT,
            VALID_CURRENCY
        );

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequestJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when JSON is malformed")
    void shouldReturn400WhenJsonIsMalformed() {
        // Arrange
        String malformedJson = "{ \"externalReference\": \"EXT-123\","; // Missing closing brace

        // Act & Assert
        webTestClient.post()
            .uri("/payment-initiation/payment-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(malformedJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.detail").exists();
    }
}

