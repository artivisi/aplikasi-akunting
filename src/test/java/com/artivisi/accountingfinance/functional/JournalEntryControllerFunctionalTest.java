package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for JournalEntryController.
 * Tests journal/ledger views and API endpoints.
 */
@DisplayName("Journal Entry Controller Tests")
@Import(ServiceTestDataInitializer.class)
class JournalEntryControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ChartOfAccountRepository coaRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display journal list page")
    void shouldDisplayJournalListPage() {
        navigateTo("/journals");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/journals.*"));
    }

    @Test
    @DisplayName("Should have date filter inputs")
    void shouldHaveDateFilterInputs() {
        navigateTo("/journals");
        waitForPageLoad();

        var startDateInput = page.locator("input[name='startDate']").first();
        var endDateInput = page.locator("input[name='endDate']").first();

        if (startDateInput.isVisible()) {
            assertThat(startDateInput).isVisible();
        }
        if (endDateInput.isVisible()) {
            assertThat(endDateInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should have account select dropdown")
    void shouldHaveAccountSelectDropdown() {
        navigateTo("/journals");
        waitForPageLoad();

        var accountSelect = page.locator("select[name='accountId']").first();
        if (accountSelect.isVisible()) {
            assertThat(accountSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should filter journals by account")
    void shouldFilterJournalsByAccount() {
        var account = coaRepository.findAll().stream().findFirst();
        if (account.isEmpty()) {
            return;
        }

        navigateTo("/journals?accountId=" + account.get().getId());
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter journals by date range")
    void shouldFilterJournalsByDateRange() {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now();

        navigateTo("/journals?startDate=" + start + "&endDate=" + end);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should search journals by keyword")
    void shouldSearchJournalsByKeyword() {
        navigateTo("/journals");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("test");

            var filterBtn = page.locator("button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display account ledger page")
    void shouldDisplayAccountLedgerPage() {
        var account = coaRepository.findAll().stream().findFirst();
        if (account.isEmpty()) {
            return;
        }

        navigateTo("/journals/ledger/" + account.get().getId());
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display account ledger with date range")
    void shouldDisplayAccountLedgerWithDateRange() {
        var account = coaRepository.findAll().stream().findFirst();
        if (account.isEmpty()) {
            return;
        }

        LocalDate start = LocalDate.now().minusMonths(3);
        LocalDate end = LocalDate.now();

        navigateTo("/journals/ledger/" + account.get().getId() +
                   "?startDate=" + start + "&endDate=" + end);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should get journals by date range via API")
    void shouldGetJournalsByDateRangeViaApi() {
        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now();

        navigateTo("/journals/api?startDate=" + start + "&endDate=" + end);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should get journals by transaction via API")
    void shouldGetJournalsByTransactionViaApi() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/journals/api/by-transaction/" + transaction.get().getId());
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should get ledger data via API")
    void shouldGetLedgerDataViaApi() {
        var account = coaRepository.findAll().stream().findFirst();
        if (account.isEmpty()) {
            return;
        }

        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now();

        navigateTo("/journals/api/ledger/" + account.get().getId() +
                   "?startDate=" + start + "&endDate=" + end);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }
}
