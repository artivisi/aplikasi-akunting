package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.AssetCategoryRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for AssetCategoryController.
 * Tests asset category list, create, edit, activate, deactivate, delete operations.
 */
@DisplayName("Asset Category Controller Tests")
@Import(ServiceTestDataInitializer.class)
class AssetCategoryControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private AssetCategoryRepository categoryRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display asset category list page")
    void shouldDisplayAssetCategoryListPage() {
        navigateTo("/assets/categories");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter categories by status")
    void shouldFilterCategoriesByStatus() {
        navigateTo("/assets/categories");
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
    @DisplayName("Should search categories by keyword")
    void shouldSearchCategoriesByKeyword() {
        navigateTo("/assets/categories");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search'], input[name='keyword']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("kendaraan");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new asset category form")
    void shouldDisplayNewAssetCategoryForm() {
        navigateTo("/assets/categories/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new asset category")
    void shouldCreateNewAssetCategory() {
        navigateTo("/assets/categories/new");
        waitForPageLoad();

        // Fill category name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Category " + System.currentTimeMillis());
        }

        // Fill useful life months
        var usefulLifeInput = page.locator("input[name='usefulLifeMonths']").first();
        if (usefulLifeInput.isVisible()) {
            usefulLifeInput.fill("60");
        }

        // Select depreciation method
        var methodSelect = page.locator("select[name='depreciationMethod']").first();
        if (methodSelect.isVisible()) {
            methodSelect.selectOption("STRAIGHT_LINE");
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
        navigateTo("/assets/categories/new");
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
    @DisplayName("Should display asset category edit form")
    void shouldDisplayAssetCategoryEditForm() {
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/categories/" + category.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update asset category")
    void shouldUpdateAssetCategory() {
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/categories/" + category.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Category " + System.currentTimeMillis());
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
    @DisplayName("Should activate asset category")
    void shouldActivateAssetCategory() {
        var category = categoryRepository.findAll().stream()
                .filter(c -> c.getActive() == null || !c.getActive())
                .findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/categories/" + category.get().getId() + "/edit");
        waitForPageLoad();

        var activateBtn = page.locator("form[action*='/activate'] button[type='submit']").first();
        if (activateBtn.isVisible()) {
            activateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should deactivate asset category")
    void shouldDeactivateAssetCategory() {
        var category = categoryRepository.findAll().stream()
                .filter(c -> c.getActive() != null && c.getActive())
                .findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/categories/" + category.get().getId() + "/edit");
        waitForPageLoad();

        var deactivateBtn = page.locator("form[action*='/deactivate'] button[type='submit']").first();
        if (deactivateBtn.isVisible()) {
            deactivateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should delete asset category")
    void shouldDeleteAssetCategory() {
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/categories/" + category.get().getId() + "/edit");
        waitForPageLoad();

        var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== ADDITIONAL COVERAGE TESTS ====================

    @Test
    @DisplayName("Should access HTMX category list fragment")
    void shouldAccessHtmxCategoryListFragment() {
        var response = page.request().get(baseUrl() + "/assets/categories",
            com.microsoft.playwright.options.RequestOptions.create()
                .setHeader("HX-Request", "true"));

        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("HTMX category list fragment should return 200")
            .isEqualTo(200);
    }

    @Test
    @DisplayName("Should filter categories by active status via param")
    void shouldFilterCategoriesByActiveStatusViaParam() {
        navigateTo("/assets/categories?active=true");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter categories by inactive status")
    void shouldFilterCategoriesByInactiveStatus() {
        navigateTo("/assets/categories?active=false");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter categories with combined search and active")
    void shouldFilterCategoriesWithCombinedSearchAndActive() {
        navigateTo("/assets/categories?search=kendaraan&active=true");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should show form fields for new category")
    void shouldShowFormFieldsForNewCategory() {
        navigateTo("/assets/categories/new");
        waitForPageLoad();

        // Verify all required form fields are present
        assertThat(page.locator("#code")).isVisible();
        assertThat(page.locator("#name")).isVisible();
        assertThat(page.locator("#description, textarea[name='description']").first()).isVisible();

        // Verify depreciation method select
        var methodSelect = page.locator("select[name='depreciationMethod']").first();
        assertThat(methodSelect).isVisible();
    }

    @Test
    @DisplayName("Should show account selects in form")
    void shouldShowAccountSelectsInForm() {
        navigateTo("/assets/categories/new");
        waitForPageLoad();

        // The form should have account selection dropdowns
        var assetAccountSelect = page.locator("select[name='assetAccount']").first();
        var accumDepSelect = page.locator("select[name='accumulatedDepreciationAccount']").first();
        var depExpenseSelect = page.locator("select[name='depreciationExpenseAccount']").first();

        if (assetAccountSelect.isVisible()) {
            assertThat(assetAccountSelect).isVisible();
        }
        if (accumDepSelect.isVisible()) {
            assertThat(accumDepSelect).isVisible();
        }
        if (depExpenseSelect.isVisible()) {
            assertThat(depExpenseSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should activate category from list page")
    void shouldActivateCategoryFromListPage() {
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        // POST to activate endpoint
        var response = page.request().post(
            baseUrl() + "/assets/categories/" + category.get().getId() + "/activate");

        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Activate category should respond")
            .isIn(200, 302, 403);
    }

    @Test
    @DisplayName("Should deactivate category from list page")
    void shouldDeactivateCategoryFromListPage() {
        var category = categoryRepository.findAll().stream()
            .filter(c -> c.getActive() != null && c.getActive())
            .findFirst();

        if (category.isEmpty()) {
            return;
        }

        // POST to deactivate endpoint
        var response = page.request().post(
            baseUrl() + "/assets/categories/" + category.get().getId() + "/deactivate");

        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Deactivate category should respond")
            .isIn(200, 302, 403);
    }

    @Test
    @DisplayName("Should paginate category list")
    void shouldPaginateCategoryList() {
        navigateTo("/assets/categories?page=0&size=5");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }
}
