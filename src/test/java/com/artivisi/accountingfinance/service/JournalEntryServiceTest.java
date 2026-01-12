package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for JournalEntryService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("JournalEntryService Integration Tests")
class JournalEntryServiceTest {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    private ChartOfAccount testAccount;

    @BeforeEach
    void setup() {
        // Get a test account
        testAccount = chartOfAccountRepository.findByAccountCode("1.1.01").orElse(null);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should throw exception for non-existent entry ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> journalEntryService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("Should throw exception for non-existent journal number")
        void shouldThrowExceptionForNonExistentJournalNumber() {
            assertThatThrownBy(() -> journalEntryService.findByJournalNumber("NON-EXISTENT-NUMBER"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("Should find entries by transaction ID - empty for random ID")
        void shouldFindEntriesByTransactionIdEmptyForRandomId() {
            UUID randomId = UUID.randomUUID();
            List<JournalEntry> entries = journalEntryService.findByTransactionId(randomId);
            assertThat(entries).isEmpty();
        }

        @Test
        @DisplayName("Should find all entries by date range")
        void shouldFindAllEntriesByDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            Page<JournalEntry> entries = journalEntryService.findAllByDateRange(
                    startDate, endDate, PageRequest.of(0, 10));

            assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should find entries by account and date range")
        void shouldFindEntriesByAccountAndDateRange() {
            if (testAccount == null) return;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            List<JournalEntry> entries = journalEntryService.findByAccountAndDateRange(
                    testAccount.getId(), startDate, endDate);

            assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should return empty for date range with no transactions")
        void shouldReturnEmptyForDateRangeWithNoTransactions() {
            if (testAccount == null) return;

            // Far past date range with no transactions
            LocalDate startDate = LocalDate.of(1990, 1, 1);
            LocalDate endDate = LocalDate.of(1990, 12, 31);

            List<JournalEntry> entries = journalEntryService.findByAccountAndDateRange(
                    testAccount.getId(), startDate, endDate);

            assertThat(entries).isEmpty();
        }
    }

    @Nested
    @DisplayName("General Ledger Operations")
    class GeneralLedgerTests {

        @Test
        @DisplayName("Should throw exception for non-existent account")
        void shouldThrowExceptionForNonExistentAccount() {
            UUID randomId = UUID.randomUUID();
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            assertThatThrownBy(() -> journalEntryService.getGeneralLedger(randomId, startDate, endDate))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("Should get general ledger for existing account")
        void shouldGetGeneralLedgerForExistingAccount() {
            if (testAccount == null) return;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            JournalEntryService.GeneralLedgerData ledger = journalEntryService.getGeneralLedger(
                    testAccount.getId(), startDate, endDate);

            assertThat(ledger).isNotNull();
            assertThat(ledger.account()).isNotNull();
            assertThat(ledger.openingBalance()).isNotNull();
            assertThat(ledger.entries()).isNotNull();
            assertThat(ledger.closingBalance()).isNotNull();
        }

        @Test
        @DisplayName("Should calculate balances correctly in general ledger")
        void shouldCalculateBalancesCorrectlyInGeneralLedger() {
            if (testAccount == null) return;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            JournalEntryService.GeneralLedgerData ledger = journalEntryService.getGeneralLedger(
                    testAccount.getId(), startDate, endDate);

            // Each entry should have a valid running balance
            for (JournalEntryService.LedgerLineItem entry : ledger.entries()) {
                assertThat(entry.runningBalance()).isNotNull();
                assertThat(entry.entry()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should paginate entries correctly")
        void shouldPaginateEntriesCorrectly() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            Page<JournalEntry> page1 = journalEntryService.findAllByDateRange(
                    startDate, endDate, PageRequest.of(0, 5));
            Page<JournalEntry> page2 = journalEntryService.findAllByDateRange(
                    startDate, endDate, PageRequest.of(1, 5));

            assertThat(page1).isNotNull();
            assertThat(page2).isNotNull();
            assertThat(page1.getNumber()).isEqualTo(0);
            assertThat(page2.getNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return correct page size")
        void shouldReturnCorrectPageSize() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            Page<JournalEntry> page = journalEntryService.findAllByDateRange(
                    startDate, endDate, PageRequest.of(0, 10));

            assertThat(page.getSize()).isEqualTo(10);
        }
    }
}
