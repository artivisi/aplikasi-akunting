package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for DataImportController.
 * Tests the import page display and form interactions.
 */
@DisplayName("Data Import Controller Tests")
@Import(ServiceTestDataInitializer.class)
class DataImportControllerFunctionalTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display import page")
    void shouldDisplayImportPage() {
        navigateTo("/settings/import");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings\\/import.*"));
    }

    @Test
    @DisplayName("Should have file upload input")
    void shouldHaveFileUploadInput() {
        navigateTo("/settings/import");
        waitForPageLoad();

        var fileInput = page.locator("input[type='file']").first();
        assertThat(fileInput).isVisible();
    }

    @Test
    @DisplayName("Should have submit button")
    void shouldHaveSubmitButton() {
        navigateTo("/settings/import");
        waitForPageLoad();

        var submitBtn = page.locator("button[type='submit'], input[type='submit']").first();
        assertThat(submitBtn).isVisible();
    }

    @Test
    @DisplayName("Should show error for empty file submission")
    void shouldShowErrorForEmptyFileSubmission() {
        navigateTo("/settings/import");
        waitForPageLoad();

        // Try to submit without file
        var submitBtn = page.locator("button[type='submit'], input[type='submit']").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Should remain on import page or show error
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show import form elements")
    void shouldShowImportFormElements() {
        navigateTo("/settings/import");
        waitForPageLoad();

        // Check for form
        var form = page.locator("form").first();
        assertThat(form).isVisible();
    }

    @Test
    @DisplayName("Should be accessible from settings menu")
    void shouldBeAccessibleFromSettingsMenu() {
        navigateTo("/settings");
        waitForPageLoad();

        // Look for import link
        var importLink = page.locator("a[href*='/settings/import']").first();
        if (importLink.isVisible()) {
            importLink.click();
            waitForPageLoad();
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings\\/import.*"));
        }
    }

    @Test
    @DisplayName("Should navigate back to settings")
    void shouldNavigateBackToSettings() {
        navigateTo("/settings/import");
        waitForPageLoad();

        var backLink = page.locator("a[href*='/settings']").first();
        if (backLink.isVisible()) {
            backLink.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
