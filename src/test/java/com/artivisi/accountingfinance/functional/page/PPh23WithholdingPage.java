package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PPh23WithholdingPage {

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
    private static final String TOTAL_WITHHELD = "#total-withheld";
    private static final String TOTAL_DEPOSITED = "#total-deposited";
    private static final String BALANCE = "#balance";
    private static final String PPH23_STATUS = "#pph23-status";
    private static final String PPH23_MESSAGE = "#pph23-message";

    public PPh23WithholdingPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public PPh23WithholdingPage navigate() {
        page.navigate(baseUrl + "/reports/pph23-withholding",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public PPh23WithholdingPage navigateWithDates(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/pph23-withholding?startDate=" + startDate + "&endDate=" + endDate,
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

    // Assertions - PPh 23 Values
    public void assertTotalWithheldVisible() {
        assertThat(page.locator(TOTAL_WITHHELD)).isVisible();
    }

    public String getTotalWithheldText() {
        return page.locator(TOTAL_WITHHELD).textContent().trim();
    }

    public void assertTotalDepositedVisible() {
        assertThat(page.locator(TOTAL_DEPOSITED)).isVisible();
    }

    public String getTotalDepositedText() {
        return page.locator(TOTAL_DEPOSITED).textContent().trim();
    }

    public void assertBalanceVisible() {
        assertThat(page.locator(BALANCE)).isVisible();
    }

    public String getBalanceText() {
        return page.locator(BALANCE).textContent().trim();
    }

    // Assertions - Status
    public void assertPPh23StatusVisible() {
        assertThat(page.locator(PPH23_STATUS)).isVisible();
    }

    public void assertPPh23MessageContains(String expected) {
        assertThat(page.locator(PPH23_MESSAGE)).containsText(expected);
    }
}
