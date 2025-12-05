package com.artivisi.accountingfinance.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PasswordValidator Tests")
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Nested
    @DisplayName("Valid Passwords")
    class ValidPasswords {

        @Test
        @DisplayName("Should accept password meeting all requirements")
        void shouldAcceptValidPassword() {
            String password = "SecurePass123!";

            var result = validator.validate(password);

            assertThat(result.isValid()).isTrue();
            assertThat(result.errors()).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "MyPassword123!",
                "Abcdefgh1234@",
                "P@ssw0rd!2024",
                "Complex#Pass99",
                "Test123456789$"
        })
        @DisplayName("Should accept various valid passwords")
        void shouldAcceptVariousValidPasswords(String password) {
            assertThat(validator.isValid(password)).isTrue();
        }

        @Test
        @DisplayName("Should accept password with various special characters")
        void shouldAcceptVariousSpecialChars() {
            String[] specialChars = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "[", "]", "{", "}", ";", "'", ":", "\"", "\\", "|", ",", ".", "<", ">", "/", "?"};

            for (String special : specialChars) {
                String password = "SecurePass1" + special;
                assertThat(validator.isValid(password))
                        .withFailMessage("Should accept special char: " + special)
                        .isTrue();
            }
        }
    }

    @Nested
    @DisplayName("Invalid Passwords - Null/Empty")
    class NullAndEmptyPasswords {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should reject null or empty password")
        void shouldRejectNullOrEmpty(String password) {
            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).contains("Password wajib diisi");
        }
    }

    @Nested
    @DisplayName("Invalid Passwords - Length")
    class LengthValidation {

        @Test
        @DisplayName("Should reject password shorter than 12 characters")
        void shouldRejectShortPassword() {
            String password = "Short1!aB"; // 9 chars

            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).anyMatch(e -> e.contains("minimal 12 karakter"));
        }

        @Test
        @DisplayName("Should accept password with exactly 12 characters")
        void shouldAcceptExact12Chars() {
            String password = "Abcdefgh12!@"; // exactly 12 chars

            assertThat(validator.isValid(password)).isTrue();
        }

        @Test
        @DisplayName("Should accept very long passwords")
        void shouldAcceptLongPassword() {
            String password = "A".repeat(50) + "a".repeat(50) + "1!";

            assertThat(validator.isValid(password)).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid Passwords - Missing Requirements")
    class MissingRequirements {

        @Test
        @DisplayName("Should reject password without uppercase")
        void shouldRejectNoUppercase() {
            String password = "lowercase1234!";

            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).anyMatch(e -> e.contains("huruf besar"));
        }

        @Test
        @DisplayName("Should reject password without lowercase")
        void shouldRejectNoLowercase() {
            String password = "UPPERCASE1234!";

            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).anyMatch(e -> e.contains("huruf kecil"));
        }

        @Test
        @DisplayName("Should reject password without digit")
        void shouldRejectNoDigit() {
            String password = "NoDigitsHere!!";

            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).anyMatch(e -> e.contains("angka"));
        }

        @Test
        @DisplayName("Should reject password without special character")
        void shouldRejectNoSpecialChar() {
            String password = "NoSpecialChar1";

            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).anyMatch(e -> e.contains("karakter khusus"));
        }

        @Test
        @DisplayName("Should collect all errors when multiple requirements fail")
        void shouldCollectAllErrors() {
            String password = "short"; // fails: length, uppercase, digit, special

            var result = validator.validate(password);

            assertThat(result.isValid()).isFalse();
            assertThat(result.errors()).hasSize(4);
            assertThat(result.getErrorMessage()).contains("12 karakter", "huruf besar", "angka", "karakter khusus");
        }
    }

    @Nested
    @DisplayName("isValid() Quick Check")
    class IsValidMethod {

        @Test
        @DisplayName("Should return true for valid password")
        void shouldReturnTrueForValid() {
            assertThat(validator.isValid("ValidPass123!")).isTrue();
        }

        @Test
        @DisplayName("Should return false for invalid password")
        void shouldReturnFalseForInvalid() {
            assertThat(validator.isValid("short")).isFalse();
        }
    }

    @Nested
    @DisplayName("PasswordValidationResult")
    class ValidationResultTests {

        @Test
        @DisplayName("Should combine errors in getErrorMessage")
        void shouldCombineErrors() {
            String password = "abc"; // fails multiple requirements

            var result = validator.validate(password);
            String errorMessage = result.getErrorMessage();

            assertThat(errorMessage).contains(". ");
        }

        @Test
        @DisplayName("Should return empty message when valid")
        void shouldReturnEmptyMessageWhenValid() {
            var result = validator.validate("ValidPass123!");

            assertThat(result.getErrorMessage()).isEmpty();
        }
    }
}
