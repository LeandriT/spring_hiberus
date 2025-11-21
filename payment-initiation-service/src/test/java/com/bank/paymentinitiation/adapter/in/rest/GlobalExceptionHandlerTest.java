package com.bank.paymentinitiation.adapter.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.bank.paymentinitiation.domain.exception.InvalidPaymentException;
import com.bank.paymentinitiation.domain.exception.PaymentOrderNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle PaymentOrderNotFoundException with 404")
    void shouldHandlePaymentOrderNotFoundException() {
        // Arrange
        PaymentOrderNotFoundException ex = new PaymentOrderNotFoundException("Order not found");

        // Act
        var response = handler.handlePaymentOrderNotFoundException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Payment Order Not Found");
        assertThat(response.getBody().getDetail()).isEqualTo("Order not found");
    }

    @Test
    @DisplayName("Should handle InvalidPaymentException with 400")
    void shouldHandleInvalidPaymentException() {
        // Arrange
        InvalidPaymentException ex = new InvalidPaymentException("Invalid payment");

        // Act
        var response = handler.handleInvalidPaymentException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Invalid Payment Order");
        assertThat(response.getBody().getDetail()).isEqualTo("Invalid payment");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400")
    void shouldHandleMethodArgumentNotValidException() {
        // Arrange
        BindException bindException = new BindException(new Object(), "target");
        bindException.addError(new FieldError("target", "debtorAccount", "must not be null"));
        bindException.addError(new FieldError("target", "amount", "must be greater than 0"));
        org.springframework.core.MethodParameter methodParameter = new org.springframework.core.MethodParameter(
                this.getClass().getDeclaredMethods()[0], 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindException);

        // Act
        var response = handler.handleMethodArgumentNotValidException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).contains("debtorAccount");
        assertThat(response.getBody().getDetail()).contains("amount");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with 400")
    void shouldHandleHttpMessageNotReadableException() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error");

        // Act
        var response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).contains("Invalid JSON format");
    }

    @Test
    @DisplayName("Should handle generic Exception with 500")
    void shouldHandleGenericException() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        var response = handler.handleGenericException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).contains("Unexpected error");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty detail")
    void shouldHandleMethodArgumentNotValidExceptionWithEmptyDetail() {
        // Arrange
        BindException bindException = new BindException(new Object(), "target");
        org.springframework.core.MethodParameter methodParameter = new org.springframework.core.MethodParameter(
                this.getClass().getDeclaredMethods()[0], 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindException);

        // Act
        var response = handler.handleMethodArgumentNotValidException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Cannot deserialize")
    void shouldHandleHttpMessageNotReadableExceptionWithCannotDeserialize() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Cannot deserialize value");

        // Act
        var response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).contains("Invalid request format");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic message")
    void shouldHandleHttpMessageNotReadableExceptionWithGenericMessage() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Some other error");

        // Act
        var response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("Request body is not readable or has invalid format");
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with null message")
    void shouldHandleHttpMessageNotReadableExceptionWithNullMessage() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("", new RuntimeException());

        // Act
        var response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("Request body is not readable or has invalid format");
    }
}

