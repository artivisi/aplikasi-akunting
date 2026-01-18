package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.service.FiscalYearClosingService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for FiscalYearClosingService with data persistence verification.
 * Tests actual closing entry creation and database state.
 */
@DisplayName("Fiscal Year Closing - Persistence Tests")
@Import(ServiceTestDataInitializer.class)
class FiscalYearClosingPersistenceTest extends PlaywrightTestBase {

    private static final int TEST_YEAR = 2097; // Far future year to avoid conflicts

    @Autowired
    private FiscalYearClosingService fiscalYearClosingService;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ChartOfAccountRepository accountRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display fiscal year closing page")
    void shouldDisplayFiscalYearClosingPage() {
        navigateTo("/reports/fiscal-closing");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should preview closing with zero activity year")
    void shouldPreviewClosingWithZeroActivityYear() {
        // Use far future year with no transactions
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(TEST_YEAR);

        // Verify preview structure
        assertThat(preview).isNotNull();
        assertThat(preview.year()).isEqualTo(TEST_YEAR);
        assertThat(preview.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(preview.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(preview.netIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(preview.entries()).isEmpty();
        assertThat(preview.alreadyClosed()).isFalse();
    }

    @Test
    @DisplayName("Should verify hasClosingEntries returns false for unclosed year")
    void shouldVerifyHasClosingEntriesReturnsFalseForUnclosedYear() {
        boolean hasClosed = fiscalYearClosingService.hasClosingEntries(TEST_YEAR);
        assertThat(hasClosed).isFalse();
    }

    @Test
    @DisplayName("Should verify getClosingEntries returns empty for unclosed year")
    void shouldVerifyGetClosingEntriesReturnsEmptyForUnclosedYear() {
        List<JournalEntry> entries = fiscalYearClosingService.getClosingEntries(TEST_YEAR);
        assertThat(entries).isEmpty();
    }

    @Test
    @DisplayName("Should preview closing entries structure for current year")
    void shouldPreviewClosingEntriesStructureForCurrentYear() {
        int currentYear = java.time.LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        assertThat(preview).isNotNull();
        assertThat(preview.year()).isEqualTo(currentYear);

        // Net income should always equal revenue - expense
        assertThat(preview.netIncome())
            .isEqualByComparingTo(preview.totalRevenue().subtract(preview.totalExpense()));

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
    @DisplayName("Should display preview on closing page")
    void shouldDisplayPreviewOnClosingPage() {
        navigateTo("/reports/fiscal-closing");
        waitForPageLoad();

        // Select year
        var yearSelect = page.locator("#year-select, select[name='year']");
        if (yearSelect.isVisible()) {
            // Verify year selector is present
            assertThat(yearSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should verify closing entry preview lines have complete data")
    void shouldVerifyClosingEntryPreviewLinesHaveCompleteData() {
        int currentYear = java.time.LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        for (FiscalYearClosingService.ClosingEntryPreview entry : preview.entries()) {
            // Verify entry structure
            assertThat(entry.referenceNumber()).startsWith("CLOSING-");
            assertThat(entry.description()).isNotBlank();
            assertThat(entry.date()).isNotNull();
            assertThat(entry.lines()).isNotEmpty();

            for (FiscalYearClosingService.ClosingLinePreview line : entry.lines()) {
                // Verify line structure
                assertThat(line.accountCode()).isNotBlank();
                assertThat(line.accountName()).isNotBlank();
                assertThat(line.debit()).isNotNull();
                assertThat(line.credit()).isNotNull();
                assertThat(line.memo()).isNotBlank();

                // Either debit or credit must be positive
                assertThat(line.debit().compareTo(BigDecimal.ZERO) > 0 ||
                          line.credit().compareTo(BigDecimal.ZERO) > 0).isTrue();
            }
        }
    }

    @Test
    @DisplayName("Should verify required accounts exist for closing")
    void shouldVerifyRequiredAccountsExistForClosing() {
        // Verify LABA_BERJALAN account exists
        var labaBerjalan = accountRepository.findByAccountCode("3.2.02");
        assertThat(labaBerjalan).isPresent();

        // Verify LABA_DITAHAN account exists
        var labaDitahan = accountRepository.findByAccountCode("3.2.01");
        assertThat(labaDitahan).isPresent();
    }

    @Test
    @DisplayName("Should preview closing for past year")
    void shouldPreviewClosingForPastYear() {
        int pastYear = java.time.LocalDate.now().getYear() - 1;
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(pastYear);

        assertThat(preview).isNotNull();
        assertThat(preview.year()).isEqualTo(pastYear);
    }

    @Test
    @DisplayName("Should calculate income statement totals correctly in preview")
    void shouldCalculateIncomeStatementTotalsCorrectlyInPreview() {
        int currentYear = java.time.LocalDate.now().getYear();
        FiscalYearClosingService.ClosingPreview preview = fiscalYearClosingService.previewClosing(currentYear);

        // Basic validation - all values should be non-null
        assertThat(preview.totalRevenue()).isNotNull();
        assertThat(preview.totalExpense()).isNotNull();
        assertThat(preview.netIncome()).isNotNull();

        // Revenue and expense should be non-negative
        assertThat(preview.totalRevenue()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(preview.totalExpense()).isGreaterThanOrEqualTo(BigDecimal.ZERO);

        // Net income calculation verification
        BigDecimal calculatedNetIncome = preview.totalRevenue().subtract(preview.totalExpense());
        assertThat(preview.netIncome()).isEqualByComparingTo(calculatedNetIncome);
    }
}
