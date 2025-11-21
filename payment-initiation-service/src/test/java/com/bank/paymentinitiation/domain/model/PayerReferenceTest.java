package com.bank.paymentinitiation.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PayerReference value object.
 */
@DisplayName("PayerReference Value Object Tests")
class PayerReferenceTest {

    @Test
    @DisplayName("Should create PayerReference successfully with valid value")
    void shouldCreatePayerReferenceSuccessfullyWithValidValue() {
        // Arrange & Act
        PayerReference reference = new PayerReference("EC123456789012345678");

        // Assert
        Assertions.assertThat(reference.getValue()).isEqualTo("EC123456789012345678");
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowExceptionWhenValueIsNull() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new PayerReference(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payer reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when value is empty")
    void shouldThrowExceptionWhenValueIsEmpty() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new PayerReference(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payer reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void shouldThrowExceptionWhenValueIsBlank() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new PayerReference("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Payer reference must not be null or empty");
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreTheSame() {
        // Arrange
        PayerReference ref1 = new PayerReference("EC123456789012345678");
        PayerReference ref2 = new PayerReference("EC123456789012345678");

        // Assert
        Assertions.assertThat(ref1).isEqualTo(ref2);
        Assertions.assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Arrange
        PayerReference ref1 = new PayerReference("EC123456789012345678");
        PayerReference ref2 = new PayerReference("EC987654321098765432");

        // Assert
        Assertions.assertThat(ref1).isNotEqualTo(ref2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Arrange
        PayerReference ref = new PayerReference("EC123456789012345678");

        // Assert
        Assertions.assertThat(ref).isNotEqualTo(null);
        Assertions.assertThat(ref.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        // Arrange
        PayerReference ref = new PayerReference("EC123456789012345678");

        // Assert
        Assertions.assertThat(ref).isNotEqualTo("not a PayerReference");
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        // Arrange
        PayerReference ref = new PayerReference("EC123456789012345678");

        // Act & Assert
        int hashCode1 = ref.hashCode();
        int hashCode2 = ref.hashCode();
        Assertions.assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Arrange
        PayerReference ref = new PayerReference("EC123456789012345678");

        // Act
        String toString = ref.toString();

        // Assert
        Assertions.assertThat(toString).isNotNull();
        Assertions.assertThat(toString).isNotEmpty();
    }
}

