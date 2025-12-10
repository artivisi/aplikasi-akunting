package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Online Seller Sales Tests
 * Tests inventory sales transactions and reports.
 */
@DisplayName("Online Seller - Sales")
public class SellerSalesTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display inventory transaction list with sales")
    void shouldDisplayInventoryTransactionListWithSales() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify inventory transactions page loads
        assertThat(page.locator("h1")).containsText("Transaksi Persediaan");
    }

    @Test
    @DisplayName("Should display income statement")
    void shouldDisplayIncomeStatement() {
        loginAsAdmin();
        navigateTo("/reports/income-statement");
        waitForPageLoad();

        // Verify income statement loads
        assertThat(page.locator("h1")).containsText("Laba Rugi");
    }

    @Test
    @DisplayName("Should display balance sheet")
    void shouldDisplayBalanceSheet() {
        loginAsAdmin();
        navigateTo("/reports/balance-sheet");
        waitForPageLoad();

        // Verify balance sheet loads
        assertThat(page.locator("h1")).containsText("Neraca");
    }

    @Test
    @DisplayName("Should display cash flow statement")
    void shouldDisplayCashFlowStatement() {
        loginAsAdmin();
        navigateTo("/reports/cash-flow");
        waitForPageLoad();

        // Verify cash flow loads
        assertThat(page.locator("h1")).containsText("Arus Kas");
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
