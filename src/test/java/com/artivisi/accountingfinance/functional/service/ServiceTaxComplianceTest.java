package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.functional.page.TaxCalendarPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Service Industry Tax Compliance Tests
 * Tests PKP tax compliance: PPN, PPh 21, PPh 23, tax calendar.
 * Uses Page Object Pattern for maintainability.
 */
@DisplayName("Service Industry - Tax Compliance")
@Import(ServiceTestDataInitializer.class)
public class ServiceTaxComplianceTest extends PlaywrightTestBase {

    // Page Objects
    private TaxCalendarPage taxCalendarPage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        taxCalendarPage = new TaxCalendarPage(page, baseUrl);
    }

    @Test
    @DisplayName("Should display tax calendar")
    void shouldDisplayTaxCalendar() {
        loginAsAdmin();
        initPageObjects();

        taxCalendarPage.navigate()
            .verifyPageTitle();

        // Take screenshot for user manual
        takeManualScreenshot("tax-calendar");
    }

    @Test
    @DisplayName("Should display PPN summary report")
    void shouldDisplayPpnSummaryReport() {
        loginAsAdmin();
        navigateTo("/reports/ppn-summary");
        waitForPageLoad();

        // Verify PPN report page loads using ID
        assertThat(page.locator("#page-title")).containsText("PPN");

        // Take screenshot for user manual
        takeManualScreenshot("reports-ppn-summary");
    }

    @Test
    @DisplayName("Should display PPh 23 withholding report")
    void shouldDisplayPph23WithholdingReport() {
        loginAsAdmin();
        navigateTo("/reports/pph23-withholding");
        waitForPageLoad();

        // Verify PPh 23 report page loads using ID
        assertThat(page.locator("#page-title")).containsText("PPh 23");
    }

    @Test
    @DisplayName("Should display tax export page")
    void shouldDisplayTaxExportPage() {
        loginAsAdmin();
        navigateTo("/reports/tax-export");
        waitForPageLoad();

        // Verify tax export page loads using ID
        assertThat(page.locator("#page-title")).containsText("Export");
    }

    @Test
    @DisplayName("Should display tax summary report")
    void shouldDisplayTaxSummaryReport() {
        loginAsAdmin();
        navigateTo("/reports/tax-summary");
        waitForPageLoad();

        // Verify tax summary page loads using ID
        assertThat(page.locator("#page-title")).containsText("Pajak");

        // Take screenshot for user manual
        takeManualScreenshot("reports-tax-summary");
    }

    @Test
    @DisplayName("Should display tax calendar yearly view")
    void shouldDisplayTaxCalendarYearly() {
        loginAsAdmin();
        navigateTo("/tax-calendar/yearly");
        waitForPageLoad();

        // Verify yearly view loads (if exists)
        assertThat(page.locator("#page-title")).isVisible();

        // Take screenshot for user manual
        takeManualScreenshot("tax-calendar-yearly");
    }
}
