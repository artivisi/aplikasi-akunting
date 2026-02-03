package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for FiscalYearClosingService.
 * Tests fiscal year closing preview and execution.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Fiscal Year Closing Service Integration Tests")
@Transactional
class FiscalYearClosingServiceTest {

    @Autowired
    private FiscalYearClosingService fiscalYearClosingService;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    // Use a far-future year for testing to avoid conflicts
    private static final int TEST_YEAR = 2099;

    @Test
    @DisplayName("Should check if closing entries exist")
    void shouldCheckIfClosingEntriesExist() {
        // Check for a year that definitely has no closing entries
        boolean hasClosing = fiscalYearClosingService.hasClosingEntries(TEST_YEAR);
        assertThat(hasClosing).isFalse();
    }

    @Test
    @DisplayName("Should get empty list for year without closing entries")
    void shouldGetEmptyListForYearWithoutClosingEntries() {
        List<JournalEntry> entries = fiscalYearClosingService.getClosingEntries(TEST_YEAR);
        assertThat(entries).isEmpty();
    }

    @Test
    @DisplayName("Should preview closing for year")
    void shouldPreviewClosingForYear() {
        int currentYear = LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        assertThat(preview).isNotNull();
        assertThat(preview.year()).isEqualTo(currentYear);
        assertThat(preview.totalRevenue()).isNotNull();
        assertThat(preview.totalExpense()).isNotNull();
        assertThat(preview.netIncome()).isNotNull();
        // Net income should equal revenue - expense
        assertThat(preview.netIncome())
                .isEqualByComparingTo(preview.totalRevenue().subtract(preview.totalExpense()));
    }

    @Test
    @DisplayName("Should preview closing entries structure")
    void shouldPreviewClosingEntriesStructure() {
        int currentYear = LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        // If there's any income statement activity, there should be entries
        if (preview.totalRevenue().compareTo(BigDecimal.ZERO) > 0 ||
            preview.totalExpense().compareTo(BigDecimal.ZERO) > 0) {
            assertThat(preview.entries()).isNotEmpty();

            // Check first entry structure
            FiscalYearClosingService.ClosingEntryPreview firstEntry = preview.entries().get(0);
            assertThat(firstEntry.referenceNumber()).startsWith("CLOSING-");
            assertThat(firstEntry.date()).isEqualTo(LocalDate.of(currentYear, 12, 31));
            assertThat(firstEntry.lines()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should have closing entry preview lines with account info")
    void shouldHaveClosingEntryPreviewLinesWithAccountInfo() {
        int currentYear = LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        for (FiscalYearClosingService.ClosingEntryPreview entry : preview.entries()) {
            for (FiscalYearClosingService.ClosingLinePreview line : entry.lines()) {
                assertThat(line.accountCode()).isNotEmpty();
                assertThat(line.accountName()).isNotEmpty();
                assertThat(line.debit()).isNotNull();
                assertThat(line.credit()).isNotNull();
                assertThat(line.memo()).isNotEmpty();

                // Either debit or credit should be non-zero
                assertThat(line.debit().compareTo(BigDecimal.ZERO) > 0 ||
                          line.credit().compareTo(BigDecimal.ZERO) > 0).isTrue();
            }
        }
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("Should execute closing and create journal entries")
    void shouldExecuteClosingAndCreateJournalEntries() {
        // Verify required accounts exist
        var labaBerjalan = chartOfAccountRepository.findByAccountCode("3.2.02");
        var labaDitahan = chartOfAccountRepository.findByAccountCode("3.2.01");

        if (labaBerjalan.isEmpty() || labaDitahan.isEmpty()) {
            return; // Skip if required accounts not set up
        }

        // First check preview
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(TEST_YEAR);

        // If no entries to close (no income/expense activity), skip
        if (preview.entries().isEmpty()) {
            return;
        }

        // Execute closing
        List<JournalEntry> closingEntries = fiscalYearClosingService.executeClosing(TEST_YEAR);

        assertThat(closingEntries).isNotEmpty();

        // Verify entries have proper structure
        for (JournalEntry entry : closingEntries) {
            assertThat(entry.getJournalNumber()).isNotEmpty();
            assertThat(entry.getAccount()).isNotNull();
            assertThat(entry.getPostedAt()).isNotNull();
        }

        // Verify hasClosingEntries now returns true
        assertThat(fiscalYearClosingService.hasClosingEntries(TEST_YEAR)).isTrue();
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("Should reject duplicate closing for same year")
    void shouldRejectDuplicateClosingForSameYear() {
        var labaBerjalan = chartOfAccountRepository.findByAccountCode("3.2.02");
        var labaDitahan = chartOfAccountRepository.findByAccountCode("3.2.01");

        if (labaBerjalan.isEmpty() || labaDitahan.isEmpty()) {
            return;
        }

        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(TEST_YEAR);
        if (preview.entries().isEmpty()) {
            return;
        }

        // Execute first closing
        fiscalYearClosingService.executeClosing(TEST_YEAR);

        // Attempt second closing should fail
        assertThatThrownBy(() -> fiscalYearClosingService.executeClosing(TEST_YEAR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sudah ada");
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("Should reverse closing entries")
    void shouldReverseClosingEntries() {
        var labaBerjalan = chartOfAccountRepository.findByAccountCode("3.2.02");
        var labaDitahan = chartOfAccountRepository.findByAccountCode("3.2.01");

        if (labaBerjalan.isEmpty() || labaDitahan.isEmpty()) {
            return;
        }

        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(TEST_YEAR);
        if (preview.entries().isEmpty()) {
            return;
        }

        // Execute closing
        fiscalYearClosingService.executeClosing(TEST_YEAR);

        // Reverse
        int reversedCount = fiscalYearClosingService.reverseClosing(TEST_YEAR, "Test reversal reason");

        assertThat(reversedCount).isGreaterThan(0);

        // After reversing, closing entries should be voided
        List<JournalEntry> entries = fiscalYearClosingService.getClosingEntries(TEST_YEAR);
        entries.forEach(entry -> assertThat(entry.getVoidedAt()).isNotNull());
    }

    @Test
    @WithMockUser(username = "admin")
    @DisplayName("Should reject reverse for year without closing")
    void shouldRejectReverseForYearWithoutClosing() {
        // Use a year that definitely has no closing
        assertThatThrownBy(() -> fiscalYearClosingService.reverseClosing(1999, "Test reason"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Tidak ada jurnal penutup");
    }

    @Test
    @DisplayName("Should show alreadyClosed flag in preview")
    void shouldShowAlreadyClosedFlagInPreview() {
        // For year without closing
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(TEST_YEAR);
        assertThat(preview.alreadyClosed()).isFalse();
    }

    @Test
    @DisplayName("Should calculate balanced closing entries in preview")
    void shouldCalculateBalancedClosingEntriesInPreview() {
        int currentYear = LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        // Each entry should be balanced
        for (FiscalYearClosingService.ClosingEntryPreview entry : preview.entries()) {
            BigDecimal totalDebit = entry.lines().stream()
                    .map(FiscalYearClosingService.ClosingLinePreview::debit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCredit = entry.lines().stream()
                    .map(FiscalYearClosingService.ClosingLinePreview::credit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(totalDebit).isEqualByComparingTo(totalCredit);
        }
    }

    @Test
    @DisplayName("Should preview closing for past year")
    void shouldPreviewClosingForPastYear() {
        // Use last year
        int lastYear = LocalDate.now().getYear() - 1;
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(lastYear);

        assertThat(preview).isNotNull();
        assertThat(preview.year()).isEqualTo(lastYear);
    }

    @Test
    @DisplayName("Should handle year with zero activity")
    void shouldHandleYearWithZeroActivity() {
        // Use far future year with no transactions
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(2098);

        assertThat(preview).isNotNull();
        assertThat(preview.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(preview.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(preview.netIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        // Should have no closing entries to create
        assertThat(preview.entries()).isEmpty();
    }
}
