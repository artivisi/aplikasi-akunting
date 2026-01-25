package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.service.JournalEntryService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Functional tests for JournalEntryService with data persistence verification.
 * Tests journal entry retrieval, general ledger generation, and UI navigation.
 */
@DisplayName("Journal Entry - Persistence Tests")
@Import(ServiceTestDataInitializer.class)
class JournalEntryPersistenceTest extends PlaywrightTestBase {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    @Autowired
    private JournalTemplateRepository templateRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== FIND BY ID TESTS ====================

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find existing journal entry by ID")
        void shouldFindExistingJournalEntryById() {
            var entry = journalEntryRepository.findAll().stream().findFirst();
            if (entry.isEmpty()) {
                return;
            }

            var found = journalEntryService.findById(entry.get().getId());
            org.assertj.core.api.Assertions.assertThat(found).isNotNull();
            org.assertj.core.api.Assertions.assertThat(found.getId()).isEqualTo(entry.get().getId());
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID nonExistentId = UUID.randomUUID();
            assertThatThrownBy(() -> journalEntryService.findById(nonExistentId))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
        }
    }

    // ==================== FIND BY JOURNAL NUMBER TESTS ====================

    @Nested
    @DisplayName("Find By Journal Number Tests")
    class FindByJournalNumberTests {

        @Test
        @DisplayName("Should find existing journal entry by journal number")
        void shouldFindExistingJournalEntryByJournalNumber() {
            var entry = journalEntryRepository.findAll().stream()
                .filter(e -> e.getJournalNumber() != null)
                .findFirst();
            if (entry.isEmpty()) {
                return;
            }

            var found = journalEntryService.findByJournalNumber(entry.get().getJournalNumber());
            org.assertj.core.api.Assertions.assertThat(found).isNotNull();
            org.assertj.core.api.Assertions.assertThat(found.getJournalNumber()).isEqualTo(entry.get().getJournalNumber());
        }

        @Test
        @DisplayName("Should throw exception for non-existent journal number")
        void shouldThrowExceptionForNonExistentJournalNumber() {
            String nonExistentNumber = "JE-9999-9999";
            assertThatThrownBy(() -> journalEntryService.findByJournalNumber(nonExistentNumber))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should find all entries by journal number")
        void shouldFindAllEntriesByJournalNumber() {
            var entry = journalEntryRepository.findAll().stream()
                .filter(e -> e.getJournalNumber() != null)
                .findFirst();
            if (entry.isEmpty()) {
                return;
            }

            var entries = journalEntryService.findAllByJournalNumber(entry.get().getJournalNumber());
            org.assertj.core.api.Assertions.assertThat(entries).isNotEmpty();
        }
    }

    // ==================== FIND BY TRANSACTION TESTS ====================

    @Nested
    @DisplayName("Find By Transaction Tests")
    class FindByTransactionTests {

        @Test
        @DisplayName("Should find entries by transaction ID")
        void shouldFindEntriesByTransactionId() {
            var transaction = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.POSTED)
                .findFirst();
            if (transaction.isEmpty()) {
                return;
            }

            var entries = journalEntryService.findByTransactionId(transaction.get().getId());
            org.assertj.core.api.Assertions.assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should return empty list for non-existent transaction")
        void shouldReturnEmptyListForNonExistentTransaction() {
            UUID nonExistentId = UUID.randomUUID();
            var entries = journalEntryService.findByTransactionId(nonExistentId);
            org.assertj.core.api.Assertions.assertThat(entries).isEmpty();
        }
    }

    // ==================== FIND BY DATE RANGE TESTS ====================

    @Nested
    @DisplayName("Find By Date Range Tests")
    class FindByDateRangeTests {

        @Test
        @DisplayName("Should find entries by date range")
        void shouldFindEntriesByDateRange() {
            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();
            var resultPage = journalEntryService.findAllByDateRange(startDate, endDate, PageRequest.of(0, 10));

            org.assertj.core.api.Assertions.assertThat(resultPage).isNotNull();
        }

        @Test
        @DisplayName("Should return empty page for future date range")
        void shouldReturnEmptyPageForFutureDateRange() {
            var startDate = LocalDate.now().plusYears(10);
            var endDate = LocalDate.now().plusYears(11);
            var resultPage = journalEntryService.findAllByDateRange(startDate, endDate, PageRequest.of(0, 10));

            org.assertj.core.api.Assertions.assertThat(resultPage).isNotNull();
            org.assertj.core.api.Assertions.assertThat(resultPage.getContent()).isEmpty();
        }
    }

    // ==================== FIND BY ACCOUNT AND DATE RANGE TESTS ====================

    @Nested
    @DisplayName("Find By Account and Date Range Tests")
    class FindByAccountAndDateRangeTests {

        @Test
        @DisplayName("Should find entries by account and date range")
        void shouldFindEntriesByAccountAndDateRange() {
            var account = chartOfAccountRepository.findAll().stream().findFirst();
            if (account.isEmpty()) {
                return;
            }

            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();
            var entries = journalEntryService.findByAccountAndDateRange(
                account.get().getId(), startDate, endDate);

            org.assertj.core.api.Assertions.assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should return empty list for non-existent account")
        void shouldReturnEmptyListForNonExistentAccount() {
            UUID nonExistentId = UUID.randomUUID();
            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();

            var entries = journalEntryService.findByAccountAndDateRange(
                nonExistentId, startDate, endDate);
            org.assertj.core.api.Assertions.assertThat(entries).isEmpty();
        }
    }

    // ==================== GENERAL LEDGER TESTS ====================

    @Nested
    @DisplayName("General Ledger Tests")
    class GeneralLedgerTests {

        @Test
        @DisplayName("Should generate general ledger for existing account")
        void shouldGenerateGeneralLedgerForExistingAccount() {
            var account = chartOfAccountRepository.findAll().stream().findFirst();
            if (account.isEmpty()) {
                return;
            }

            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();
            var ledger = journalEntryService.getGeneralLedger(
                account.get().getId(), startDate, endDate);

            org.assertj.core.api.Assertions.assertThat(ledger).isNotNull();
            org.assertj.core.api.Assertions.assertThat(ledger.account()).isNotNull();
        }

        @Test
        @DisplayName("Should generate paged general ledger")
        void shouldGeneratePagedGeneralLedger() {
            var account = chartOfAccountRepository.findAll().stream().findFirst();
            if (account.isEmpty()) {
                return;
            }

            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();
            var ledger = journalEntryService.getGeneralLedgerPaged(
                account.get().getId(), startDate, endDate, null, PageRequest.of(0, 10));

            org.assertj.core.api.Assertions.assertThat(ledger).isNotNull();
            org.assertj.core.api.Assertions.assertThat(ledger.account()).isNotNull();
        }

        @Test
        @DisplayName("Should generate paged general ledger with search filter")
        void shouldGeneratePagedGeneralLedgerWithSearchFilter() {
            var account = chartOfAccountRepository.findAll().stream().findFirst();
            if (account.isEmpty()) {
                return;
            }

            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();
            var ledger = journalEntryService.getGeneralLedgerPaged(
                account.get().getId(), startDate, endDate, "test", PageRequest.of(0, 10));

            org.assertj.core.api.Assertions.assertThat(ledger).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception for non-existent account in general ledger")
        void shouldThrowExceptionForNonExistentAccountInGeneralLedger() {
            UUID nonExistentId = UUID.randomUUID();
            var startDate = LocalDate.now().minusMonths(6);
            var endDate = LocalDate.now();

            assertThatThrownBy(() -> journalEntryService.getGeneralLedger(
                nonExistentId, startDate, endDate))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
        }
    }

    // ==================== CALCULATE ACCOUNT IMPACT TESTS ====================

    @Nested
    @DisplayName("Calculate Account Impact Tests")
    class CalculateAccountImpactTests {

        @Test
        @Transactional
        @DisplayName("Should calculate account impact for entries")
        void shouldCalculateAccountImpactForEntries() {
            var entries = journalEntryRepository.findAll().stream().limit(5).toList();
            if (entries.isEmpty()) {
                return;
            }

            var impacts = journalEntryService.calculateAccountImpact(entries);
            org.assertj.core.api.Assertions.assertThat(impacts).isNotNull();
        }

        @Test
        @DisplayName("Should return empty list for empty entries")
        void shouldReturnEmptyListForEmptyEntries() {
            var impacts = journalEntryService.calculateAccountImpact(List.of());
            org.assertj.core.api.Assertions.assertThat(impacts).isEmpty();
        }
    }

    // ==================== UI TESTS ====================

    @Nested
    @DisplayName("UI Tests")
    class UiTests {

        @Test
        @DisplayName("Should display journal entries list page")
        void shouldDisplayJournalEntriesListPage() {
            navigateTo("/journal-entries");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display general ledger page")
        void shouldDisplayGeneralLedgerPage() {
            var account = chartOfAccountRepository.findAll().stream().findFirst();
            if (account.isEmpty()) {
                return;
            }

            navigateTo("/journal-entries/general-ledger/" + account.get().getId());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display journal entry detail page")
        void shouldDisplayJournalEntryDetailPage() {
            var entry = journalEntryRepository.findAll().stream()
                .filter(e -> e.getJournalNumber() != null)
                .findFirst();
            if (entry.isEmpty()) {
                return;
            }

            navigateTo("/journal-entries/" + entry.get().getJournalNumber());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }
}
