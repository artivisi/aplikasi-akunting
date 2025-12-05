package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Security Audit Log Viewer")
class AuditLogViewerTest {

    @Nested
    @DisplayName("Audit Log Access and Display")
    class AuditLogAccessTests extends PlaywrightTestBase {

        @Test
        @DisplayName("Should display audit log page")
        void shouldDisplayAuditLogPage() {
            loginAsAdmin();
            page.navigate(baseUrl() + "/settings/audit-logs");

            assertThat(page.locator("#page-title")).containsText("Security Audit Log");
            assertThat(page.locator("#audit-log-table")).isVisible();
        }

        @Test
        @DisplayName("Should display audit log entries")
        void shouldDisplayAuditLogEntries() {
            loginAsAdmin();
            // Login generates audit log entries
            page.navigate(baseUrl() + "/settings/audit-logs");

            // Should have at least one entry (from login)
            assertThat(page.locator("#audit-log-table")).isVisible();

            // Check for login event (from our login)
            assertThat(page.locator("#audit-log-table")).containsText("LOGIN SUCCESS");
        }

        @Test
        @DisplayName("Should filter by event type")
        void shouldFilterByEventType() {
            loginAsAdmin();
            page.navigate(baseUrl() + "/settings/audit-logs");

            // Select LOGIN_SUCCESS event type
            page.locator("#event-type-filter").selectOption("LOGIN_SUCCESS");
            page.locator("#btn-apply").click();

            // Wait for HTMX to update
            page.waitForLoadState();

            // All visible entries should be LOGIN_SUCCESS
            assertThat(page.locator("#audit-log-table")).containsText("LOGIN SUCCESS");
        }

        @Test
        @DisplayName("Should filter by username")
        void shouldFilterByUsername() {
            loginAsAdmin();
            page.navigate(baseUrl() + "/settings/audit-logs");

            // Filter by admin username
            page.locator("#username-filter").fill("admin");
            page.locator("#btn-apply").click();

            // Wait for HTMX to update
            page.waitForLoadState();

            // Results should contain admin
            assertThat(page.locator("#audit-log-table")).containsText("admin");
        }

        @Test
        @DisplayName("Should reset filters")
        void shouldResetFilters() {
            loginAsAdmin();
            page.navigate(baseUrl() + "/settings/audit-logs?eventType=LOGIN_SUCCESS&username=admin");

            // Click reset button
            page.locator("#btn-reset").click();

            // Wait for page load
            page.waitForLoadState();

            // Filters should be cleared
            assertThat(page.locator("#event-type-filter")).hasValue("");
            assertThat(page.locator("#username-filter")).hasValue("");
        }

        @Test
        @DisplayName("Should display event type badges with correct colors")
        void shouldDisplayEventTypeBadges() {
            loginAsAdmin();
            page.navigate(baseUrl() + "/settings/audit-logs");

            // Check that LOGIN_SUCCESS badge exists (green color)
            assertThat(page.locator("#audit-log-table .bg-green-100").first()).isVisible();
        }
    }

    @Nested
    @DisplayName("Audit Log Pagination")
    class AuditLogPaginationTests extends PlaywrightTestBase {

        @Test
        @DisplayName("Should show audit log table even with few entries")
        void shouldShowAuditLogTable() {
            loginAsAdmin();
            page.navigate(baseUrl() + "/settings/audit-logs");

            // Check audit log table is displayed (pagination only shows if > 1 page)
            assertThat(page.locator("#audit-log-table")).isVisible();
        }
    }

    @Nested
    @DisplayName("Audit Log Authorization")
    class AuditLogAuthorizationTests extends PlaywrightTestBase {

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() {
            // Navigate without login
            page.navigate(baseUrl() + "/settings/audit-logs");

            // Should redirect to login
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/login.*"));
        }
    }
}
