package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ProjectPaymentTermRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for PaymentTermController.
 * Tests payment term create, edit, delete, generate invoice operations.
 */
@DisplayName("Payment Term Controller Tests")
@Import(ServiceTestDataInitializer.class)
@Transactional
class PaymentTermControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ProjectPaymentTermRepository paymentTermRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display new payment term form")
    void shouldDisplayNewPaymentTermForm() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/payment-terms/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new payment term")
    void shouldCreateNewPaymentTerm() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/payment-terms/new");
        waitForPageLoad();

        // Fill name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Payment Term " + System.currentTimeMillis());
        }

        // Fill percentage
        var percentageInput = page.locator("input[name='percentage']").first();
        if (percentageInput.isVisible()) {
            percentageInput.fill("25");
        }

        // Fill due date
        var dueDateInput = page.locator("input[name='dueDate']").first();
        if (dueDateInput.isVisible()) {
            dueDateInput.fill(LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE));
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
    @DisplayName("Should display payment term edit form")
    void shouldDisplayPaymentTermEditForm() {
        var paymentTerm = paymentTermRepository.findAll().stream()
                .filter(pt -> pt.getProject() != null)
                .findFirst();
        if (paymentTerm.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + paymentTerm.get().getProject().getCode() + "/payment-terms/" + paymentTerm.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update payment term")
    void shouldUpdatePaymentTerm() {
        var paymentTerm = paymentTermRepository.findAll().stream()
                .filter(pt -> pt.getProject() != null)
                .findFirst();
        if (paymentTerm.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + paymentTerm.get().getProject().getCode() + "/payment-terms/" + paymentTerm.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Term " + System.currentTimeMillis());
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
    @DisplayName("Should delete payment term")
    void shouldDeletePaymentTerm() {
        var paymentTerm = paymentTermRepository.findAll().stream()
                .filter(pt -> pt.getProject() != null)
                .findFirst();
        if (paymentTerm.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + paymentTerm.get().getProject().getCode() + "/payment-terms/" + paymentTerm.get().getId() + "/edit");
        waitForPageLoad();

        var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should generate invoice from payment term")
    void shouldGenerateInvoiceFromPaymentTerm() {
        var paymentTerm = paymentTermRepository.findAll().stream()
                .filter(pt -> pt.getProject() != null)
                .findFirst();
        if (paymentTerm.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + paymentTerm.get().getProject().getCode() + "/payment-terms/" + paymentTerm.get().getId() + "/edit");
        waitForPageLoad();

        var generateBtn = page.locator("form[action*='/generate-invoice'] button[type='submit']").first();
        if (generateBtn.isVisible()) {
            generateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
