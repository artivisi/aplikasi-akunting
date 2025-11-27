package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TaxSummaryPage {

    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_PERIOD = "#report-period";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String TAX_TABLE = "table";
    private static final String TAX_ROW = "tbody tr";

    public TaxSummaryPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TaxSummaryPage navigate() {
        page.navigate(baseUrl + "/reports/tax-summary",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public TaxSummaryPage navigateWithDates(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/tax-summary?startDate=" + startDate + "&endDate=" + endDate,
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    // Actions
    public void setStartDate(String date) {
        page.fill(START_DATE, date);
    }

    public void setEndDate(String date) {
        page.fill(END_DATE, date);
    }

    public void clickGenerate() {
        page.click(BTN_GENERATE);
        page.waitForLoadState();
    }

    // Assertions - Page Elements
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expected);
    }

    public void assertReportTitleVisible() {
        assertThat(page.locator(REPORT_TITLE)).isVisible();
    }

    public void assertReportTitleText(String expected) {
        assertThat(page.locator(REPORT_TITLE)).hasText(expected);
    }

    public void assertStartDateVisible() {
        assertThat(page.locator(START_DATE)).isVisible();
    }

    public void assertEndDateVisible() {
        assertThat(page.locator(END_DATE)).isVisible();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }

    public void assertReportPeriodContains(String expected) {
        assertThat(page.locator(REPORT_PERIOD)).containsText(expected);
    }

    // Assertions - Tax Table
    public void assertTaxTableVisible() {
        assertThat(page.locator(TAX_TABLE)).isVisible();
    }

    public int getTaxRowCount() {
        return page.locator(TAX_ROW).count();
    }

    public void assertTaxAccountExists(String accountCode) {
        assertThat(page.locator("td:has-text('" + accountCode + "')")).isVisible();
    }

    public void assertTaxAccountLabelExists(String label) {
        assertThat(page.locator("td:has-text('" + label + "')")).isVisible();
    }

    // Navigation links
    public void clickPPNSummaryLink() {
        page.locator("a:has-text('Ringkasan PPN')").click();
        page.waitForLoadState();
    }

    public void clickPPh23Link() {
        page.locator("a:has-text('Pemotongan PPh 23')").click();
        page.waitForLoadState();
    }
}
