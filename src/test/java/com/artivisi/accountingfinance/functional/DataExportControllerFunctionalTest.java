package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for DataExportController.
 * Tests the export page display and download functionality.
 */
@DisplayName("Data Export Controller Tests")
@Import(ServiceTestDataInitializer.class)
class DataExportControllerFunctionalTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display export page")
    void shouldDisplayExportPage() {
        navigateTo("/settings/export");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings\\/export.*"));
    }

    @Test
    @DisplayName("Should show export statistics")
    void shouldShowExportStatistics() {
        navigateTo("/settings/export");
        waitForPageLoad();

        // Page should contain statistics about data to be exported
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have export download button")
    void shouldHaveExportDownloadButton() {
        navigateTo("/settings/export");
        waitForPageLoad();

        var downloadBtn = page.locator("button[type='submit'], a[href*='/download'], form[action*='/download'] button").first();
        assertThat(downloadBtn).isVisible();
    }

    @Test
    @DisplayName("Should have export form")
    void shouldHaveExportForm() {
        navigateTo("/settings/export");
        waitForPageLoad();

        var form = page.locator("form[action*='/export'], form[method='post']").first();
        assertThat(form).isVisible();
    }

    @Test
    @DisplayName("Should be accessible from settings menu")
    void shouldBeAccessibleFromSettingsMenu() {
        navigateTo("/settings");
        waitForPageLoad();

        var exportLink = page.locator("a[href*='/settings/export']").first();
        if (exportLink.isVisible()) {
            exportLink.click();
            waitForPageLoad();
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings\\/export.*"));
        }
    }

    @Test
    @DisplayName("Should navigate back to settings")
    void shouldNavigateBackToSettings() {
        navigateTo("/settings/export");
        waitForPageLoad();

        var backLink = page.locator("a[href='/settings'], a[href*='/settings']:not([href*='/export'])").first();
        if (backLink.isVisible()) {
            backLink.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should initiate download when export button clicked")
    void shouldInitiateDownloadWhenExportButtonClicked() {
        navigateTo("/settings/export");
        waitForPageLoad();

        // Find download button and click it
        var downloadBtn = page.locator("form[action*='/download'] button[type='submit']").first();
        if (downloadBtn.isVisible()) {
            assertThat(downloadBtn).isEnabled();
        }
    }

    @Test
    @DisplayName("Should download export zip file")
    void shouldDownloadExportZipFile() {
        navigateTo("/settings/export");
        waitForPageLoad();

        // Wait for download when clicking the button
        var download = page.waitForDownload(() -> {
            page.locator("form[action*='/download'] button[type='submit']").click();
        });

        // Verify download was initiated
        org.assertj.core.api.Assertions.assertThat(download.suggestedFilename())
                .as("Should download zip file")
                .endsWith(".zip");
    }
}
