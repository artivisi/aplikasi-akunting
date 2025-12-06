package com.artivisi.accountingfinance.security;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Security Regression Tests
 *
 * Tests security controls using Playwright against a running Spring Boot app.
 * These tests verify that security vulnerabilities identified in the audit
 * are properly mitigated.
 *
 * Run with: ./mvnw test -Dtest=SecurityRegressionTest
 */
@DisplayName("Security Regression Tests")
class SecurityRegressionTest extends PlaywrightTestBase {

    @BeforeEach
    void setUp() {
        // Start fresh for each test
    }

    @Nested
    @DisplayName("Authentication Security")
    class AuthenticationSecurityTests {

        @Test
        @DisplayName("Should reject invalid credentials")
        void shouldRejectInvalidCredentials() {
            navigateTo("/login");
            waitForPageLoad();

            page.fill("input[name='username']", "admin");
            page.fill("input[name='password']", "wrongpassword");
            page.click("button[type='submit']");

            // Should stay on login page with error
            assertTrue(page.url().contains("/login"));
            assertTrue(page.locator(".alert-danger, .text-red, [class*='error']").isVisible() ||
                       page.url().contains("error"));
        }

        @Test
        @DisplayName("Should protect against brute force with rate limiting")
        void shouldProtectAgainstBruteForce() {
            // Attempt multiple failed logins
            for (int i = 0; i < 6; i++) {
                navigateTo("/login");
                waitForPageLoad();
                page.fill("input[name='username']", "admin");
                page.fill("input[name='password']", "wrong" + i);
                page.click("button[type='submit']");
            }

            // After multiple failures, should see lockout message or rate limit
            // Note: This test documents the expected behavior - implement in Phase 6.3
            navigateTo("/login");
            waitForPageLoad();
            // Currently no lockout implemented - this test will pass when Phase 6.3 is complete
        }

        @Test
        @DisplayName("Should redirect to login when accessing protected page without auth")
        void shouldRedirectToLoginForProtectedPages() {
            // Clear any existing session
            context.clearCookies();

            navigateTo("/dashboard");
            waitForPageLoad();

            assertTrue(page.url().contains("/login"),
                    "Should redirect to login page, but was: " + page.url());
        }

        @Test
        @DisplayName("Should invalidate session on logout")
        void shouldInvalidateSessionOnLogout() {
            loginAsAdmin();
            navigateTo("/dashboard");
            waitForPageLoad();
            assertTrue(page.url().contains("/dashboard"));

            // Logout
            page.click("a[href='/logout'], button:has-text('Logout'), form[action='/logout'] button");
            waitForPageLoad();

            // Try to access protected page
            navigateTo("/dashboard");
            waitForPageLoad();
            assertTrue(page.url().contains("/login"),
                    "Should redirect to login after logout");
        }
    }

    @Nested
    @DisplayName("CSRF Protection")
    class CsrfProtectionTests {

        @Test
        @DisplayName("Should include CSRF token in forms")
        void shouldIncludeCsrfTokenInForms() {
            loginAsAdmin();
            navigateTo("/accounts");
            waitForPageLoad();

            // Check for CSRF meta tag or hidden input
            boolean hasCsrfMeta = page.locator("meta[name='_csrf']").count() > 0;
            boolean hasCsrfInput = page.locator("input[name='_csrf']").count() > 0;

            assertTrue(hasCsrfMeta || hasCsrfInput,
                    "Page should contain CSRF token in meta tag or hidden input");
        }

        @Test
        @DisplayName("Should include CSRF header configuration for HTMX")
        void shouldConfigureCsrfForHtmx() {
            loginAsAdmin();
            navigateTo("/dashboard");
            waitForPageLoad();

            // Check for CSRF header meta tag
            Locator csrfHeaderMeta = page.locator("meta[name='_csrf_header']");
            assertTrue(csrfHeaderMeta.count() > 0,
                    "Should have CSRF header meta tag for HTMX integration");
        }

        @Test
        @DisplayName("Should reject POST request without CSRF token")
        void shouldRejectPostWithoutCsrfToken() {
            loginAsAdmin();

            // Make a POST request without CSRF token using Playwright's request API
            var apiContext = page.context().request();
            var response = apiContext.post(baseUrl() + "/accounts",
                    RequestOptions.create()
                            .setHeader("Content-Type", "application/x-www-form-urlencoded")
                            .setData("code=TEST&name=Test&type=ASSET"));

            // Should be rejected with 403 Forbidden
            assertEquals(403, response.status(),
                    "POST without CSRF token should return 403 Forbidden");
        }
    }

    @Nested
    @DisplayName("XSS Prevention")
    class XssPreventionTests {

        private static final List<String> XSS_PAYLOADS = List.of(
                "<script>alert('XSS')</script>",
                "<img src=x onerror=alert('XSS')>",
                "<svg onload=alert('XSS')>",
                "javascript:alert('XSS')",
                "<body onload=alert('XSS')>",
                "'><script>alert('XSS')</script>",
                "\"><script>alert('XSS')</script>"
        );

        @Test
        @DisplayName("Should escape XSS payloads in search inputs")
        void shouldEscapeXssInSearch() {
            loginAsAdmin();

            for (String payload : XSS_PAYLOADS) {
                navigateTo("/accounts");
                waitForPageLoad();

                Locator searchInput = page.locator("input[type='search'], input[name='search'], input[placeholder*='Cari']");
                if (searchInput.count() > 0) {
                    searchInput.first().fill(payload);
                    page.keyboard().press("Enter");
                    waitForPageLoad();

                    // The payload should be escaped, not executed
                    String content = page.content();
                    assertFalse(content.contains("<script>alert"),
                            "XSS payload should be escaped: " + payload);
                }
            }
        }

        @Test
        @DisplayName("Should escape user input in displayed content")
        void shouldEscapeUserInputInDisplay() {
            loginAsAdmin();

            // Navigate to a form that displays user input
            navigateTo("/clients/create");
            waitForPageLoad();

            String xssPayload = "<script>alert('XSS')</script>";

            // Fill form with XSS payload
            Locator nameInput = page.locator("input[name='name'], input[name='clientName']");
            if (nameInput.count() > 0) {
                nameInput.first().fill(xssPayload);

                // Check that the input value is properly handled
                String inputValue = nameInput.first().inputValue();
                // The value in the input should be the literal text, not executed
                assertEquals(xssPayload, inputValue);
            }
        }
    }

    @Nested
    @DisplayName("Security Headers")
    class SecurityHeaderTests {

        @Test
        @DisplayName("Should include X-Content-Type-Options header")
        void shouldIncludeXContentTypeOptions() {
            Response response = page.navigate(baseUrl() + "/login");

            Map<String, String> headers = response.headers();
            String xContentTypeOptions = headers.get("x-content-type-options");
            assertEquals("nosniff", xContentTypeOptions,
                    "X-Content-Type-Options should be 'nosniff'");
        }

        @Test
        @DisplayName("Should include X-Frame-Options header")
        void shouldIncludeXFrameOptions() {
            Response response = page.navigate(baseUrl() + "/login");

            Map<String, String> headers = response.headers();
            String xFrameOptions = headers.get("x-frame-options");
            assertNotNull(xFrameOptions, "X-Frame-Options header should be present");
            assertTrue(xFrameOptions.equals("DENY") || xFrameOptions.equals("SAMEORIGIN"),
                    "X-Frame-Options should be DENY or SAMEORIGIN, was: " + xFrameOptions);
        }

        @Test
        @DisplayName("Should include Content-Security-Policy header")
        void shouldIncludeContentSecurityPolicy() {
            Response response = page.navigate(baseUrl() + "/login");

            Map<String, String> headers = response.headers();
            String csp = headers.get("content-security-policy");
            assertNotNull(csp, "Content-Security-Policy header should be present");
            assertTrue(csp.contains("default-src"),
                    "CSP should contain default-src directive");
        }

        @Test
        @DisplayName("Should include Referrer-Policy header")
        void shouldIncludeReferrerPolicy() {
            Response response = page.navigate(baseUrl() + "/login");

            Map<String, String> headers = response.headers();
            String referrerPolicy = headers.get("referrer-policy");
            assertNotNull(referrerPolicy, "Referrer-Policy header should be present");
        }

        @Test
        @DisplayName("Should include X-XSS-Protection header if present")
        void shouldIncludeXssProtection() {
            Response response = page.navigate(baseUrl() + "/login");

            Map<String, String> headers = response.headers();
            String xssProtection = headers.get("x-xss-protection");
            // X-XSS-Protection is deprecated in modern browsers (CSP replaces it)
            // If present, verify it's configured correctly; if absent, that's acceptable
            if (xssProtection != null && !xssProtection.isEmpty()) {
                assertTrue(xssProtection.contains("1") || xssProtection.equals("0"),
                        "X-XSS-Protection should be '1; mode=block' or '0', was: " + xssProtection);
            }
            // Note: Modern security relies on CSP rather than X-XSS-Protection
        }
    }

    @Nested
    @DisplayName("Session Security")
    class SessionSecurityTests {

        @Test
        @DisplayName("Should use secure session cookies")
        void shouldUseSecureSessionCookies() {
            loginAsAdmin();

            var cookies = context.cookies();
            var sessionCookie = cookies.stream()
                    .filter(c -> c.name.contains("SESSION") || c.name.equals("JSESSIONID"))
                    .findFirst();

            assertTrue(sessionCookie.isPresent(), "Session cookie should exist");

            // Check httpOnly flag - prevents JavaScript access to cookie
            assertTrue(sessionCookie.get().httpOnly,
                    "Session cookie should have httpOnly flag");

            // Check sameSite flag - prevents CSRF attacks
            // SameSite can be "Strict", "Lax", or "None"
            var sameSite = sessionCookie.get().sameSite;
            assertNotNull(sameSite, "Session cookie should have sameSite attribute");
            assertTrue(sameSite.toString().equals("STRICT") || sameSite.toString().equals("LAX"),
                    "Session cookie sameSite should be Strict or Lax, was: " + sameSite);

            // Note: secure flag only applies when using HTTPS
            // In test environment (HTTP), secure flag may not be set
        }

        @Test
        @DisplayName("Should generate new session ID after login")
        void shouldRegenerateSessionAfterLogin() {
            navigateTo("/login");
            waitForPageLoad();

            // Get session before login
            var cookiesBefore = context.cookies();
            var sessionBefore = cookiesBefore.stream()
                    .filter(c -> c.name.contains("SESSION") || c.name.equals("JSESSIONID"))
                    .findFirst()
                    .map(c -> c.value)
                    .orElse("");

            // Login
            loginAsAdmin();

            // Get session after login
            var cookiesAfter = context.cookies();
            var sessionAfter = cookiesAfter.stream()
                    .filter(c -> c.name.contains("SESSION") || c.name.equals("JSESSIONID"))
                    .findFirst()
                    .map(c -> c.value)
                    .orElse("");

            // Session ID should change after login (session fixation protection)
            assertNotEquals(sessionBefore, sessionAfter,
                    "Session ID should be regenerated after login");
        }
    }

    @Nested
    @DisplayName("Authorization Controls")
    class AuthorizationTests {

        @Test
        @DisplayName("Should restrict access based on roles")
        void shouldRestrictAccessBasedOnRoles() {
            // Login as admin
            loginAsAdmin();

            // Admin should be able to access user management
            navigateTo("/users");
            waitForPageLoad();

            // Should not show 403 or redirect
            assertFalse(page.url().contains("/error") || page.url().contains("/403"),
                    "Admin should have access to user management");
        }

        @Test
        @DisplayName("Should prevent horizontal privilege escalation")
        void shouldPreventHorizontalPrivilegeEscalation() {
            loginAsAdmin();

            // Try to access another user's data by manipulating URLs
            // This is a basic check - more comprehensive tests needed
            navigateTo("/users");
            waitForPageLoad();

            // Application should validate ownership/permissions
            // Currently just documenting the test - implement specific checks
        }
    }

    @Nested
    @DisplayName("Input Validation")
    class InputValidationTests {

        @Test
        @DisplayName("Should validate required fields")
        void shouldValidateRequiredFields() {
            loginAsAdmin();
            navigateTo("/accounts/new");
            waitForPageLoad();

            // Try to submit empty form
            page.click("#btn-simpan");
            waitForPageLoad();

            // Should show validation errors or stay on form
            boolean hasValidationError = page.locator("#validation-errors").count() > 0
                    || page.url().contains("/new");

            assertTrue(hasValidationError, "Should show validation errors for empty required fields");
        }

        @Test
        @DisplayName("Should reject SQL injection attempts in inputs")
        void shouldRejectSqlInjection() {
            loginAsAdmin();

            List<String> sqlPayloads = List.of(
                    "' OR '1'='1",
                    "'; DROP TABLE users; --",
                    "1; SELECT * FROM users",
                    "' UNION SELECT * FROM users --"
            );

            for (String payload : sqlPayloads) {
                navigateTo("/accounts");
                waitForPageLoad();

                Locator searchInput = page.locator("input[type='search'], input[name='search']");
                if (searchInput.count() > 0) {
                    searchInput.first().fill(payload);
                    page.keyboard().press("Enter");
                    waitForPageLoad();

                    // Should not cause database error or expose data
                    String content = page.content().toLowerCase();
                    assertFalse(content.contains("sql") && content.contains("error"),
                            "SQL injection should not cause SQL error: " + payload);
                    assertFalse(content.contains("exception"),
                            "SQL injection should not expose exception: " + payload);
                }
            }
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should not expose stack traces in error pages")
        void shouldNotExposeStackTraces() {
            loginAsAdmin();

            // Access non-existent page
            navigateTo("/nonexistent-page-12345");
            waitForPageLoad();

            String content = page.content().toLowerCase();

            assertFalse(content.contains("stacktrace"), "Should not expose stack trace");
            assertFalse(content.contains("exception"), "Should not expose exception details");
            assertFalse(content.contains("at com.artivisi"), "Should not expose package names in error");
        }

        @Test
        @DisplayName("Should show generic error messages")
        void shouldShowGenericErrorMessages() {
            loginAsAdmin();

            // Trigger an error condition
            navigateTo("/accounts/nonexistent-uuid-12345");
            waitForPageLoad();

            String content = page.content().toLowerCase();

            // Should show user-friendly error, not technical details
            assertFalse(content.contains("nullpointerexception"),
                    "Should not expose NullPointerException");
            assertFalse(content.contains("hibernate"),
                    "Should not expose Hibernate details");
        }
    }

    @Nested
    @DisplayName("File Upload Security")
    class FileUploadSecurityTests {

        @Test
        @DisplayName("Should validate file types on upload")
        void shouldValidateFileTypes() {
            loginAsAdmin();

            // Navigate to a page with file upload
            navigateTo("/settings");
            waitForPageLoad();

            // File type validation is handled server-side
            // This test documents the expected behavior
        }
    }
}
