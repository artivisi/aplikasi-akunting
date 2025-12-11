package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Inventory Reports.
 * Handles stock balance, stock movement, valuation, and profitability reports.
 */
public class InventoryReportPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs where available, h1 for legacy pages
    private static final String PAGE_TITLE = "h1";  // Inventory reports use h1 without ID
    private static final String REPORT_CONTENT = "#report-content";
    private static final String REPORT_TABLE = "#report-table";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_EXPORT_PDF = "#btn-export-pdf";
    private static final String BTN_EXPORT_EXCEL = "#btn-export-excel";

    // Report-specific totals
    private static final String TOTAL_STOCK_VALUE = "#total-stock-value";
    private static final String TOTAL_REVENUE = "#total-revenue";
    private static final String TOTAL_COGS = "#total-cogs";
    private static final String TOTAL_GROSS_PROFIT = "#total-gross-profit";
    private static final String GROSS_MARGIN_PERCENT = "#gross-margin-percent";

    public InventoryReportPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public InventoryReportPage navigateStockBalance() {
        page.navigate(baseUrl + "/inventory/reports/stock-balance");
        page.waitForLoadState();
        return this;
    }

    public InventoryReportPage navigateStockMovement() {
        // Default: show all transactions from start of 2024
        return navigateStockMovement("2024-01-01", "2024-12-31");
    }

    public InventoryReportPage navigateStockMovement(String startDate, String endDate) {
        page.navigate(baseUrl + "/inventory/reports/stock-movement?startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
        return this;
    }

    public InventoryReportPage navigateValuation() {
        page.navigate(baseUrl + "/inventory/reports/valuation");
        page.waitForLoadState();
        return this;
    }

    public InventoryReportPage navigateProfitability() {
        // Default: show all transactions from start of 2024
        return navigateProfitability("2024-01-01", "2024-12-31");
    }

    public InventoryReportPage navigateProfitability(String startDate, String endDate) {
        page.navigate(baseUrl + "/inventory/reports/profitability?startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
        return this;
    }

    public InventoryReportPage verifyPageTitle(String expectedTitle) {
        assertThat(page.locator(PAGE_TITLE)).containsText(expectedTitle);
        return this;
    }

    public InventoryReportPage verifyReportContentVisible() {
        assertThat(page.locator(REPORT_CONTENT)).isVisible();
        return this;
    }

    public InventoryReportPage verifyReportTableVisible() {
        assertThat(page.locator(REPORT_TABLE)).isVisible();
        return this;
    }

    public InventoryReportPage setDateRange(String startDate, String endDate) {
        page.locator(START_DATE).fill(startDate);
        page.locator(END_DATE).fill(endDate);
        return this;
    }

    public InventoryReportPage generateReport() {
        page.locator(BTN_GENERATE).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify total stock value.
     */
    public InventoryReportPage verifyTotalStockValue(String expectedValue) {
        assertThat(page.locator(TOTAL_STOCK_VALUE)).containsText(expectedValue);
        return this;
    }

    /**
     * Verify total revenue in profitability report.
     */
    public InventoryReportPage verifyTotalRevenue(String expectedValue) {
        assertThat(page.locator(TOTAL_REVENUE)).containsText(expectedValue);
        return this;
    }

    /**
     * Verify total COGS in profitability report.
     */
    public InventoryReportPage verifyTotalCogs(String expectedValue) {
        assertThat(page.locator(TOTAL_COGS)).containsText(expectedValue);
        return this;
    }

    /**
     * Verify total gross profit in profitability report.
     */
    public InventoryReportPage verifyTotalGrossProfit(String expectedValue) {
        assertThat(page.locator(TOTAL_GROSS_PROFIT)).containsText(expectedValue);
        return this;
    }

    /**
     * Verify gross margin percentage.
     */
    public InventoryReportPage verifyGrossMarginPercent(String expectedPercent) {
        assertThat(page.locator(GROSS_MARGIN_PERCENT)).containsText(expectedPercent);
        return this;
    }

    /**
     * Verify product row count in report.
     */
    public InventoryReportPage verifyProductCount(int expectedCount) {
        assertThat(page.locator(REPORT_TABLE + " tbody tr")).hasCount(expectedCount);
        return this;
    }

    public InventoryReportPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
