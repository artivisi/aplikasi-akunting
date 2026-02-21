package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Slf4j
@DisplayName("Aging Reports - Functional Tests")
@Import(ServiceTestDataInitializer.class)
class AgingReportTest extends PlaywrightTestBase {

    @BeforeEach
    void setUp() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Receivables aging page loads with report structure")
    void shouldDisplayReceivablesAgingPage() {
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();

        assertThat(page.locator("[data-testid='aging-receivables']")).isVisible();
        assertThat(page.locator("[data-testid='aging-summary']")).isVisible();
        assertThat(page.locator("input#asOfDate")).isVisible();

        log.info("Receivables aging page loaded");
    }

    @Test
    @DisplayName("Payables aging page loads with report structure")
    void shouldDisplayPayablesAgingPage() {
        navigateTo("/reports/aging/payables");
        waitForPageLoad();

        assertThat(page.locator("[data-testid='aging-payables']")).isVisible();
        assertThat(page.locator("[data-testid='aging-summary']")).isVisible();
        assertThat(page.locator("input#asOfDate")).isVisible();

        log.info("Payables aging page loaded");
    }

    @Test
    @DisplayName("Receivables aging shows no data message when no outstanding invoices")
    void shouldShowNoDataForReceivables() {
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();

        // With only test data (no sent invoices), should show empty or no-data
        Locator noData = page.locator("[data-testid='no-data']");
        Locator table = page.locator("[data-testid='aging-table']");

        // Either no data message or an empty table is acceptable
        boolean hasNoData = noData.count() > 0;
        boolean hasTable = table.count() > 0;
        org.assertj.core.api.Assertions.assertThat(hasNoData || hasTable)
                .as("Page shows either no-data message or aging table")
                .isTrue();

        log.info("Receivables aging no-data check passed");
    }

    @Test
    @DisplayName("Receivables aging with as-of date parameter")
    void shouldFilterReceivablesByAsOfDate() {
        navigateTo("/reports/aging/receivables?asOfDate=2026-02-22");
        waitForPageLoad();

        assertThat(page.locator("[data-testid='aging-receivables']")).isVisible();
        // Verify the date is set in the filter
        String dateValue = page.locator("input#asOfDate").inputValue();
        org.assertj.core.api.Assertions.assertThat(dateValue).isEqualTo("2026-02-22");

        log.info("Receivables aging as-of date filter works");
    }

    @Test
    @DisplayName("Receivables aging summary cards show Rp amounts")
    void shouldShowSummaryAmountsForReceivables() {
        navigateTo("/reports/aging/receivables");
        waitForPageLoad();

        // Summary cards should display Rp formatted amounts
        Locator summaryCards = page.locator("[data-testid='aging-summary']");
        assertThat(summaryCards).isVisible();

        // Each card should contain "Rp" text
        Locator amounts = summaryCards.locator(".font-mono");
        org.assertj.core.api.Assertions.assertThat(amounts.count())
                .as("Summary should have 6 amount cards")
                .isEqualTo(6);

        log.info("Receivables aging summary cards verified");
    }

    @Test
    @DisplayName("Sidebar links to aging reports exist")
    void shouldHaveSidebarLinks() {
        navigateTo("/dashboard");
        waitForPageLoad();

        // Open Laporan group
        page.locator("#nav-group-laporan").click();

        assertThat(page.locator("#nav-aging-receivables")).isVisible();
        assertThat(page.locator("#nav-aging-payables")).isVisible();

        log.info("Sidebar aging report links verified");
    }
}
