package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for InventoryReportController.
 * Tests inventory report index, stock balance, stock movement, valuation, and profitability pages.
 */
@DisplayName("Inventory Report Controller Tests")
@Import(ServiceTestDataInitializer.class)
class InventoryReportControllerFunctionalTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== INDEX PAGE ====================

    @Test
    @DisplayName("Should display inventory reports index page")
    void shouldDisplayInventoryReportsIndexPage() {
        navigateTo("/inventory/reports");
        waitForPageLoad();

        org.assertj.core.api.Assertions.assertThat(page.content())
            .as("Reports index page should show report menu")
            .contains("Laporan Persediaan");
    }

    @Test
    @DisplayName("Should have links to report types on index page")
    void shouldHaveLinksToReportTypesOnIndexPage() {
        navigateTo("/inventory/reports");
        waitForPageLoad();

        var stockBalanceLink = page.locator("a[href*='/inventory/reports/stock-balance']").first();
        assertThat(stockBalanceLink).isVisible();

        var stockMovementLink = page.locator("a[href*='/inventory/reports/stock-movement']").first();
        assertThat(stockMovementLink).isVisible();
    }

    // ==================== STOCK BALANCE REPORT ====================

    @Test
    @DisplayName("Should display stock balance report page")
    void shouldDisplayStockBalanceReportPage() {
        navigateTo("/inventory/reports/stock-balance");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Saldo Stok");
    }

    @Test
    @DisplayName("Should filter stock balance by category")
    void shouldFilterStockBalanceByCategory() {
        navigateTo("/inventory/reports/stock-balance");
        waitForPageLoad();

        var categorySelect = page.locator("#categoryId").first();
        if (categorySelect.isVisible()) {
            var options = categorySelect.locator("option");
            if (options.count() > 1) {
                categorySelect.selectOption(new String[]{options.nth(1).getAttribute("value")});

                var filterBtn = page.locator("form button[type='submit']").first();
                if (filterBtn.isVisible()) {
                    filterBtn.click();
                    waitForPageLoad();
                }
            }
        }

        assertThat(page.locator("#page-title")).hasText("Laporan Saldo Stok");
    }

    @Test
    @DisplayName("Should search stock balance by product name")
    void shouldSearchStockBalanceByProductName() {
        navigateTo("/inventory/reports/stock-balance?search=kopi");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Saldo Stok");
    }

    @Test
    @DisplayName("Should have export links on stock balance page")
    void shouldHaveExportLinksOnStockBalancePage() {
        navigateTo("/inventory/reports/stock-balance");
        waitForPageLoad();

        var pdfLink = page.locator("a[href*='/stock-balance/export/pdf']").first();
        assertThat(pdfLink).isVisible();

        var excelLink = page.locator("a[href*='/stock-balance/export/excel']").first();
        assertThat(excelLink).isVisible();
    }

    @Test
    @DisplayName("Should access stock balance print view")
    void shouldAccessStockBalancePrintView() {
        navigateTo("/inventory/reports/stock-balance/print");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should trigger stock balance PDF export")
    void shouldTriggerStockBalancePdfExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/stock-balance/export/pdf");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Stock balance PDF export should return success or redirect")
            .isIn(200, 302);
    }

    @Test
    @DisplayName("Should trigger stock balance Excel export")
    void shouldTriggerStockBalanceExcelExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/stock-balance/export/excel");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Stock balance Excel export should return success or redirect")
            .isIn(200, 302);
    }

    // ==================== STOCK MOVEMENT REPORT ====================

    @Test
    @DisplayName("Should display stock movement report page")
    void shouldDisplayStockMovementReportPage() {
        navigateTo("/inventory/reports/stock-movement");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Mutasi Stok");
    }

    @Test
    @DisplayName("Should filter stock movement by date range")
    void shouldFilterStockMovementByDateRange() {
        navigateTo("/inventory/reports/stock-movement?startDate=2024-01-01&endDate=2024-12-31");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Mutasi Stok");
    }

    @Test
    @DisplayName("Should filter stock movement by category")
    void shouldFilterStockMovementByCategory() {
        navigateTo("/inventory/reports/stock-movement");
        waitForPageLoad();

        var categorySelect = page.locator("#categoryId").first();
        if (categorySelect.isVisible()) {
            var options = categorySelect.locator("option");
            if (options.count() > 1) {
                categorySelect.selectOption(new String[]{options.nth(1).getAttribute("value")});
                page.locator("form button[type='submit']").first().click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("#page-title")).hasText("Laporan Mutasi Stok");
    }

    @Test
    @DisplayName("Should access stock movement print view")
    void shouldAccessStockMovementPrintView() {
        navigateTo("/inventory/reports/stock-movement/print");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should trigger stock movement PDF export")
    void shouldTriggerStockMovementPdfExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/stock-movement/export/pdf");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Stock movement PDF export should return success or redirect")
            .isIn(200, 302);
    }

    @Test
    @DisplayName("Should trigger stock movement Excel export")
    void shouldTriggerStockMovementExcelExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/stock-movement/export/excel");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Stock movement Excel export should return success or redirect")
            .isIn(200, 302);
    }

    // ==================== VALUATION REPORT ====================

    @Test
    @DisplayName("Should display valuation report page")
    void shouldDisplayValuationReportPage() {
        navigateTo("/inventory/reports/valuation");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Penilaian Persediaan");
    }

    @Test
    @DisplayName("Should filter valuation by category")
    void shouldFilterValuationByCategory() {
        navigateTo("/inventory/reports/valuation");
        waitForPageLoad();

        var categorySelect = page.locator("#categoryId").first();
        if (categorySelect.isVisible()) {
            var options = categorySelect.locator("option");
            if (options.count() > 1) {
                categorySelect.selectOption(new String[]{options.nth(1).getAttribute("value")});
                page.locator("form button[type='submit']").first().click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("#page-title")).hasText("Laporan Penilaian Persediaan");
    }

    @Test
    @DisplayName("Should access valuation print view")
    void shouldAccessValuationPrintView() {
        navigateTo("/inventory/reports/valuation/print");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should trigger valuation PDF export")
    void shouldTriggerValuationPdfExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/valuation/export/pdf");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Valuation PDF export should return success or redirect")
            .isIn(200, 302);
    }

    @Test
    @DisplayName("Should trigger valuation Excel export")
    void shouldTriggerValuationExcelExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/valuation/export/excel");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Valuation Excel export should return success or redirect")
            .isIn(200, 302);
    }

    // ==================== PROFITABILITY REPORT ====================

    @Test
    @DisplayName("Should display profitability report page")
    void shouldDisplayProfitabilityReportPage() {
        navigateTo("/inventory/reports/profitability");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Profitabilitas Produk");
    }

    @Test
    @DisplayName("Should filter profitability by date range")
    void shouldFilterProfitabilityByDateRange() {
        navigateTo("/inventory/reports/profitability?startDate=2024-01-01&endDate=2024-12-31");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Profitabilitas Produk");
    }

    @Test
    @DisplayName("Should access profitability print view")
    void shouldAccessProfitabilityPrintView() {
        navigateTo("/inventory/reports/profitability/print");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should trigger profitability PDF export")
    void shouldTriggerProfitabilityPdfExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/profitability/export/pdf");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Profitability PDF export should return success or redirect")
            .isIn(200, 302);
    }

    @Test
    @DisplayName("Should trigger profitability Excel export")
    void shouldTriggerProfitabilityExcelExport() {
        var response = page.request().get(baseUrl() + "/inventory/reports/profitability/export/excel");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Profitability Excel export should return success or redirect")
            .isIn(200, 302);
    }
}
