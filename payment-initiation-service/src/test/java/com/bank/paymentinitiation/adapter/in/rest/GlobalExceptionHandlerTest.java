package com.bank.paymentinitiation.adapter.in.rest;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle PaymentOrderNotFoundException and return 404")
    void shouldHandlePaymentOrderNotFoundExceptionAndReturn404() {
        // Arrange
        String message = "Payment order with reference 'PO-123' not found";
        PaymentOrderNotFoundException ex = new PaymentOrderNotFoundException(message);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handlePaymentOrderNotFoundException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(404);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Payment Order Not Found");
        Assertions.assertThat(response.getBody().getDetail()).isEqualTo(message);
        Assertions.assertThat(response.getBody().getType().toString()).isEqualTo("about:blank");
    }

    @Test
    @DisplayName("Should handle InvalidPaymentException and return 400")
    void shouldHandleInvalidPaymentExceptionAndReturn400() {
        // Arrange
        String message = "Invalid payment amount";
        InvalidPaymentException ex = new InvalidPaymentException(message);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleInvalidPaymentException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(400);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Invalid Payment Order");
        Assertions.assertThat(response.getBody().getDetail()).isEqualTo(message);
        Assertions.assertThat(response.getBody().getType().toString()).isEqualTo("about:blank");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithFieldErrors() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("request", "debtorAccount", "must not be null"));
        fieldErrors.add(new FieldError("request", "instructedAmount", "must not be null"));
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleMethodArgumentNotValidException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(400);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        Assertions.assertThat(response.getBody().getDetail())
            .contains("debtorAccount")
            .contains("instructedAmount")
            .contains("Validation failed");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with object errors when no field errors")
    void shouldHandleMethodArgumentNotValidExceptionWithObjectErrors() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        
        List<FieldError> fieldErrors = new ArrayList<>();
        List<ObjectError> objectErrors = new ArrayList<>();
        objectErrors.add(new ObjectError("request", "Invalid request format"));
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(bindingResult.getAllErrors()).thenReturn(objectErrors);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleMethodArgumentNotValidException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(400);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        Assertions.assertThat(response.getBody().getDetail())
            .contains("Invalid request format")
            .contains("Validation failed");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty errors")
    void shouldHandleMethodArgumentNotValidExceptionWithEmptyErrors() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleMethodArgumentNotValidException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(400);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        Assertions.assertThat(response.getBody().getDetail()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with message")
    void shouldHandleHttpMessageNotReadableExceptionWithMessage() {
        // Arrange
        String errorMessage = "JSON parse error: Unexpected character";
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn(errorMessage);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(400);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        Assertions.assertThat(response.getBody().getDetail()).contains("JSON parse error");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with null message")
    void shouldHandleHttpMessageNotReadableExceptionWithNullMessage() {
        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn(null);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(400);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        Assertions.assertThat(response.getBody().getDetail()).isEqualTo("Invalid request body format");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with multiline message")
    void shouldHandleHttpMessageNotReadableExceptionWithMultilineMessage() {
        // Arrange
        String errorMessage = "JSON parse error: Unexpected character\nat line 1, column 5";
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn(errorMessage);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getDetail())
            .contains("JSON parse error")
            .doesNotContain("at line 1"); // Should only contain first line
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with long message")
    void shouldHandleHttpMessageNotReadableExceptionWithLongMessage() {
        // Arrange
        String longMessage = "A".repeat(250); // Message longer than 200 characters
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn(longMessage);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getDetail().length()).isLessThanOrEqualTo(203); // 200 + "..."
        Assertions.assertThat(response.getBody().getDetail()).endsWith("...");
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500")
    void shouldHandleGenericExceptionAndReturn500() {
        // Arrange
        RuntimeException ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(500);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        Assertions.assertThat(response.getBody().getDetail()).isEqualTo("An internal server error occurred");
        Assertions.assertThat(response.getBody().getType().toString()).isEqualTo("about:blank");
    }

    @Test
    @DisplayName("Should handle generic Exception with cause")
    void shouldHandleGenericExceptionWithCause() {
        // Arrange
        IllegalArgumentException cause = new IllegalArgumentException("Root cause");
        RuntimeException ex = new RuntimeException("Unexpected error", cause);

        // Act
        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex);

        // Assert
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatus()).isEqualTo(500);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    }
}

