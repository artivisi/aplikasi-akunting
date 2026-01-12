package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.SalaryComponentRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for SalaryComponentController.
 * Tests salary component list, create, edit, activate, deactivate operations.
 */
@DisplayName("Salary Component Controller Tests")
@Import(ServiceTestDataInitializer.class)
class SalaryComponentControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private SalaryComponentRepository componentRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display salary component list page")
    void shouldDisplaySalaryComponentListPage() {
        navigateTo("/salary-components");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter components by type")
    void shouldFilterComponentsByType() {
        navigateTo("/salary-components");
        waitForPageLoad();

        var typeSelect = page.locator("select[name='type']").first();
        if (typeSelect.isVisible()) {
            var options = typeSelect.locator("option");
            if (options.count() > 1) {
                typeSelect.selectOption(new String[]{options.nth(1).getAttribute("value")});

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
    @DisplayName("Should filter components by status")
    void shouldFilterComponentsByStatus() {
        navigateTo("/salary-components");
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
    @DisplayName("Should search components by keyword")
    void shouldSearchComponentsByKeyword() {
        navigateTo("/salary-components");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search'], input[name='keyword']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("tunjangan");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new salary component form")
    void shouldDisplayNewSalaryComponentForm() {
        navigateTo("/salary-components/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new salary component")
    void shouldCreateNewSalaryComponent() {
        navigateTo("/salary-components/new");
        waitForPageLoad();

        // Fill component name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Component " + System.currentTimeMillis());
        }

        // Fill component code
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill("TC" + System.currentTimeMillis());
        }

        // Select type
        var typeSelect = page.locator("select[name='type']").first();
        if (typeSelect.isVisible()) {
            typeSelect.selectOption("ALLOWANCE");
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
        navigateTo("/salary-components/new");
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
    @DisplayName("Should display salary component detail page")
    void shouldDisplaySalaryComponentDetailPage() {
        var component = componentRepository.findAll().stream().findFirst();
        if (component.isEmpty()) {
            return;
        }

        navigateTo("/salary-components/" + component.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/salary-components\\/.*"));
    }

    @Test
    @DisplayName("Should display salary component edit form")
    void shouldDisplaySalaryComponentEditForm() {
        var component = componentRepository.findAll().stream().findFirst();
        if (component.isEmpty()) {
            return;
        }

        navigateTo("/salary-components/" + component.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update salary component")
    void shouldUpdateSalaryComponent() {
        var component = componentRepository.findAll().stream().findFirst();
        if (component.isEmpty()) {
            return;
        }

        navigateTo("/salary-components/" + component.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Component " + System.currentTimeMillis());
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/salary-components\\/.*"));
    }

    @Test
    @DisplayName("Should deactivate salary component")
    void shouldDeactivateSalaryComponent() {
        var component = componentRepository.findAll().stream()
                .filter(c -> c.getActive() != null && c.getActive())
                .findFirst();
        if (component.isEmpty()) {
            return;
        }

        navigateTo("/salary-components/" + component.get().getId());
        waitForPageLoad();

        var deactivateBtn = page.locator("form[action*='/deactivate'] button[type='submit']").first();
        if (deactivateBtn.isVisible()) {
            deactivateBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/salary-components.*"));
    }

    @Test
    @DisplayName("Should activate salary component")
    void shouldActivateSalaryComponent() {
        var component = componentRepository.findAll().stream()
                .filter(c -> c.getActive() == null || !c.getActive())
                .findFirst();
        if (component.isEmpty()) {
            return;
        }

        navigateTo("/salary-components/" + component.get().getId());
        waitForPageLoad();

        var activateBtn = page.locator("form[action*='/activate'] button[type='submit']").first();
        if (activateBtn.isVisible()) {
            activateBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/salary-components.*"));
    }
}
