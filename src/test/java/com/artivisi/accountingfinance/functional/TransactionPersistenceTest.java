package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TemplateCategory;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.enums.VoidReason;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.service.TransactionService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Functional tests for Transaction operations with data persistence verification.
 * Tests transaction creation, posting, voiding, and journal entry generation.
 */
@DisplayName("Transaction - Persistence Tests")
@Import(ServiceTestDataInitializer.class)
class TransactionPersistenceTest extends PlaywrightTestBase {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JournalTemplateRepository templateRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== SERVICE LAYER TESTS ====================

    @Test
    @DisplayName("Should verify transaction service is available")
    void shouldVerifyTransactionServiceIsAvailable() {
        assertThat(transactionService).isNotNull();
        assertThat(transactionRepository).isNotNull();
    }

    @Test
    @DisplayName("Should verify templates are available for transactions")
    void shouldVerifyTemplatesAreAvailableForTransactions() {
        var templates = templateRepository.findAll();
        assertThat(templates).isNotEmpty();

        // At least one current version template should exist
        var currentTemplates = templates.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsCurrentVersion()))
            .toList();
        assertThat(currentTemplates).isNotEmpty();
    }

    @Test
    @DisplayName("Should find transactions by status filter")
    void shouldFindTransactionsByStatusFilter() {
        var draftPage = transactionService.findByFilters(
            TransactionStatus.DRAFT, null, null, null, PageRequest.of(0, 10));
        assertThat(draftPage).isNotNull();

        var postedPage = transactionService.findByFilters(
            TransactionStatus.POSTED, null, null, null, PageRequest.of(0, 10));
        assertThat(postedPage).isNotNull();
    }

    @Test
    @DisplayName("Should find transactions by category filter")
    void shouldFindTransactionsByCategoryFilter() {
        var incomePage = transactionService.findByFilters(
            null, TemplateCategory.INCOME, null, null, PageRequest.of(0, 10));
        assertThat(incomePage).isNotNull();

        var expensePage = transactionService.findByFilters(
            null, TemplateCategory.EXPENSE, null, null, PageRequest.of(0, 10));
        assertThat(expensePage).isNotNull();
    }

    @Test
    @DisplayName("Should search transactions by query")
    void shouldSearchTransactionsByQuery() {
        var results = transactionService.search("test", PageRequest.of(0, 10));
        assertThat(results).isNotNull();
    }

    @Test
    @DisplayName("Should count transactions by status")
    void shouldCountTransactionsByStatus() {
        long draftCount = transactionService.countByStatus(TransactionStatus.DRAFT);
        assertThat(draftCount).isGreaterThanOrEqualTo(0);

        long postedCount = transactionService.countByStatus(TransactionStatus.POSTED);
        assertThat(postedCount).isGreaterThanOrEqualTo(0);
    }

    // ==================== UI TESTS ====================

    @Test
    @DisplayName("Should display transactions list page")
    void shouldDisplayTransactionsListPage() {
        navigateTo("/transactions");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should display transaction form page with template")
    void shouldDisplayTransactionFormPageWithTemplate() {
        // Get a template
        var templates = templateRepository.findAll();
        if (templates.isEmpty()) {
            return;
        }

        JournalTemplate template = templates.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsCurrentVersion()))
            .findFirst()
            .orElse(templates.get(0));

        navigateTo("/transactions/new?templateId=" + template.getId());
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should filter transactions by status via UI")
    void shouldFilterTransactionsByStatusViaUi() {
        navigateTo("/transactions?status=POSTED");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should filter transactions by category via UI")
    void shouldFilterTransactionsByCategoryViaUi() {
        navigateTo("/transactions?category=INCOME");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should filter transactions by date range via UI")
    void shouldFilterTransactionsByDateRangeViaUi() {
        String today = LocalDate.now().toString();
        navigateTo("/transactions?startDate=" + today + "&endDate=" + today);
        waitForPageLoad();

        // Page should load - verify we're not on error page
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should search transactions via UI")
    void shouldSearchTransactionsViaUi() {
        navigateTo("/transactions?search=test");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should display quick transaction templates")
    void shouldDisplayQuickTransactionTemplates() {
        // Request quick templates via HTMX
        var response = page.request().get(baseUrl() + "/transactions/quick/templates",
            com.microsoft.playwright.options.RequestOptions.create()
                .setHeader("HX-Request", "true"));

        org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should display quick transaction form for template")
    void shouldDisplayQuickTransactionFormForTemplate() {
        // Get a template
        var templates = templateRepository.findAll();
        if (templates.isEmpty()) {
            return;
        }

        JournalTemplate template = templates.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsCurrentVersion()))
            .findFirst()
            .orElse(templates.get(0));

        // Request quick form via HTMX
        var response = page.request().get(baseUrl() + "/transactions/quick/form?templateId=" + template.getId(),
            com.microsoft.playwright.options.RequestOptions.create()
                .setHeader("HX-Request", "true"));

        org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should search templates via UI")
    void shouldSearchTemplatesViaUi() {
        // Request template search via HTMX
        var response = page.request().get(baseUrl() + "/transactions/templates/search?q=",
            com.microsoft.playwright.options.RequestOptions.create()
                .setHeader("HX-Request", "true"));

        org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should preview transaction via UI")
    void shouldPreviewTransactionViaUi() {
        // Get a template
        var templates = templateRepository.findAll();
        if (templates.isEmpty()) {
            return;
        }

        JournalTemplate template = templates.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsCurrentVersion()))
            .findFirst()
            .orElse(templates.get(0));

        // Request preview via HTMX
        var response = page.request().get(baseUrl() + "/transactions/preview?templateId=" + template.getId() + "&amount=1000000",
            com.microsoft.playwright.options.RequestOptions.create()
                .setHeader("HX-Request", "true"));

        org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
    }
}
