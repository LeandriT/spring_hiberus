package com.bank.paymentinitiation.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Exception Constructors Tests")
class ExceptionConstructorsTest {

    @Test
    @DisplayName("InvalidPaymentException should support constructor with message and cause")
    void invalidPaymentExceptionShouldSupportConstructorWithMessageAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");
        String message = "Invalid payment";

        // Act
        InvalidPaymentException ex = new InvalidPaymentException(message, cause);

        // Assert
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("PaymentOrderNotFoundException should support constructor with message and cause")
    void paymentOrderNotFoundExceptionShouldSupportConstructorWithMessageAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Root cause");
        String message = "Payment order not found";

        // Act
        PaymentOrderNotFoundException ex = new PaymentOrderNotFoundException(message, cause);

        // Assert
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}

