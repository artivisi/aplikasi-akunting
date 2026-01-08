package com.artivisi.accountingfinance.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("LoginAttemptService Tests")
class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
        service.resetAllAttempts();
    }

    @Nested
    @DisplayName("Failed Login Tracking")
    class FailedLoginTracking {

        @Test
        @DisplayName("Should track failed login attempts")
        void shouldTrackFailedAttempts() {
            service.loginFailed("testuser");
            service.loginFailed("testuser");
            service.loginFailed("testuser");

            assertThat(service.getFailedAttempts("testuser")).isEqualTo(3);
        }

        @Test
        @DisplayName("Should track attempts case-insensitively")
        void shouldTrackCaseInsensitively() {
            service.loginFailed("TestUser");
            service.loginFailed("testuser");
            service.loginFailed("TESTUSER");

            assertThat(service.getFailedAttempts("testuser")).isEqualTo(3);
            assertThat(service.getFailedAttempts("TESTUSER")).isEqualTo(3);
        }

        @Test
        @DisplayName("Should track attempts separately per user")
        void shouldTrackSeparatelyPerUser() {
            service.loginFailed("user1");
            service.loginFailed("user1");
            service.loginFailed("user2");

            assertThat(service.getFailedAttempts("user1")).isEqualTo(2);
            assertThat(service.getFailedAttempts("user2")).isEqualTo(1);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should ignore null or blank usernames")
        void shouldIgnoreNullOrBlankUsernames(String username) {
            service.loginFailed(username);

            assertThat(service.getFailedAttempts(username)).isZero();
            assertThat(service.isBlocked(username)).isFalse();
        }
    }

    @Nested
    @DisplayName("Account Lockout")
    class AccountLockout {

        @Test
        @DisplayName("Should not block before reaching max attempts")
        void shouldNotBlockBeforeMaxAttempts() {
            for (int i = 0; i < 4; i++) {
                service.loginFailed("testuser");
            }

            assertThat(service.isBlocked("testuser")).isFalse();
        }

        @Test
        @DisplayName("Should block after 5 failed attempts")
        void shouldBlockAfter5Attempts() {
            for (int i = 0; i < 5; i++) {
                service.loginFailed("testuser");
            }

            assertThat(service.isBlocked("testuser")).isTrue();
        }

        @Test
        @DisplayName("Should remain blocked after additional attempts")
        void shouldRemainBlockedAfterMoreAttempts() {
            for (int i = 0; i < 10; i++) {
                service.loginFailed("testuser");
            }

            assertThat(service.isBlocked("testuser")).isTrue();
            assertThat(service.getFailedAttempts("testuser")).isEqualTo(10);
        }

        @Test
        @DisplayName("Should return remaining lockout time when blocked")
        void shouldReturnRemainingLockoutTime() {
            for (int i = 0; i < 5; i++) {
                service.loginFailed("testuser");
            }

            long remainingMinutes = service.getRemainingLockoutMinutes("testuser");

            // Should be around 30 minutes (give some margin for test execution time)
            assertThat(remainingMinutes).isBetween(29L, 31L);
        }

        @Test
        @DisplayName("Should return 0 lockout time when not blocked")
        void shouldReturnZeroWhenNotBlocked() {
            service.loginFailed("testuser");

            assertThat(service.getRemainingLockoutMinutes("testuser")).isZero();
        }
    }

    @Nested
    @DisplayName("Successful Login")
    class SuccessfulLogin {

        @Test
        @DisplayName("Should reset attempts on successful login")
        void shouldResetAttemptsOnSuccess() {
            service.loginFailed("testuser");
            service.loginFailed("testuser");
            service.loginFailed("testuser");

            service.loginSucceeded("testuser");

            assertThat(service.getFailedAttempts("testuser")).isZero();
            assertThat(service.isBlocked("testuser")).isFalse();
        }

        @Test
        @DisplayName("Should reset attempts case-insensitively")
        void shouldResetCaseInsensitively() {
            service.loginFailed("TestUser");
            service.loginFailed("TESTUSER");

            service.loginSucceeded("testuser");

            assertThat(service.getFailedAttempts("TestUser")).isZero();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should handle null or empty username on success")
        void shouldHandleNullOrEmptyOnSuccess(String username) {
            assertThatCode(() -> service.loginSucceeded(username))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("isBlocked() Method")
    class IsBlockedMethod {

        @Test
        @DisplayName("Should return false for unknown user")
        void shouldReturnFalseForUnknownUser() {
            assertThat(service.isBlocked("unknownuser")).isFalse();
        }

        @Test
        @DisplayName("Should check case-insensitively")
        void shouldCheckCaseInsensitively() {
            for (int i = 0; i < 5; i++) {
                service.loginFailed("TestUser");
            }

            assertThat(service.isBlocked("testuser")).isTrue();
            assertThat(service.isBlocked("TESTUSER")).isTrue();
        }
    }

    @Nested
    @DisplayName("getFailedAttempts() Method")
    class GetFailedAttemptsMethod {

        @Test
        @DisplayName("Should return 0 for unknown user")
        void shouldReturnZeroForUnknownUser() {
            assertThat(service.getFailedAttempts("unknownuser")).isZero();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return 0 for null or empty username")
        void shouldReturnZeroForNullOrEmpty(String username) {
            assertThat(service.getFailedAttempts(username)).isZero();
        }
    }

    @Nested
    @DisplayName("getRemainingLockoutMinutes() Method")
    class GetRemainingLockoutMinutesMethod {

        @Test
        @DisplayName("Should return 0 for unknown user")
        void shouldReturnZeroForUnknownUser() {
            assertThat(service.getRemainingLockoutMinutes("unknownuser")).isZero();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return 0 for null or empty username")
        void shouldReturnZeroForNullOrEmpty(String username) {
            assertThat(service.getRemainingLockoutMinutes(username)).isZero();
        }
    }

    @Nested
    @DisplayName("Reset All Attempts")
    class ResetAllAttempts {

        @Test
        @DisplayName("Should clear all tracked attempts")
        void shouldClearAllAttempts() {
            service.loginFailed("user1");
            service.loginFailed("user2");
            for (int i = 0; i < 5; i++) {
                service.loginFailed("user3");
            }

            service.resetAllAttempts();

            assertThat(service.getFailedAttempts("user1")).isZero();
            assertThat(service.getFailedAttempts("user2")).isZero();
            assertThat(service.getFailedAttempts("user3")).isZero();
            assertThat(service.isBlocked("user3")).isFalse();
        }
    }
}
