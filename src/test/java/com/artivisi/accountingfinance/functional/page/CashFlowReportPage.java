package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CashFlowReportPage {

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
    private static final String BTN_EXPORT_PDF = "#btn-export-pdf";
    private static final String BTN_EXPORT_EXCEL = "#btn-export-excel";
    private static final String OPERATING_TOTAL = "#operating-total";
    private static final String INVESTING_TOTAL = "#investing-total";
    private static final String FINANCING_TOTAL = "#financing-total";
    private static final String NET_CASH_CHANGE = "#net-cash-change";
    private static final String BEGINNING_BALANCE = "#beginning-balance";
    private static final String ENDING_BALANCE = "#ending-balance";

    public CashFlowReportPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public CashFlowReportPage navigate() {
        page.navigate(baseUrl + "/reports/cash-flow",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public CashFlowReportPage navigateWithDates(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/cash-flow?startDate=" + startDate + "&endDate=" + endDate,
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

    public void assertExportPdfButtonVisible() {
        assertThat(page.locator(BTN_EXPORT_PDF)).isVisible();
    }

    public void assertExportExcelButtonVisible() {
        assertThat(page.locator(BTN_EXPORT_EXCEL)).isVisible();
    }

    public void assertReportPeriodContains(String expected) {
        assertThat(page.locator(REPORT_PERIOD)).containsText(expected);
    }

    // Assertions - Cash Flow Values
    public void assertOperatingTotalVisible() {
        assertThat(page.locator(OPERATING_TOTAL)).isVisible();
    }

    public String getOperatingTotalText() {
        return page.locator(OPERATING_TOTAL).textContent().trim();
    }

    public void assertInvestingTotalVisible() {
        assertThat(page.locator(INVESTING_TOTAL)).isVisible();
    }

    public String getInvestingTotalText() {
        return page.locator(INVESTING_TOTAL).textContent().trim();
    }

    public void assertFinancingTotalVisible() {
        assertThat(page.locator(FINANCING_TOTAL)).isVisible();
    }

    public String getFinancingTotalText() {
        return page.locator(FINANCING_TOTAL).textContent().trim();
    }

    public void assertNetCashChangeVisible() {
        assertThat(page.locator(NET_CASH_CHANGE)).isVisible();
    }

    public String getNetCashChangeText() {
        return page.locator(NET_CASH_CHANGE).textContent().trim();
    }

    public void assertBeginningBalanceVisible() {
        assertThat(page.locator(BEGINNING_BALANCE)).isVisible();
    }

    public String getBeginningBalanceText() {
        return page.locator(BEGINNING_BALANCE).textContent().trim();
    }

    public void assertEndingBalanceVisible() {
        assertThat(page.locator(ENDING_BALANCE)).isVisible();
    }

    public String getEndingBalanceText() {
        return page.locator(ENDING_BALANCE).textContent().trim();
    }
}
