package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Service Industry Security Settings Tests
 * Tests security and compliance features for user manual screenshots.
 */
@DisplayName("Service Industry - Security Settings")
@Import(ServiceTestDataInitializer.class)
public class ServiceSecuritySettingsTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display audit logs page")
    void shouldDisplayAuditLogs() {
        loginAsAdmin();
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("h1").first()).isVisible();

        // Take screenshot for user manual (needed for 11-keamanan-kepatuhan.md)
        takeManualScreenshot("settings-audit-logs");
    }

    @Test
    @DisplayName("Should display data subjects page (GDPR/UU PDP)")
    void shouldDisplayDataSubjects() {
        loginAsAdmin();
        navigateTo("/settings/data-subjects");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("h1").first()).isVisible();

        // Take screenshot for user manual (needed for 11-keamanan-kepatuhan.md)
        takeManualScreenshot("settings-data-subjects");
    }

    @Test
    @DisplayName("Should display privacy settings page")
    void shouldDisplayPrivacySettings() {
        loginAsAdmin();
        navigateTo("/settings/privacy");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("h1").first()).isVisible();

        // Take screenshot for user manual (needed for 11-keamanan-kepatuhan.md)
        takeManualScreenshot("settings-privacy");
    }
}
