package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.InventoryPurchaseFormPage;
import com.artivisi.accountingfinance.functional.page.InventorySaleFormPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.ProductFormPage;
import com.artivisi.accountingfinance.functional.page.TransactionListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Inventory Auto-Journal Generation (Phase 5)")
class InventoryAutoJournalTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private ProductFormPage productFormPage;
    private InventoryPurchaseFormPage purchaseFormPage;
    private InventorySaleFormPage saleFormPage;
    private TransactionListPage transactionListPage;

    // Account IDs from seed data
    private static final String INVENTORY_ACCOUNT_ID = "10000000-0000-0000-0000-000000000151";
    private static final String COGS_ACCOUNT_ID = "50000000-0000-0000-0000-000000000131";
    private static final String SALES_ACCOUNT_ID = "40000000-0000-0000-0000-000000000104";

    private String testProductCode;
    private String testProductId;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        productFormPage = new ProductFormPage(page, baseUrl());
        purchaseFormPage = new InventoryPurchaseFormPage(page, baseUrl());
        saleFormPage = new InventorySaleFormPage(page, baseUrl());
        transactionListPage = new TransactionListPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();

        // Create a test product with inventory accounts configured
        testProductCode = "AUTOJRNL" + System.currentTimeMillis() % 100000;
        createTestProductWithAccounts();
    }

    private void createTestProductWithAccounts() {
        productFormPage.navigateToNew();
        productFormPage.fillCode(testProductCode);
        productFormPage.fillName("Auto Journal Test Product " + testProductCode);
        productFormPage.fillUnit("pcs");
        productFormPage.selectCostingMethod("WEIGHTED_AVERAGE");
        productFormPage.selectInventoryAccount(INVENTORY_ACCOUNT_ID);
        productFormPage.selectCogsAccount(COGS_ACCOUNT_ID);
        productFormPage.selectSalesAccount(SALES_ACCOUNT_ID);
        productFormPage.fillSellingPrice("25000");
        productFormPage.clickSubmit();

        // Get product ID from URL after creation
        page.navigate(baseUrl() + "/products");
        page.waitForLoadState();
        page.fill("#search-input", testProductCode);
        page.waitForTimeout(500);

        // Click on product to get to detail and extract ID from URL
        page.click("a:has-text('" + testProductCode + "')");
        page.waitForLoadState();
        String url = page.url();
        testProductId = url.substring(url.lastIndexOf("/") + 1);
    }

    @Test
    @DisplayName("Should create journal entry when recording inventory purchase")
    void shouldCreateJournalEntryForPurchase() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String refNumber = "PO-AUTO-" + System.currentTimeMillis() % 10000;

        // Record purchase
        purchaseFormPage.navigate();
        purchaseFormPage.selectProductByValue(testProductId);
        purchaseFormPage.fillDate(today);
        purchaseFormPage.fillQuantity("10");
        purchaseFormPage.fillUnitCost("15000");
        purchaseFormPage.fillReference(refNumber);
        purchaseFormPage.fillNotes("Auto journal test purchase");
        purchaseFormPage.clickSubmit();

        page.waitForLoadState();

        // Verify journal entry was created by checking transaction list
        transactionListPage.navigate();
        transactionListPage.searchTransaction(testProductCode);

        // Should find a transaction with the product code in description
        String pageContent = page.content();
        assertThat(pageContent).contains("Pembelian");
        assertThat(pageContent).contains(testProductCode);
    }

    @Test
    @DisplayName("Should create journal entry when recording inventory sale")
    void shouldCreateJournalEntryForSale() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String purchaseRef = "PO-PRE-" + System.currentTimeMillis() % 10000;
        String saleRef = "SO-AUTO-" + System.currentTimeMillis() % 10000;

        // First, record a purchase to have stock available
        purchaseFormPage.navigate();
        purchaseFormPage.selectProductByValue(testProductId);
        purchaseFormPage.fillDate(today);
        purchaseFormPage.fillQuantity("20");
        purchaseFormPage.fillUnitCost("15000");
        purchaseFormPage.fillReference(purchaseRef);
        purchaseFormPage.fillNotes("Pre-sale purchase");
        purchaseFormPage.clickSubmit();
        page.waitForLoadState();

        // Record sale
        saleFormPage.navigate();
        saleFormPage.selectProductByValue(testProductId);
        saleFormPage.fillDate(today);
        saleFormPage.fillQuantity("5");
        saleFormPage.fillUnitPrice("25000");
        saleFormPage.fillReference(saleRef);
        saleFormPage.fillNotes("Auto journal test sale");
        saleFormPage.clickSubmit();

        page.waitForLoadState();

        // Verify journal entries were created by checking transaction list
        transactionListPage.navigate();
        transactionListPage.searchTransaction(testProductCode);

        // Should find transactions with the product code
        String pageContent = page.content();
        assertThat(pageContent).contains("Penjualan");
        assertThat(pageContent).contains(testProductCode);
    }

    @Test
    @DisplayName("Should not create journal entry for product without inventory account")
    void shouldNotCreateJournalEntryForProductWithoutInventoryAccount() {
        // Create a product without inventory accounts
        String noAccountProductCode = "NOACC" + System.currentTimeMillis() % 100000;
        productFormPage.navigateToNew();
        productFormPage.fillCode(noAccountProductCode);
        productFormPage.fillName("No Account Product " + noAccountProductCode);
        productFormPage.fillUnit("pcs");
        productFormPage.selectCostingMethod("WEIGHTED_AVERAGE");
        // Do NOT set inventory accounts
        productFormPage.clickSubmit();

        // Get product ID
        page.navigate(baseUrl() + "/products");
        page.waitForLoadState();
        page.fill("#search-input", noAccountProductCode);
        page.waitForTimeout(500);
        page.click("a:has-text('" + noAccountProductCode + "')");
        page.waitForLoadState();
        String url = page.url();
        String noAccountProductId = url.substring(url.lastIndexOf("/") + 1);

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String refNumber = "PO-NOACC-" + System.currentTimeMillis() % 10000;

        // Record purchase for product without accounts
        purchaseFormPage.navigate();
        purchaseFormPage.selectProductByValue(noAccountProductId);
        purchaseFormPage.fillDate(today);
        purchaseFormPage.fillQuantity("5");
        purchaseFormPage.fillUnitCost("10000");
        purchaseFormPage.fillReference(refNumber);
        purchaseFormPage.fillNotes("No account product purchase");
        purchaseFormPage.clickSubmit();

        page.waitForLoadState();

        // Check transaction list - should NOT contain this product code
        transactionListPage.navigate();
        transactionListPage.searchTransaction(noAccountProductCode);

        // The transaction list should not show any entries for this product
        String pageContent = page.content();
        // We check that there's no transaction entry with this specific reference number
        assertThat(pageContent).doesNotContain(refNumber);
    }
}
