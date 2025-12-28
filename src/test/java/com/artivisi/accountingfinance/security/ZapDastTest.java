package com.artivisi.accountingfinance.security;

import org.junit.jupiter.api.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDate;

/**
 * Comprehensive OWASP ZAP DAST Security Tests
 *
 * Tests OWASP Top 10 vulnerabilities:
 * - SQL Injection (A03:2021)
 * - XSS (A03:2021)
 * - Broken Authentication (A07:2021)
 * - Security Misconfiguration (A05:2021)
 *
 * Run locally:
 *   ./mvnw test -Dtest=ZapDastTest -DexcludedGroups="" -Ddast.enabled=true
 *
 * Quick scan (passive only):
 *   ./mvnw test -Dtest=ZapDastTest -DexcludedGroups="" -Ddast.enabled=true -Ddast.quick=true
 */
@DisplayName("OWASP ZAP DAST Security Scan")
class ZapDastTest extends ZapDastTestBase {

    // OWASP Top 10 test payloads
    private static final String[] SQL_INJECTION_PAYLOADS = {
            "' OR '1'='1",
            "1; DROP TABLE users--",
            "' UNION SELECT * FROM users--",
            "admin'--"
    };

    private static final String[] XSS_PAYLOADS = {
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "javascript:alert('XSS')",
            "<svg onload=alert('XSS')>"
    };

    private static final String[] PATH_TRAVERSAL_PAYLOADS = {
            "../../../etc/passwd",
            "....//....//etc/passwd"
    };

    @Test
    @DisplayName("Should pass SQL injection security scan")
    void shouldPassSqlInjectionScan() throws Exception {
        skipIfNotEnabled();

        log.info("=== Starting SQL Injection Security Scan ===");

        authenticatedClient = performLogin("admin", "admin");

        // First access pages to populate ZAP's scan tree
        accessPage(authenticatedClient, "/accounts");
        accessPage(authenticatedClient, "/accounts/new");

        log.info("Testing SQL injection payloads in search fields...");
        for (String payload : SQL_INJECTION_PAYLOADS) {
            accessPageWithParams("/accounts", "search=" + urlEncode(payload));
            testLoginWithPayload(payload);
        }

        // Spider to populate scan tree
        spiderTarget(targetUrl + "/accounts");

        waitForPassiveScan();

        if (!QUICK_SCAN) {
            runActiveScan(targetUrl + "/accounts");
        }

        ScanResults results = analyzeAlerts("SQLInjection");
        generateHtmlReport("zap-sqli-report.html", targetUrl);

        assertSecurityThresholds(results, "SQLInjection");
    }

    @Test
    @DisplayName("Should pass XSS security scan")
    void shouldPassXssScan() throws Exception {
        skipIfNotEnabled();

        log.info("=== Starting XSS Security Scan ===");

        authenticatedClient = performLogin("admin", "admin");

        log.info("Testing XSS payloads...");
        for (String payload : XSS_PAYLOADS) {
            accessPageWithParams("/transactions", "search=" + urlEncode(payload));
            accessPageWithParams("/accounts", "search=" + urlEncode(payload));
            accessPageWithParams("/templates", "search=" + urlEncode(payload));
            submitAccountForm("XSS" + System.currentTimeMillis(), payload, "ASSET");
        }

        waitForPassiveScan();

        if (!QUICK_SCAN) {
            runActiveScan(targetUrl);
        }

        ScanResults results = analyzeAlerts("XSS");
        generateHtmlReport("zap-xss-report.html", targetUrl);

        assertSecurityThresholds(results, "XSS");
    }

    @Test
    @DisplayName("Should pass authentication security scan")
    void shouldPassAuthenticationScan() throws Exception {
        skipIfNotEnabled();

        log.info("=== Starting Authentication Security Scan ===");

        HttpClient unauthClient = createProxiedClient();

        // Test login page
        accessPage(unauthClient, "/login");

        // Test login with SQL injection payloads
        for (String payload : SQL_INJECTION_PAYLOADS) {
            testLoginWithPayload(payload);
        }

        // Test session handling
        authenticatedClient = performLogin("admin", "admin");
        accessPage(authenticatedClient, "/dashboard");
        accessPage(authenticatedClient, "/accounts");

        // Test logout
        accessPage(authenticatedClient, "/logout");

        // Try accessing protected pages after logout
        accessPage(unauthClient, "/dashboard");

        waitForPassiveScan();

        if (!QUICK_SCAN) {
            runActiveScan(targetUrl + "/login");
        }

        ScanResults results = analyzeAlerts("Authentication");
        generateHtmlReport("zap-auth-report.html", targetUrl);

        assertSecurityThresholds(results, "Authentication");
    }

    @Test
    @DisplayName("Should pass form input security scan")
    void shouldPassFormInputScan() throws Exception {
        skipIfNotEnabled();

        log.info("=== Starting Form Input Security Scan ===");

        authenticatedClient = performLogin("admin", "admin");

        // Test search with various payloads
        log.info("Testing search functionality...");
        String[] searchTerms = {"test", "' OR 1=1--", "<script>alert(1)</script>", "{{7*7}}"};
        for (String term : searchTerms) {
            accessPageWithParams("/accounts", "search=" + urlEncode(term));
            accessPageWithParams("/templates", "search=" + urlEncode(term));
        }

        // Test transaction filters
        log.info("Testing filters...");
        LocalDate today = LocalDate.now();
        accessPageWithParams("/transactions", "startDate=" + today.minusMonths(1) + "&endDate=" + today);
        accessPageWithParams("/transactions", "status=POSTED");
        accessPageWithParams("/transactions", "category=REVENUE");

        // Test account form
        log.info("Testing form submissions...");
        submitAccountForm("TEST001", "Test Account", "ASSET");
        submitAccountForm("TESTXSS", "<script>alert('xss')</script>", "ASSET");

        // Test parameterized URLs
        log.info("Testing parameterized URLs...");
        String fakeUuid = "00000000-0000-0000-0000-000000000001";
        accessPage(authenticatedClient, "/accounts/" + fakeUuid);
        accessPage(authenticatedClient, "/templates/" + fakeUuid);

        // Test path traversal
        for (String payload : PATH_TRAVERSAL_PAYLOADS) {
            accessPage(authenticatedClient, "/accounts/" + urlEncode(payload));
        }

        spiderTarget(targetUrl + "/dashboard");
        waitForPassiveScan();

        if (!QUICK_SCAN) {
            runActiveScan(targetUrl);
        }

        ScanResults results = analyzeAlerts("FormInput");
        generateHtmlReport("zap-form-input-report.html", targetUrl);

        assertSecurityThresholds(results, "FormInput");
    }

    // ========== Helper Methods ==========

    private void testLoginWithPayload(String payload) {
        try {
            HttpClient client = createProxiedClient();
            HttpResponse<String> loginPage = getPage(client, "/login");
            String csrf = extractCsrfToken(loginPage.body());

            String formData = "username=" + urlEncode(payload) + "&password=test&_csrf=" + csrf;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl + "/login"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.debug("Login test error (expected): {}", e.getMessage());
        }
    }

    private void submitAccountForm(String code, String name, String type) {
        try {
            HttpResponse<String> formPage = getPage(authenticatedClient, "/accounts/new");
            String csrf = extractCsrfToken(formPage.body());

            String formData = String.format(
                    "accountCode=%s&accountName=%s&accountType=%s&_csrf=%s",
                    urlEncode(code), urlEncode(name), type, csrf
            );

            postForm("/accounts/new", formData);
        } catch (Exception e) {
            log.debug("Form submission error: {}", e.getMessage());
        }
    }
}
