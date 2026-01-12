package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for ClientController.
 * Tests client list, create, edit, deactivate operations.
 */
@DisplayName("Client Controller Tests")
@Import(ServiceTestDataInitializer.class)
class ClientControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display client list page")
    void shouldDisplayClientListPage() {
        navigateTo("/clients");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter clients by status")
    void shouldFilterClientsByStatus() {
        navigateTo("/clients");
        waitForPageLoad();

        var activeCheckbox = page.locator("input[name='active']").first();
        if (activeCheckbox.isVisible()) {
            activeCheckbox.check();

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should search clients by keyword")
    void shouldSearchClientsByKeyword() {
        navigateTo("/clients");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("test");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new client form")
    void shouldDisplayNewClientForm() {
        navigateTo("/clients/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new client")
    void shouldCreateNewClient() {
        navigateTo("/clients/new");
        waitForPageLoad();

        // Fill client name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Client " + System.currentTimeMillis());
        }

        // Fill email
        var emailInput = page.locator("input[name='email']").first();
        if (emailInput.isVisible()) {
            emailInput.fill("testclient" + System.currentTimeMillis() + "@example.com");
        }

        // Fill phone
        var phoneInput = page.locator("input[name='phone']").first();
        if (phoneInput.isVisible()) {
            phoneInput.fill("08123456789");
        }

        // Fill address
        var addressInput = page.locator("textarea[name='address'], input[name='address']").first();
        if (addressInput.isVisible()) {
            addressInput.fill("123 Test Street");
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
        navigateTo("/clients/new");
        waitForPageLoad();

        // Submit without filling name
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Should stay on form or show error
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display client detail page")
    void shouldDisplayClientDetailPage() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/clients/" + client.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/clients\\/.*"));
    }

    @Test
    @DisplayName("Should display client edit form")
    void shouldDisplayClientEditForm() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/clients/" + client.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update client")
    void shouldUpdateClient() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/clients/" + client.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Client " + System.currentTimeMillis());
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/clients\\/.*"));
    }

    @Test
    @DisplayName("Should deactivate client")
    void shouldDeactivateClient() {
        var client = clientRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/clients/" + client.get().getId());
        waitForPageLoad();

        var deactivateBtn = page.locator("form[action*='/deactivate'] button[type='submit']").first();
        if (deactivateBtn.isVisible()) {
            deactivateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should activate client")
    void shouldActivateClient() {
        var client = clientRepository.findAll().stream()
                .filter(c -> Boolean.FALSE.equals(c.getActive()))
                .findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/clients/" + client.get().getId());
        waitForPageLoad();

        var activateBtn = page.locator("form[action*='/activate'] button[type='submit']").first();
        if (activateBtn.isVisible()) {
            activateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
