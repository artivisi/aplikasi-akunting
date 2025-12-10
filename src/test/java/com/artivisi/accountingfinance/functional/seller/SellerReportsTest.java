package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Online Seller Reports Tests
 * Tests inventory reports and profitability analysis.
 */
@DisplayName("Online Seller - Reports")
public class SellerReportsTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display inventory stock balance report")
    void shouldDisplayStockBalanceReport() {
        loginAsAdmin();
        navigateTo("/inventory/reports/stock-balance");
        waitForPageLoad();

        // Verify stock balance report loads
        assertThat(page.locator("h1")).containsText("Laporan Saldo Stok");
    }

    @Test
    @DisplayName("Should display inventory stock movement report")
    void shouldDisplayStockMovementReport() {
        loginAsAdmin();
        navigateTo("/inventory/reports/stock-movement");
        waitForPageLoad();

        // Verify stock movement report loads
        assertThat(page.locator("h1")).containsText("Laporan Mutasi Stok");
    }

    @Test
    @DisplayName("Should display inventory valuation report")
    void shouldDisplayValuationReport() {
        loginAsAdmin();
        navigateTo("/inventory/reports/valuation");
        waitForPageLoad();

        // Verify valuation report loads
        assertThat(page.locator("h1")).containsText("Laporan Penilaian Persediaan");
    }

    @Test
    @DisplayName("Should display product profitability report")
    void shouldDisplayProductProfitabilityReport() {
        loginAsAdmin();
        navigateTo("/inventory/reports/profitability");
        waitForPageLoad();

        // Verify profitability report loads
        assertThat(page.locator("h1")).containsText("Laporan Profitabilitas Produk");
    }

    @Test
    @DisplayName("Should display trial balance")
    void shouldDisplayTrialBalance() {
        loginAsAdmin();
        navigateTo("/reports/trial-balance");
        waitForPageLoad();

        // Verify trial balance loads
        assertThat(page.locator("h1")).containsText("Neraca Saldo");
    }
}
