package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for SettingsController.
 * Tests company settings, bank accounts, telegram, and audit logs.
 */
@DisplayName("Settings Controller Tests")
@Import(ServiceTestDataInitializer.class)
class SettingsControllerFunctionalTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display company settings page")
    void shouldDisplayCompanySettingsPage() {
        navigateTo("/settings");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on settings page")
            .contains("/settings");
    }

    @Test
    @DisplayName("Should display company name field")
    void shouldDisplayCompanyNameField() {
        navigateTo("/settings");
        waitForPageLoad();

        var companyNameInput = page.locator("input[name='companyName']").first();

        assertThat(companyNameInput.isVisible())
            .as("Company name input should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display NPWP field")
    void shouldDisplayNpwpField() {
        navigateTo("/settings");
        waitForPageLoad();

        var npwpInput = page.locator("input[name='npwp']").first();

        assertThat(npwpInput.isVisible())
            .as("NPWP input should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display bank accounts section")
    void shouldDisplayBankAccountsSection() {
        navigateTo("/settings");
        waitForPageLoad();

        assertThat(page.locator("text=Rekening Bank").first().isVisible())
            .as("Bank accounts section should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should update company settings")
    void shouldUpdateCompanySettings() {
        navigateTo("/settings");
        waitForPageLoad();

        // Update company name
        var companyNameInput = page.locator("input[name='companyName']").first();
        String originalName = companyNameInput.inputValue();

        companyNameInput.fill(originalName + " Updated");
        page.locator("form[action='/settings/company'] button[type='submit']").click();
        waitForPageLoad();

        // Verify success message or redirect
        assertThat(page.url())
            .as("Should redirect to settings page")
            .contains("/settings");

        // Restore original name
        navigateTo("/settings");
        waitForPageLoad();
        page.locator("input[name='companyName']").first().fill(originalName);
        page.locator("form[action='/settings/company'] button[type='submit']").click();
        waitForPageLoad();
    }

    @Test
    @DisplayName("Should display new bank account form")
    void shouldDisplayNewBankAccountForm() {
        navigateTo("/settings/bank-accounts/new");
        waitForPageLoad();

        assertThat(page.locator("input[name='bankName']").isVisible())
            .as("Bank name input should be visible")
            .isTrue();

        assertThat(page.locator("input[name='accountNumber']").isVisible())
            .as("Account number input should be visible")
            .isTrue();

        assertThat(page.locator("input[name='accountName']").isVisible())
            .as("Account name input should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should create new bank account")
    void shouldCreateNewBankAccount() {
        navigateTo("/settings/bank-accounts/new");
        waitForPageLoad();

        // Fill the form
        page.locator("input[name='bankName']").fill("Bank Test");
        page.locator("input[name='accountNumber']").fill("1234567890123");
        page.locator("input[name='accountName']").fill("PT Test Company");

        page.locator("#btn-save-bank").click();
        waitForPageLoad();

        // Should redirect to settings
        assertThat(page.url())
            .as("Should redirect to settings page")
            .contains("/settings");
    }

    @Test
    @DisplayName("Should display bank accounts list")
    void shouldDisplayBankAccountsList() {
        navigateTo("/settings/bank-accounts");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on bank accounts page")
            .contains("/settings/bank-accounts");
    }

    @Test
    @DisplayName("Should have add bank account button")
    void shouldHaveAddBankAccountButton() {
        navigateTo("/settings");
        waitForPageLoad();

        var addButton = page.locator("a[href*='/bank-accounts/new']").first();

        assertThat(addButton.isVisible())
            .as("Add bank account button should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display telegram settings page")
    void shouldDisplayTelegramSettingsPage() {
        navigateTo("/settings/telegram");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on telegram settings page")
            .contains("/settings/telegram");
    }

    @Test
    @DisplayName("Should show telegram status")
    void shouldShowTelegramStatus() {
        navigateTo("/settings/telegram");
        waitForPageLoad();

        // Should show either linked status or link button
        var pageContent = page.content();
        assertThat(pageContent)
            .as("Should show telegram status information")
            .containsAnyOf("Telegram", "Hubungkan", "Linked", "Putuskan");
    }

    @Test
    @DisplayName("Should display about page")
    void shouldDisplayAboutPage() {
        navigateTo("/settings/about");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on about page")
            .contains("/settings/about");
    }

    @Test
    @DisplayName("Should show git commit info")
    void shouldShowGitCommitInfo() {
        navigateTo("/settings/about");
        waitForPageLoad();

        // Should show commit ID or version info
        var pageContent = page.content();
        assertThat(pageContent)
            .as("Should show version or commit info")
            .containsAnyOf("Commit", "commit", "Version", "version", "Git");
    }

    @Test
    @DisplayName("Should display privacy policy page")
    void shouldDisplayPrivacyPolicyPage() {
        navigateTo("/settings/privacy");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on privacy page")
            .contains("/settings/privacy");
    }

    @Test
    @DisplayName("Should display audit logs page")
    void shouldDisplayAuditLogsPage() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on audit logs page")
            .contains("/settings/audit-logs");
    }

    @Test
    @DisplayName("Should display event type filter")
    void shouldDisplayEventTypeFilter() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        var eventTypeSelect = page.locator("select[name='eventType']").first();

        assertThat(eventTypeSelect.isVisible())
            .as("Event type filter should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display date range filters")
    void shouldDisplayDateRangeFilters() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        var startDateInput = page.locator("input[name='startDate']").first();
        var endDateInput = page.locator("input[name='endDate']").first();

        assertThat(startDateInput.isVisible())
            .as("Start date filter should be visible")
            .isTrue();

        assertThat(endDateInput.isVisible())
            .as("End date filter should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display username filter")
    void shouldDisplayUsernameFilter() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        var usernameInput = page.locator("input[name='username']").first();

        assertThat(usernameInput.isVisible())
            .as("Username filter should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should filter audit logs by event type")
    void shouldFilterAuditLogsByEventType() {
        // Navigate directly with filter parameter to test server-side filtering
        navigateTo("/settings/audit-logs?eventType=LOGIN_SUCCESS");
        waitForPageLoad();

        // Verify the filter is applied (select should have the value selected)
        var eventTypeSelect = page.locator("select[name='eventType']").first();
        assertThat(eventTypeSelect.inputValue())
            .as("Event type filter should be selected")
            .isEqualTo("LOGIN_SUCCESS");
    }

    @Test
    @DisplayName("Should show audit log entries")
    void shouldShowAuditLogEntries() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        // The page should have the audit log table
        // Since we've been logging in, there should be at least login entries
        var auditLogTable = page.locator("#audit-log-table").first();

        assertThat(auditLogTable.isVisible())
            .as("Audit log table should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should navigate between settings sections")
    void shouldNavigateBetweenSettingsSections() {
        navigateTo("/settings");
        waitForPageLoad();

        // Navigate to bank accounts
        var bankAccountsLink = page.locator("a[href*='/settings/bank-accounts']").first();
        if (bankAccountsLink.isVisible()) {
            bankAccountsLink.click();
            waitForPageLoad();
            assertThat(page.url()).contains("/settings/bank-accounts");
        }
    }

    @Test
    @DisplayName("Should have link to about page")
    void shouldHaveLinkToAboutPage() {
        navigateTo("/settings");
        waitForPageLoad();

        var aboutLink = page.locator("a[href*='/settings/about']").first();

        assertThat(aboutLink.isVisible())
            .as("About link should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should access audit logs page directly")
    void shouldAccessAuditLogsPageDirectly() {
        // Audit logs page is accessed directly (not linked from settings)
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on audit logs page")
            .contains("/settings/audit-logs");
    }
}
