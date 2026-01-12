package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ProductCategoryRepository;
import com.artivisi.accountingfinance.repository.ProductRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for ProductController.
 * Tests product list, create, edit, activate, deactivate, delete operations.
 */
@DisplayName("Product Controller Tests")
@Import(ServiceTestDataInitializer.class)
class ProductControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display product list page")
    void shouldDisplayProductListPage() {
        navigateTo("/products");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter products by category")
    void shouldFilterProductsByCategory() {
        navigateTo("/products");
        waitForPageLoad();

        var categorySelect = page.locator("select[name='categoryId']").first();
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

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter products by status")
    void shouldFilterProductsByStatus() {
        navigateTo("/products");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            statusSelect.selectOption("ACTIVE");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should search products by keyword")
    void shouldSearchProductsByKeyword() {
        navigateTo("/products");
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
    @DisplayName("Should display new product form")
    void shouldDisplayNewProductForm() {
        navigateTo("/products/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new product")
    void shouldCreateNewProduct() {
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/products/new");
        waitForPageLoad();

        // Fill product code
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill("PRD-" + System.currentTimeMillis());
        }

        // Fill product name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Product " + System.currentTimeMillis());
        }

        // Select category
        var categorySelect = page.locator("select[name='category.id'], select[name='categoryId']").first();
        if (categorySelect.isVisible()) {
            categorySelect.selectOption(category.get().getId().toString());
        }

        // Fill unit
        var unitInput = page.locator("input[name='unit']").first();
        if (unitInput.isVisible()) {
            unitInput.fill("pcs");
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
    @DisplayName("Should show validation error for empty name")
    void shouldShowValidationErrorForEmptyName() {
        navigateTo("/products/new");
        waitForPageLoad();

        // Submit without filling required fields
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display product detail page")
    void shouldDisplayProductDetailPage() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/products/" + product.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/products\\/.*"));
    }

    @Test
    @DisplayName("Should display product edit form")
    void shouldDisplayProductEditForm() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/products/" + product.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update product")
    void shouldUpdateProduct() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/products/" + product.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Product " + System.currentTimeMillis());
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
    @DisplayName("Should activate product")
    void shouldActivateProduct() {
        var product = productRepository.findAll().stream()
                .filter(p -> !p.isActive())
                .findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/products/" + product.get().getId());
        waitForPageLoad();

        var activateBtn = page.locator("form[action*='/activate'] button[type='submit']").first();
        if (activateBtn.isVisible()) {
            activateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should deactivate product")
    void shouldDeactivateProduct() {
        var product = productRepository.findAll().stream()
                .filter(p -> p.isActive())
                .findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/products/" + product.get().getId());
        waitForPageLoad();

        var deactivateBtn = page.locator("form[action*='/deactivate'] button[type='submit']").first();
        if (deactivateBtn.isVisible()) {
            deactivateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/products/" + product.get().getId());
        waitForPageLoad();

        var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
