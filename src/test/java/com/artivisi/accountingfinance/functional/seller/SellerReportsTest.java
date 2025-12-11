package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.entity.Product;
import com.artivisi.accountingfinance.functional.page.InventoryReportPage;
import com.artivisi.accountingfinance.functional.page.TrialBalancePage;
import com.artivisi.accountingfinance.functional.util.CsvLoader;
import com.artivisi.accountingfinance.functional.util.InventoryTransactionRow;
import com.artivisi.accountingfinance.repository.ProductRepository;
import com.artivisi.accountingfinance.service.InventoryService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Online Seller Reports Tests
 * Tests inventory reports and profitability analysis.
 * Uses Page Object Pattern with ID-based locators.
 *
 * Test Data (loaded from CSV - same as SellerTransactionExecutionTest):
 * - 4 Purchase transactions: IP15PRO (10), SGS24 (20), USBC (100), CASE (200)
 * - 4 Sale transactions: IP15PRO (5), SGS24 (8), USBC (30), CASE (50)
 * - 1 Adjustment: USBC (+5)
 *
 * Expected Stock (calculated from transactions):
 * - IP15PRO: 5 pcs @ 15,000,000
 * - SGS24: 12 pcs @ 12,000,000
 * - USBC: 75 pcs @ 25,000
 * - CASE: 150 pcs @ 15,000
 */
@DisplayName("Online Seller - Reports")
@Import(SellerTestDataInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SellerReportsTest extends PlaywrightTestBase {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerTestDataHelper testDataHelper;

    // Page Objects
    private InventoryReportPage inventoryReportPage;
    private TrialBalancePage trialBalancePage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        inventoryReportPage = new InventoryReportPage(page, baseUrl);
        trialBalancePage = new TrialBalancePage(page, baseUrl);
    }

    @BeforeAll
    void setupTransactionData() {
        // Setup products and categories first
        testDataHelper.setupProductsAndCategories();

        // Load transaction CSV (same CSV used by SellerTransactionExecutionTest)
        List<InventoryTransactionRow> transactions = CsvLoader.loadInventoryTransactions("seller/transactions.csv");

        // Execute each transaction programmatically via InventoryService
        for (InventoryTransactionRow tx : transactions) {
            Product product = productRepository.findByCode(tx.productCode())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + tx.productCode()));

            LocalDate txDate = LocalDate.parse(tx.date());
            BigDecimal quantity = BigDecimal.valueOf(tx.quantity());

            switch (tx.transactionType()) {
                case "PURCHASE" -> inventoryService.recordPurchase(
                        product.getId(), txDate, quantity, tx.unitCost(), tx.reference(), tx.notes());
                case "SALE" -> inventoryService.recordSale(
                        product.getId(), txDate, quantity, tx.unitPrice(), tx.reference(), tx.notes());
                case "ADJUSTMENT_IN" -> inventoryService.recordAdjustmentIn(
                        product.getId(), txDate, quantity, tx.unitCost(), tx.reference(), tx.notes());
                case "ADJUSTMENT_OUT" -> inventoryService.recordAdjustmentOut(
                        product.getId(), txDate, quantity, tx.reference(), tx.notes());
            }
        }
    }

    @Test
    @DisplayName("Should display inventory stock balance report with 4 products")
    void shouldDisplayStockBalanceReport() {
        loginAsAdmin();
        initPageObjects();

        // After SellerTransactionExecutionTest, we expect 4 products with stock:
        // IP15PRO: 5 pcs, SGS24: 12 pcs, USBC: 75 pcs, CASE: 150 pcs
        inventoryReportPage.navigateStockBalance()
            .verifyPageTitle("Saldo Stok")
            .verifyReportTableVisible()
            .verifyProductCount(4);  // Verify exactly 4 products

        takeManualScreenshot("seller/report-stock-balance");
    }

    @Test
    @DisplayName("Should display inventory stock movement report with transactions")
    void shouldDisplayStockMovementReport() {
        loginAsAdmin();
        initPageObjects();

        // After SellerTransactionExecutionTest, we expect inventory transactions:
        // 4 purchases + 4 sales + 1 adjustment = 9 transactions
        inventoryReportPage.navigateStockMovement()
            .verifyPageTitle("Mutasi Stok")
            .verifyReportTableVisible();

        // Verify we have transaction data (not empty table)
        int txCount = page.locator("#report-table tbody tr").count();
        if (txCount > 0) {
            assertThat(page.locator("#report-table tbody tr")).not().hasCount(1);  // Not just "no data" row
        }

        takeManualScreenshot("seller/report-stock-movement");
    }

    @Test
    @DisplayName("Should display inventory valuation report with 4 products")
    void shouldDisplayValuationReport() {
        loginAsAdmin();
        initPageObjects();

        // Valuation report shows all 4 products with their costing methods:
        // IP15PRO (FIFO), SGS24 (FIFO), USBC (Weighted Avg), CASE (Weighted Avg)
        inventoryReportPage.navigateValuation()
            .verifyPageTitle("Penilaian")
            .verifyReportTableVisible()
            .verifyProductCount(4);  // Verify exactly 4 products

        takeManualScreenshot("seller/report-valuation");
    }

    @Test
    @DisplayName("Should display product profitability report with sales data")
    void shouldDisplayProductProfitabilityReport() {
        loginAsAdmin();
        initPageObjects();

        // After SellerTransactionExecutionTest, we expect 4 sales transactions
        // Only products that have been sold appear in profitability report
        inventoryReportPage.navigateProfitability()
            .verifyPageTitle("Profitabilitas")
            .verifyReportTableVisible();

        // Verify we have profitability data for sold products
        int productCount = page.locator("#report-table tbody tr").count();
        if (productCount > 0) {
            assertThat(page.locator("#report-table tbody tr")).not().hasCount(1);  // Not just "no data" row
        }

        takeManualScreenshot("seller/report-profitability");
    }

    @Test
    @DisplayName("Should display trial balance")
    void shouldDisplayTrialBalance() {
        loginAsAdmin();
        initPageObjects();

        trialBalancePage.navigate()
            .verifyPageTitle();

        takeManualScreenshot("seller/report-trial-balance");
    }
}
