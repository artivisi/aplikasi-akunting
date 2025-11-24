package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BalanceSheetPage {

    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_DATE = "#report-date";
    private static final String AS_OF_DATE = "#asOfDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_PRINT = "#btn-print";
    private static final String TOTAL_ASSETS = "#total-assets";
    private static final String TOTAL_LIABILITIES = "#total-liabilities";
    private static final String TOTAL_EQUITY = "#total-equity";
    private static final String TOTAL_LIABILITIES_EQUITY = "#total-liabilities-equity";
    private static final String CURRENT_YEAR_EARNINGS = "#current-year-earnings";
    private static final String BALANCE_STATUS = "#balance-status";
    private static final String BALANCE_MESSAGE = "#balance-message";
    private static final String ASSET_ITEMS = "#asset-items";
    private static final String LIABILITY_ITEMS = "#liability-items";
    private static final String EQUITY_ITEMS = "#equity-items";
    private static final String ASSET_ROW = ".asset-row";
    private static final String LIABILITY_ROW = ".liability-row";
    private static final String EQUITY_ROW = ".equity-row";

    public BalanceSheetPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public BalanceSheetPage navigate() {
        page.navigate(baseUrl + "/reports/balance-sheet",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public BalanceSheetPage navigateWithDate(String date) {
        page.navigate(baseUrl + "/reports/balance-sheet?asOfDate=" + date,
            new Page.NavigateOptions().setTimeout(30000));
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

    public void assertAsOfDateVisible() {
        assertThat(page.locator(AS_OF_DATE)).isVisible();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }

    public void assertPrintButtonVisible() {
        assertThat(page.locator(BTN_PRINT)).isVisible();
    }

    public void assertReportDateContains(String expected) {
        assertThat(page.locator(REPORT_DATE)).containsText(expected);
    }

    // Assertions - Asset Section
    public void assertAssetItemsVisible() {
        assertThat(page.locator(ASSET_ITEMS)).isVisible();
    }

    public void assertTotalAssetsVisible() {
        assertThat(page.locator(TOTAL_ASSETS)).isVisible();
    }

    public String getTotalAssetsText() {
        return page.locator(TOTAL_ASSETS).textContent().trim();
    }

    public int getAssetRowCount() {
        return page.locator(ASSET_ROW).count();
    }

    // Assertions - Liability Section
    public void assertLiabilityItemsVisible() {
        assertThat(page.locator(LIABILITY_ITEMS)).isVisible();
    }

    public void assertTotalLiabilitiesVisible() {
        assertThat(page.locator(TOTAL_LIABILITIES)).isVisible();
    }

    public String getTotalLiabilitiesText() {
        return page.locator(TOTAL_LIABILITIES).textContent().trim();
    }

    public int getLiabilityRowCount() {
        return page.locator(LIABILITY_ROW).count();
    }

    // Assertions - Equity Section
    public void assertEquityItemsVisible() {
        assertThat(page.locator(EQUITY_ITEMS)).isVisible();
    }

    public void assertTotalEquityVisible() {
        assertThat(page.locator(TOTAL_EQUITY)).isVisible();
    }

    public String getTotalEquityText() {
        return page.locator(TOTAL_EQUITY).textContent().trim();
    }

    public int getEquityRowCount() {
        return page.locator(EQUITY_ROW).count();
    }

    // Assertions - Current Year Earnings
    public void assertCurrentYearEarningsVisible() {
        assertThat(page.locator(CURRENT_YEAR_EARNINGS)).isVisible();
    }

    public String getCurrentYearEarningsText() {
        return page.locator(CURRENT_YEAR_EARNINGS).textContent().trim();
    }

    // Assertions - Balance Check
    public void assertTotalLiabilitiesEquityVisible() {
        assertThat(page.locator(TOTAL_LIABILITIES_EQUITY)).isVisible();
    }

    public String getTotalLiabilitiesEquityText() {
        return page.locator(TOTAL_LIABILITIES_EQUITY).textContent().trim();
    }

    public void assertBalanceStatusVisible() {
        assertThat(page.locator(BALANCE_STATUS)).isVisible();
    }

    public void assertBalanceStatusText(String expected) {
        assertThat(page.locator(BALANCE_STATUS)).hasText(expected);
    }

    public void assertBalanceMessageContains(String expected) {
        assertThat(page.locator(BALANCE_MESSAGE)).containsText(expected);
    }

    // Assertions - Account Names
    public void assertAccountNameExists(String accountName) {
        assertThat(page.locator(".account-name:has-text('" + accountName + "')")).isVisible();
    }
}
