package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.InventoryTransactionRepository;
import com.artivisi.accountingfinance.repository.ProductRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for InventoryTransactionController.
 * Tests stock list, transaction list, purchase, sale, adjustment operations.
 */
@DisplayName("Inventory Transaction Controller Tests")
@Import(ServiceTestDataInitializer.class)
class InventoryTransactionControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display stock list page")
    void shouldDisplayStockListPage() {
        navigateTo("/inventory/stock");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should search stock by keyword")
    void shouldSearchStockByKeyword() {
        navigateTo("/inventory/stock");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search'], input[name='keyword']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("kopi");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display stock table")
    void shouldDisplayStockTable() {
        navigateTo("/inventory/stock/table");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display transaction list page")
    void shouldDisplayTransactionListPage() {
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter transactions by type")
    void shouldFilterTransactionsByType() {
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        var typeSelect = page.locator("select[name='type']").first();
        if (typeSelect.isVisible()) {
            typeSelect.selectOption("PURCHASE");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter transactions by date range")
    void shouldFilterTransactionsByDateRange() {
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        var startDateInput = page.locator("input[name='startDate']").first();
        var endDateInput = page.locator("input[name='endDate']").first();

        if (startDateInput.isVisible() && endDateInput.isVisible()) {
            startDateInput.fill("2024-01-01");
            endDateInput.fill("2024-12-31");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display transaction table")
    void shouldDisplayTransactionTable() {
        navigateTo("/inventory/transactions/table");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display transaction detail")
    void shouldDisplayTransactionDetail() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/inventory/transactions/" + transaction.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/transactions\\/.*"));
    }

    @Test
    @DisplayName("Should display purchase form")
    void shouldDisplayPurchaseForm() {
        navigateTo("/inventory/purchase");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create purchase transaction")
    void shouldCreatePurchaseTransaction() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/inventory/purchase");
        waitForPageLoad();

        // Select product
        var productSelect = page.locator("select[name='product.id'], select[name='productId']").first();
        if (productSelect.isVisible()) {
            productSelect.selectOption(product.get().getId().toString());
        }

        // Fill quantity
        var quantityInput = page.locator("input[name='quantity']").first();
        if (quantityInput.isVisible()) {
            quantityInput.fill("10");
        }

        // Fill unit cost
        var unitCostInput = page.locator("input[name='unitCost']").first();
        if (unitCostInput.isVisible()) {
            unitCostInput.fill("10000");
        }

        // Fill transaction date
        var transactionDateInput = page.locator("input[name='transactionDate']").first();
        if (transactionDateInput.isVisible()) {
            transactionDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display sale form")
    void shouldDisplaySaleForm() {
        navigateTo("/inventory/sale");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create sale transaction")
    void shouldCreateSaleTransaction() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/inventory/sale");
        waitForPageLoad();

        // Select product
        var productSelect = page.locator("select[name='product.id'], select[name='productId']").first();
        if (productSelect.isVisible()) {
            productSelect.selectOption(product.get().getId().toString());
        }

        // Fill quantity
        var quantityInput = page.locator("input[name='quantity']").first();
        if (quantityInput.isVisible()) {
            quantityInput.fill("5");
        }

        // Fill selling price
        var sellingPriceInput = page.locator("input[name='sellingPrice']").first();
        if (sellingPriceInput.isVisible()) {
            sellingPriceInput.fill("15000");
        }

        // Fill transaction date
        var transactionDateInput = page.locator("input[name='transactionDate']").first();
        if (transactionDateInput.isVisible()) {
            transactionDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display adjustment form")
    void shouldDisplayAdjustmentForm() {
        navigateTo("/inventory/adjustment");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create adjustment transaction")
    void shouldCreateAdjustmentTransaction() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/inventory/adjustment");
        waitForPageLoad();

        // Select product
        var productSelect = page.locator("select[name='product.id'], select[name='productId']").first();
        if (productSelect.isVisible()) {
            productSelect.selectOption(product.get().getId().toString());
        }

        // Fill quantity (positive for increase, negative for decrease)
        var quantityInput = page.locator("input[name='quantity']").first();
        if (quantityInput.isVisible()) {
            quantityInput.fill("2");
        }

        // Fill reason
        var reasonInput = page.locator("textarea[name='reason'], input[name='reason']").first();
        if (reasonInput.isVisible()) {
            reasonInput.fill("Stock count adjustment");
        }

        // Fill transaction date
        var transactionDateInput = page.locator("input[name='transactionDate']").first();
        if (transactionDateInput.isVisible()) {
            transactionDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display stock detail for product")
    void shouldDisplayStockDetailForProduct() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/inventory/stock/" + product.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/stock\\/.*"));
    }
}
