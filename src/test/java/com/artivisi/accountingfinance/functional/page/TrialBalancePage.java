package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TrialBalancePage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_DATE = "#report-date";
    private static final String AS_OF_DATE = "#asOfDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_PRINT = "#btn-print";
    private static final String TRIAL_BALANCE_TABLE = "#trial-balance-table";
    private static final String TRIAL_BALANCE_ROW = ".trial-balance-row";
    private static final String TOTAL_DEBIT = "#total-debit";
    private static final String TOTAL_CREDIT = "#total-credit";
    private static final String BALANCE_STATUS = "#balance-status";
    private static final String BALANCE_MESSAGE = "#balance-message";
    private static final String TRIAL_BALANCE_CONTENT = "#trial-balance-content";

    public TrialBalancePage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TrialBalancePage navigate() {
        page.navigate(baseUrl + "/reports/trial-balance",
            new com.microsoft.playwright.Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public TrialBalancePage navigateWithDate(String date) {
        page.navigate(baseUrl + "/reports/trial-balance?asOfDate=" + date,
            new com.microsoft.playwright.Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    // Actions
    public void setAsOfDate(String date) {
        page.fill(AS_OF_DATE, date);
    }

    public void clickGenerate() {
        page.click(BTN_GENERATE);
        page.waitForLoadState();
    }

    // Page title assertions
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expectedText);
    }

    // Report header assertions
    public void assertReportTitleVisible() {
        assertThat(page.locator(REPORT_TITLE)).isVisible();
    }

    public void assertReportTitleText(String expectedText) {
        assertThat(page.locator(REPORT_TITLE)).hasText(expectedText);
    }

    public void assertReportDateVisible() {
        assertThat(page.locator(REPORT_DATE)).isVisible();
    }

    public void assertReportDateContains(String expectedText) {
        assertThat(page.locator(REPORT_DATE)).containsText(expectedText);
    }

    // Filter assertions
    public void assertAsOfDateVisible() {
        assertThat(page.locator(AS_OF_DATE)).isVisible();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }

    public void assertPrintButtonVisible() {
        assertThat(page.locator(BTN_PRINT)).isVisible();
    }

    // Table assertions
    public void assertTrialBalanceTableVisible() {
        assertThat(page.locator(TRIAL_BALANCE_TABLE)).isVisible();
    }

    public void assertTrialBalanceHasRows() {
        assertThat(page.locator(TRIAL_BALANCE_ROW)).hasCount(page.locator(TRIAL_BALANCE_ROW).count());
    }

    public int getTrialBalanceRowCount() {
        return page.locator(TRIAL_BALANCE_ROW).count();
    }

    // Total assertions
    public void assertTotalDebitVisible() {
        assertThat(page.locator(TOTAL_DEBIT)).isVisible();
    }

    public void assertTotalCreditVisible() {
        assertThat(page.locator(TOTAL_CREDIT)).isVisible();
    }

    public void assertTotalDebitText(String expectedText) {
        assertThat(page.locator(TOTAL_DEBIT)).hasText(expectedText);
    }

    public void assertTotalCreditText(String expectedText) {
        assertThat(page.locator(TOTAL_CREDIT)).hasText(expectedText);
    }

    public String getTotalDebitText() {
        return page.locator(TOTAL_DEBIT).textContent();
    }

    public String getTotalCreditText() {
        return page.locator(TOTAL_CREDIT).textContent();
    }

    // Balance status assertions
    public void assertBalanceStatusVisible() {
        assertThat(page.locator(BALANCE_STATUS)).isVisible();
    }

    public void assertBalanceStatusText(String expectedText) {
        assertThat(page.locator(BALANCE_STATUS)).hasText(expectedText);
    }

    public void assertBalanceMessageVisible() {
        assertThat(page.locator(BALANCE_MESSAGE)).isVisible();
    }

    public void assertBalanceMessageContains(String expectedText) {
        assertThat(page.locator(BALANCE_MESSAGE)).containsText(expectedText);
    }

    // Content assertions
    public void assertTrialBalanceContentVisible() {
        assertThat(page.locator(TRIAL_BALANCE_CONTENT)).isVisible();
    }

    // Account row assertions
    public void assertAccountRowExists(String accountCode) {
        assertThat(page.locator(".account-code:has-text('" + accountCode + "')")).isVisible();
    }

    public void assertAccountNameExists(String accountName) {
        assertThat(page.locator(".account-name:has-text('" + accountName + "')")).isVisible();
    }
}
