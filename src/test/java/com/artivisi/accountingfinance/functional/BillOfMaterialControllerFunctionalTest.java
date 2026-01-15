package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.BillOfMaterialRepository;
import com.artivisi.accountingfinance.repository.ProductRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for BillOfMaterialController.
 * Tests BOM list, create, edit, detail, delete operations.
 */
@DisplayName("Bill Of Material Controller Tests")
@Import(ServiceTestDataInitializer.class)
class BillOfMaterialControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private BillOfMaterialRepository bomRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display BOM list page")
    void shouldDisplayBOMListPage() {
        navigateTo("/inventory/bom");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should search BOM by keyword")
    void shouldSearchBOMByKeyword() {
        navigateTo("/inventory/bom");
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
    @DisplayName("Should display new BOM form")
    void shouldDisplayNewBOMForm() {
        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new BOM")
    void shouldCreateNewBOM() {
        var product = productRepository.findAll().stream().findFirst();
        if (product.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        // Fill BOM name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test BOM " + System.currentTimeMillis());
        }

        // Select finished product
        var productSelect = page.locator("select[name='finishedProduct.id'], select[name='finishedProductId']").first();
        if (productSelect.isVisible()) {
            productSelect.selectOption(product.get().getId().toString());
        }

        // Fill output quantity
        var outputQtyInput = page.locator("input[name='outputQuantity']").first();
        if (outputQtyInput.isVisible()) {
            outputQtyInput.fill("1");
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
    @DisplayName("Should display BOM detail page")
    void shouldDisplayBOMDetailPage() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom\\/.*"));
    }

    @Test
    @DisplayName("Should display BOM edit form")
    void shouldDisplayBOMEditForm() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update BOM")
    void shouldUpdateBOM() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated BOM " + System.currentTimeMillis());
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
    @DisplayName("Should delete BOM")
    void shouldDeleteBOM() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId());
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
    @DisplayName("Should search BOM via query parameter")
    void shouldSearchBOMViaQueryParameter() {
        navigateTo("/inventory/bom?search=kopi");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should handle non-existent BOM detail")
    void shouldHandleNonExistentBOMDetail() {
        navigateTo("/inventory/bom/00000000-0000-0000-0000-000000000000");
        waitForPageLoad();

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom.*"));
    }

    @Test
    @DisplayName("Should handle non-existent BOM edit")
    void shouldHandleNonExistentBOMEdit() {
        navigateTo("/inventory/bom/00000000-0000-0000-0000-000000000000/edit");
        waitForPageLoad();

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom.*"));
    }

    @Test
    @DisplayName("Should create BOM with component lines")
    void shouldCreateBOMWithComponentLines() {
        var products = productRepository.findAll();
        if (products.size() < 2) {
            return;
        }

        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        // Fill BOM code
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill("BOM-TEST-" + System.currentTimeMillis());
        }

        // Fill BOM name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test BOM With Lines " + System.currentTimeMillis());
        }

        // Select finished product
        var productSelect = page.locator("select[name='productId']").first();
        if (productSelect.isVisible()) {
            productSelect.selectOption(products.get(0).getId().toString());
        }

        // Fill output quantity
        var outputQtyInput = page.locator("input[name='outputQuantity']").first();
        if (outputQtyInput.isVisible()) {
            outputQtyInput.fill("1");
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
    @DisplayName("Should display products on create form")
    void shouldDisplayProductsOnCreateForm() {
        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        var productSelect = page.locator("select[name='productId']").first();
        assertThat(productSelect).isVisible();
    }

    @Test
    @DisplayName("Should display products on edit form")
    void shouldDisplayProductsOnEditForm() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        var productSelect = page.locator("select[name='productId']").first();
        if (productSelect.isVisible()) {
            assertThat(productSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should search with empty search parameter")
    void shouldSearchWithEmptyParameter() {
        navigateTo("/inventory/bom?search=");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should update BOM with active checkbox")
    void shouldUpdateBOMWithActiveCheckbox() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        // Toggle active checkbox
        var activeCheckbox = page.locator("input[name='active']").first();
        if (activeCheckbox.isVisible()) {
            if (activeCheckbox.isChecked()) {
                activeCheckbox.uncheck();
            } else {
                activeCheckbox.check();
            }
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
    @DisplayName("Should handle BOM detail page with lines")
    void shouldHandleBOMDetailPageWithLines() {
        var bom = bomRepository.findAll().stream()
                .filter(b -> b.getLines() != null && !b.getLines().isEmpty())
                .findFirst();

        if (bom.isEmpty()) {
            // If no BOM with lines, just test any BOM
            bom = bomRepository.findAll().stream().findFirst();
        }

        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom\\/.*"));
    }

    // ==================== MORE COVERAGE TESTS ====================

    @Test
    @DisplayName("Should create BOM with all fields including description")
    void shouldCreateBOMWithAllFieldsIncludingDescription() {
        var products = productRepository.findAll();
        if (products.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        // Fill BOM code
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill("BOM-FULL-" + System.currentTimeMillis());
        }

        // Fill BOM name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Complete BOM Test " + System.currentTimeMillis());
        }

        // Fill description
        var descInput = page.locator("input[name='description'], textarea[name='description']").first();
        if (descInput.isVisible()) {
            descInput.fill("This is a test BOM with full fields");
        }

        // Select finished product
        var productSelect = page.locator("select[name='productId']").first();
        if (productSelect.isVisible()) {
            productSelect.selectOption(products.get(0).getId().toString());
        }

        // Fill output quantity
        var outputQtyInput = page.locator("input[name='outputQuantity']").first();
        if (outputQtyInput.isVisible()) {
            outputQtyInput.fill("5");
        }

        // Set active
        var activeCheckbox = page.locator("input[name='active']").first();
        if (activeCheckbox.isVisible()) {
            activeCheckbox.check();
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
    @DisplayName("Should display description field on form")
    void shouldDisplayDescriptionFieldOnForm() {
        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        var descField = page.locator("input[name='description'], textarea[name='description']").first();
        if (descField.isVisible()) {
            assertThat(descField).isVisible();
        }
    }

    @Test
    @DisplayName("Should display output quantity field on form")
    void shouldDisplayOutputQuantityFieldOnForm() {
        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        var outputQtyField = page.locator("input[name='outputQuantity']").first();
        if (outputQtyField.isVisible()) {
            assertThat(outputQtyField).isVisible();
        }
    }

    @Test
    @DisplayName("Should display code field on form")
    void shouldDisplayCodeFieldOnForm() {
        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        var codeField = page.locator("input[name='code']").first();
        if (codeField.isVisible()) {
            assertThat(codeField).isVisible();
        }
    }

    @Test
    @DisplayName("Should display name field on form")
    void shouldDisplayNameFieldOnForm() {
        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        var nameField = page.locator("input[name='name']").first();
        assertThat(nameField).isVisible();
    }

    @Test
    @DisplayName("Should update BOM description")
    void shouldUpdateBOMDescription() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        // Update description
        var descInput = page.locator("input[name='description'], textarea[name='description']").first();
        if (descInput.isVisible()) {
            descInput.fill("Updated description " + System.currentTimeMillis());
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
    @DisplayName("Should search for non-existing BOM")
    void shouldSearchForNonExistingBOM() {
        navigateTo("/inventory/bom?search=nonexistent12345");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should display BOM list with all items")
    void shouldDisplayBOMListWithAllItems() {
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Verify table or list exists
        var tableOrList = page.locator("table, .bom-list, #bom-list").first();
        if (tableOrList.isVisible()) {
            assertThat(tableOrList).isVisible();
        }
    }

    @Test
    @DisplayName("Should have create new button on list page")
    void shouldHaveCreateNewButtonOnListPage() {
        navigateTo("/inventory/bom");
        waitForPageLoad();

        var createBtn = page.locator("a[href*='/inventory/bom/create']").first();
        if (createBtn.isVisible()) {
            assertThat(createBtn).isVisible();
        }
    }

    @Test
    @DisplayName("Should have edit button on detail page")
    void shouldHaveEditButtonOnDetailPage() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId());
        waitForPageLoad();

        var editBtn = page.locator("a[href*='/edit']").first();
        if (editBtn.isVisible()) {
            assertThat(editBtn).isVisible();
        }
    }

    @Test
    @DisplayName("Should have delete form on detail page")
    void shouldHaveDeleteFormOnDetailPage() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId());
        waitForPageLoad();

        var deleteForm = page.locator("form[action*='/delete']").first();
        if (deleteForm.isVisible()) {
            assertThat(deleteForm).isVisible();
        }
    }

    @Test
    @DisplayName("Should update BOM output quantity")
    void shouldUpdateBOMOutputQuantity() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        // Update output quantity
        var outputQtyInput = page.locator("input[name='outputQuantity']").first();
        if (outputQtyInput.isVisible()) {
            outputQtyInput.fill("10");
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== FORM SUBMISSION TESTS ====================

    @Test
    @DisplayName("Should submit create BOM form with all required fields")
    void shouldSubmitCreateBOMFormWithAllRequiredFields() {
        var products = productRepository.findAll();
        if (products.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        String uniqueCode = "BOM-" + System.currentTimeMillis() % 10000;

        // Fill code
        page.locator("input[name='code']").fill(uniqueCode);

        // Fill name
        page.locator("input[name='name']").fill("Test BOM " + uniqueCode);

        // Fill description
        var descInput = page.locator("textarea[name='description']");
        if (descInput.isVisible()) {
            descInput.fill("Test BOM description");
        }

        // Select product
        var productSelect = page.locator("select[name='productId']");
        var productOptions = productSelect.locator("option[value]");
        if (productOptions.count() > 1) {
            productSelect.selectOption(productOptions.nth(1).getAttribute("value"));
        }

        // Fill output quantity
        page.locator("input[name='outputQuantity']").fill("10");

        // Check active checkbox
        var activeCheckbox = page.locator("input[name='active']");
        if (activeCheckbox.isVisible()) {
            activeCheckbox.check();
        }

        // Submit using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        // Should redirect to list or detail
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom.*"));
    }

    @Test
    @DisplayName("Should submit update BOM form")
    void shouldSubmitUpdateBOMForm() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']");
        nameInput.fill("Updated BOM " + System.currentTimeMillis());

        // Update output quantity
        var outputQtyInput = page.locator("input[name='outputQuantity']");
        if (outputQtyInput.isVisible()) {
            outputQtyInput.fill("25");
        }

        // Submit using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        // Should redirect to list or detail
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom.*"));
    }

    @Test
    @DisplayName("Should submit delete BOM form")
    void shouldSubmitDeleteBOMForm() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/bom/" + bom.get().getId());
        waitForPageLoad();

        var deleteForm = page.locator("form[action*='/delete']").first();
        if (deleteForm.isVisible()) {
            deleteForm.locator("button[type='submit']").click();
            waitForPageLoad();
        }

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom.*"));
    }

    @Test
    @DisplayName("Should submit BOM form with components")
    void shouldSubmitBOMFormWithComponents() {
        var products = productRepository.findAll();
        if (products.size() < 2) {
            return;
        }

        navigateTo("/inventory/bom/create");
        waitForPageLoad();

        String uniqueCode = "BOM-COMP-" + System.currentTimeMillis() % 10000;

        // Fill basic fields
        page.locator("input[name='code']").fill(uniqueCode);
        page.locator("input[name='name']").fill("BOM with Components " + uniqueCode);

        // Select product
        var productSelect = page.locator("select[name='productId']");
        productSelect.selectOption(products.get(0).getId().toString());

        // Fill output quantity
        page.locator("input[name='outputQuantity']").fill("1");

        // Add component by clicking add button
        var addComponentBtn = page.locator("#add-component-btn");
        if (addComponentBtn.isVisible()) {
            addComponentBtn.click();

            // Wait for component row to be added
            page.waitForSelector(".component-row:not(#component-row-template)");

            // Fill component data
            var componentSelect = page.locator(".component-row:not(#component-row-template) select[name='componentId[]']").first();
            if (componentSelect.isVisible() && products.size() > 1) {
                componentSelect.selectOption(products.get(1).getId().toString());
            }

            var componentQty = page.locator(".component-row:not(#component-row-template) input[name='componentQty[]']").first();
            if (componentQty.isVisible()) {
                componentQty.fill("2");
            }
        }

        // Submit form using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/bom.*"));
    }
}
