package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Online Seller Inventory Tests
 * Tests product catalog, categories, and inventory management.
 */
@DisplayName("Online Seller - Inventory")
public class SellerInventoryTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display product list")
    void shouldDisplayProductList() {
        loginAsAdmin();
        navigateTo("/products");
        waitForPageLoad();

        // Verify products page loads
        assertThat(page.locator("h1")).containsText("Produk");

        // Verify test products exist from V820 migration
        assertThat(page.locator("text=iPhone 15 Pro")).isVisible();
        assertThat(page.locator("text=Samsung Galaxy S24")).isVisible();
    }

    @Test
    @DisplayName("Should display product detail")
    void shouldDisplayProductDetail() {
        loginAsAdmin();
        navigateTo("/products");
        waitForPageLoad();

        // Click on iPhone product
        page.locator("text=iPhone 15 Pro").first().click();
        waitForPageLoad();

        // Verify product detail page loads
        assertThat(page.locator("text=iPhone 15 Pro").first()).isVisible();
    }

    @Test
    @DisplayName("Should display product categories")
    void shouldDisplayProductCategories() {
        loginAsAdmin();
        navigateTo("/products/categories");
        waitForPageLoad();

        // Verify categories page loads
        assertThat(page.locator("h1")).containsText("Kategori");

        // Verify test categories exist
        assertThat(page.locator("text=Smartphone")).isVisible();
        assertThat(page.locator("text=Accessories")).isVisible();
    }

    @Test
    @DisplayName("Should display inventory transactions")
    void shouldDisplayInventoryTransactions() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify inventory transactions page loads
        assertThat(page.locator("h1")).containsText("Transaksi Persediaan");
    }

    @Test
    @DisplayName("Should display stock balances")
    void shouldDisplayStockBalances() {
        loginAsAdmin();
        navigateTo("/inventory/stock");
        waitForPageLoad();

        // Verify stock page loads
        assertThat(page.locator("h1")).containsText("Stok Barang");

        // Verify products from V820 have stock
        assertThat(page.locator("text=iPhone 15 Pro")).isVisible();
    }
}
