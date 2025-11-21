package com.bank.paymentinitiation.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PayeeReference value object.
 */
@DisplayName("PayeeReference Value Object Tests")
class PayeeReferenceTest {

    @Test
    @DisplayName("Should create PayeeReference successfully with valid value")
    void shouldCreatePayeeReferenceSuccessfullyWithValidValue() {
        // Arrange & Act
        PayeeReference reference = new PayeeReference("EC987654321098765432");

        // Assert
        Assertions.assertThat(reference.getValue()).isEqualTo("EC987654321098765432");
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowExceptionWhenValueIsNull() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new PayeeReference(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payee reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when value is empty")
    void shouldThrowExceptionWhenValueIsEmpty() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new PayeeReference(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payee reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void shouldThrowExceptionWhenValueIsBlank() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new PayeeReference("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payee reference must not be null or empty");
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreTheSame() {
        // Arrange
        PayeeReference ref1 = new PayeeReference("EC987654321098765432");
        PayeeReference ref2 = new PayeeReference("EC987654321098765432");

        // Assert
        Assertions.assertThat(ref1).isEqualTo(ref2);
        Assertions.assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Arrange
        PayeeReference ref1 = new PayeeReference("EC987654321098765432");
        PayeeReference ref2 = new PayeeReference("EC123456789012345678");

        // Assert
        Assertions.assertThat(ref1).isNotEqualTo(ref2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Arrange
        PayeeReference ref = new PayeeReference("EC987654321098765432");

        // Assert
        Assertions.assertThat(ref).isNotEqualTo(null);
        Assertions.assertThat(ref.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        // Arrange
        PayeeReference ref = new PayeeReference("EC987654321098765432");

        // Assert
        Assertions.assertThat(ref).isNotEqualTo("not a PayeeReference");
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        // Arrange
        PayeeReference ref = new PayeeReference("EC987654321098765432");

        // Act & Assert
        int hashCode1 = ref.hashCode();
        int hashCode2 = ref.hashCode();
        Assertions.assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Arrange
        PayeeReference ref = new PayeeReference("EC987654321098765432");

        // Act
        String toString = ref.toString();

        // Assert
        Assertions.assertThat(toString).isNotNull();
        Assertions.assertThat(toString).isNotEmpty();
    }
}

