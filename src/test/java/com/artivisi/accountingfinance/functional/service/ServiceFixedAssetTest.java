package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Service Industry Fixed Asset Tests
 * Tests fixed asset management, categories, and depreciation.
 */
@DisplayName("Service Industry - Fixed Assets")
@Import(ServiceTestDataInitializer.class)
public class ServiceFixedAssetTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display asset categories list")
    void shouldDisplayAssetCategoriesList() {
        loginAsAdmin();
        navigateTo("/assets/categories");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title")).isVisible();

        // Take screenshot for user manual
        takeManualScreenshot("asset-categories-list");
    }

    @Test
    @DisplayName("Should display fixed assets list")
    void shouldDisplayFixedAssetsList() {
        loginAsAdmin();
        navigateTo("/assets");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title")).containsText("Aset");

        // Take screenshot for user manual
        takeManualScreenshot("assets-list");
    }

    @Test
    @DisplayName("Should display asset form")
    void shouldDisplayAssetForm() {
        loginAsAdmin();
        navigateTo("/assets/new");
        waitForPageLoad();

        // Verify form loads
        assertThat(page.locator("#page-title")).containsText("Aset");

        // Take screenshot for user manual
        takeManualScreenshot("assets-form");
    }

    @Test
    @DisplayName("Should display depreciation schedule")
    void shouldDisplayDepreciationSchedule() {
        loginAsAdmin();
        navigateTo("/assets/depreciation");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title")).isVisible();

        // Take screenshot for user manual
        takeManualScreenshot("assets-depreciation");
    }
}
