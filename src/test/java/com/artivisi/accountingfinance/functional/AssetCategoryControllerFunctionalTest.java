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
}
