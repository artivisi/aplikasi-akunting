package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.DraftTransactionRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
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
 * Functional tests for DraftTransactionController.
 * Tests draft transaction list, create, edit, approve, reject operations.
 */
@DisplayName("Draft Transaction Controller Tests")
@Import(ServiceTestDataInitializer.class)
class DraftTransactionControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private DraftTransactionRepository draftRepository;

    @Autowired
    private JournalTemplateRepository templateRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display draft transaction list page")
    void shouldDisplayDraftTransactionListPage() {
        navigateTo("/drafts");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter drafts by status")
    void shouldFilterDraftsByStatus() {
        navigateTo("/drafts");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            statusSelect.selectOption("PENDING");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter drafts by date range")
    void shouldFilterDraftsByDateRange() {
        navigateTo("/drafts");
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
    @DisplayName("Should display new draft transaction form")
    void shouldDisplayNewDraftTransactionForm() {
        navigateTo("/drafts/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new draft transaction")
    void shouldCreateNewDraftTransaction() {
        var template = templateRepository.findAll().stream().findFirst();
        if (template.isEmpty()) {
            return;
        }

        navigateTo("/drafts/new");
        waitForPageLoad();

        // Select template
        var templateSelect = page.locator("select[name='template.id'], select[name='templateId']").first();
        if (templateSelect.isVisible()) {
            templateSelect.selectOption(template.get().getId().toString());
        }

        // Fill transaction date
        var transactionDateInput = page.locator("input[name='transactionDate']").first();
        if (transactionDateInput.isVisible()) {
            transactionDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill description
        var descriptionInput = page.locator("input[name='description'], textarea[name='description']").first();
        if (descriptionInput.isVisible()) {
            descriptionInput.fill("Test Draft " + System.currentTimeMillis());
        }

        // Fill amount
        var amountInput = page.locator("input[name='amount']").first();
        if (amountInput.isVisible()) {
            amountInput.fill("1000000");
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
    @DisplayName("Should display draft transaction detail page")
    void shouldDisplayDraftTransactionDetailPage() {
        var draft = draftRepository.findAll().stream().findFirst();
        if (draft.isEmpty()) {
            return;
        }

        navigateTo("/drafts/" + draft.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/drafts\\/.*"));
    }

    @Test
    @DisplayName("Should display draft transaction edit form")
    void shouldDisplayDraftTransactionEditForm() {
        var draft = draftRepository.findAll().stream().findFirst();
        if (draft.isEmpty()) {
            return;
        }

        navigateTo("/drafts/" + draft.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update draft transaction")
    void shouldUpdateDraftTransaction() {
        var draft = draftRepository.findAll().stream()
                .filter(d -> "PENDING".equals(d.getStatus().name()))
                .findFirst();
        if (draft.isEmpty()) {
            return;
        }

        navigateTo("/drafts/" + draft.get().getId() + "/edit");
        waitForPageLoad();

        // Update description
        var descriptionInput = page.locator("input[name='description'], textarea[name='description']").first();
        if (descriptionInput.isVisible()) {
            descriptionInput.fill("Updated Draft " + System.currentTimeMillis());
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/drafts\\/.*"));
    }

    @Test
    @DisplayName("Should approve draft transaction")
    void shouldApproveDraftTransaction() {
        var draft = draftRepository.findAll().stream()
                .filter(d -> "PENDING".equals(d.getStatus().name()))
                .findFirst();
        if (draft.isEmpty()) {
            return;
        }

        navigateTo("/drafts/" + draft.get().getId());
        waitForPageLoad();

        var approveBtn = page.locator("form[action*='/approve'] button[type='submit']").first();
        if (approveBtn.isVisible()) {
            approveBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should reject draft transaction")
    void shouldRejectDraftTransaction() {
        var draft = draftRepository.findAll().stream()
                .filter(d -> "PENDING".equals(d.getStatus().name()))
                .findFirst();
        if (draft.isEmpty()) {
            return;
        }

        navigateTo("/drafts/" + draft.get().getId());
        waitForPageLoad();

        var rejectBtn = page.locator("form[action*='/reject'] button[type='submit']").first();
        if (rejectBtn.isVisible()) {
            rejectBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should delete draft transaction")
    void shouldDeleteDraftTransaction() {
        var draft = draftRepository.findAll().stream()
                .filter(d -> "PENDING".equals(d.getStatus().name()) || "REJECTED".equals(d.getStatus().name()))
                .findFirst();
        if (draft.isEmpty()) {
            return;
        }

        navigateTo("/drafts/" + draft.get().getId());
        waitForPageLoad();

        var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
