package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Online Seller Marketplace Tests
 * Tests marketplace-specific clients and flows.
 */
@DisplayName("Online Seller - Marketplace")
public class SellerMarketplaceTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display marketplace clients")
    void shouldDisplayMarketplaceClients() {
        loginAsAdmin();
        navigateTo("/clients");
        waitForPageLoad();

        // Verify clients page loads
        assertThat(page.locator("h1")).containsText("Klien");

        // Verify marketplace entities exist (using first() to avoid strict mode violation)
        assertThat(page.locator("text=Tokopedia").first()).isVisible();
        assertThat(page.locator("text=Shopee").first()).isVisible();
    }

    @Test
    @DisplayName("Should display suppliers")
    void shouldDisplaySuppliers() {
        loginAsAdmin();
        navigateTo("/clients");
        waitForPageLoad();

        // Verify supplier clients exist from V820 (using first() to avoid strict mode violation)
        assertThat(page.locator("text=Erajaya").first()).isVisible();
        assertThat(page.locator("text=Samsung").first()).isVisible();
    }

    @Test
    @DisplayName("Should display transaction list")
    void shouldDisplayTransactionList() {
        loginAsAdmin();
        navigateTo("/transactions");
        waitForPageLoad();

        // Verify transactions page loads
        assertThat(page.locator("h1")).containsText("Transaksi");
    }

    @Test
    @DisplayName("Should display cash flow report")
    void shouldDisplayCashFlowReport() {
        loginAsAdmin();
        navigateTo("/reports/cash-flow");
        waitForPageLoad();

        // Verify cash flow report loads
        assertThat(page.locator("h1")).containsText("Arus Kas");
    }
}
