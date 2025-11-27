package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PPNSummaryPage {

    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_PERIOD = "#report-period";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_PRINT = "#btn-print";
    private static final String PPN_KELUARAN = "#ppn-keluaran";
    private static final String PPN_MASUKAN = "#ppn-masukan";
    private static final String NET_PPN = "#net-ppn";
    private static final String PPN_STATUS = "#ppn-status";
    private static final String PPN_MESSAGE = "#ppn-message";

    public PPNSummaryPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public PPNSummaryPage navigate() {
        page.navigate(baseUrl + "/reports/ppn-summary",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public PPNSummaryPage navigateWithDates(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/ppn-summary?startDate=" + startDate + "&endDate=" + endDate,
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

    public void assertPrintButtonVisible() {
        assertThat(page.locator(BTN_PRINT)).isVisible();
    }

    public void assertReportPeriodContains(String expected) {
        assertThat(page.locator(REPORT_PERIOD)).containsText(expected);
    }

    // Assertions - PPN Values
    public void assertPPNKeluaranVisible() {
        assertThat(page.locator(PPN_KELUARAN)).isVisible();
    }

    public String getPPNKeluaranText() {
        return page.locator(PPN_KELUARAN).textContent().trim();
    }

    public void assertPPNMasukanVisible() {
        assertThat(page.locator(PPN_MASUKAN)).isVisible();
    }

    public String getPPNMasukanText() {
        return page.locator(PPN_MASUKAN).textContent().trim();
    }

    public void assertNetPPNVisible() {
        assertThat(page.locator(NET_PPN)).isVisible();
    }

    public String getNetPPNText() {
        return page.locator(NET_PPN).textContent().trim();
    }

    // Assertions - Status
    public void assertPPNStatusVisible() {
        assertThat(page.locator(PPN_STATUS)).isVisible();
    }

    public void assertPPNMessageContains(String expected) {
        assertThat(page.locator(PPN_MESSAGE)).containsText(expected);
    }
}
