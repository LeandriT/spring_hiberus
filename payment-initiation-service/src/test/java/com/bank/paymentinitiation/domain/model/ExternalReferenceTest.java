package com.bank.paymentinitiation.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ExternalReference value object.
 */
@DisplayName("ExternalReference Value Object Tests")
class ExternalReferenceTest {

    @Test
    @DisplayName("Should create ExternalReference successfully with valid value")
    void shouldCreateExternalReferenceSuccessfullyWithValidValue() {
        // Arrange & Act
        ExternalReference reference = new ExternalReference("EXT-123");

        // Assert
        Assertions.assertThat(reference.getValue()).isEqualTo("EXT-123");
    }

    @Test
    @DisplayName("Should throw exception when value is null")
    void shouldThrowExceptionWhenValueIsNull() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new ExternalReference(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("External reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when value is empty")
    void shouldThrowExceptionWhenValueIsEmpty() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new ExternalReference(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("External reference must not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when value is blank")
    void shouldThrowExceptionWhenValueIsBlank() {
        // Act & Assert
        Assertions.assertThatThrownBy(() -> new ExternalReference("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("External reference must not be null or empty");
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreTheSame() {
        // Arrange
        ExternalReference ref1 = new ExternalReference("EXT-123");
        ExternalReference ref2 = new ExternalReference("EXT-123");

        // Assert
        Assertions.assertThat(ref1).isEqualTo(ref2);
        Assertions.assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Arrange
        ExternalReference ref1 = new ExternalReference("EXT-123");
        ExternalReference ref2 = new ExternalReference("EXT-456");

        // Assert
        Assertions.assertThat(ref1).isNotEqualTo(ref2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Arrange
        ExternalReference ref = new ExternalReference("EXT-123");

        // Assert
        Assertions.assertThat(ref).isNotEqualTo(null);
        Assertions.assertThat(ref.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
        // Arrange
        ExternalReference ref = new ExternalReference("EXT-123");

        // Assert
        Assertions.assertThat(ref).isNotEqualTo("not an ExternalReference");
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        // Arrange
        ExternalReference ref = new ExternalReference("EXT-123");

        // Act & Assert
        int hashCode1 = ref.hashCode();
        int hashCode2 = ref.hashCode();
        Assertions.assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        // Arrange
        ExternalReference ref = new ExternalReference("EXT-123");

        // Act
        String toString = ref.toString();

        // Assert
        Assertions.assertThat(toString).isNotNull();
        Assertions.assertThat(toString).isNotEmpty();
    }
}

