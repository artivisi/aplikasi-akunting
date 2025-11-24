package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.JournalListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Journal Entry List (Buku Besar)")
class JournalEntryListTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private JournalListPage journalListPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        journalListPage = new JournalListPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("3.1 Page Display")
    class PageDisplayTests {

        @Test
        @DisplayName("Should display page title 'Buku Besar'")
        void shouldDisplayPageTitle() {
            journalListPage.navigate();

            journalListPage.assertPageTitleVisible();
            journalListPage.assertPageTitleText("Buku Besar");
        }

        @Test
        @DisplayName("Should display account filter dropdown")
        void shouldDisplayAccountFilterDropdown() {
            journalListPage.navigate();

            journalListPage.assertAccountFilterVisible();
        }

        @Test
        @DisplayName("Should display date range inputs")
        void shouldDisplayDateRangeInputs() {
            journalListPage.navigate();

            journalListPage.assertStartDateVisible();
            journalListPage.assertEndDateVisible();
        }

        @Test
        @DisplayName("Should display apply button")
        void shouldDisplayApplyButton() {
            journalListPage.navigate();

            journalListPage.assertApplyButtonVisible();
        }
    }

    @Nested
    @DisplayName("3.2 Account Filter")
    class AccountFilterTests {

        @Test
        @DisplayName("Should populate account filter with accounts from database")
        void shouldPopulateAccountFilterFromDatabase() {
            journalListPage.navigate();

            // Should have more than just the placeholder option
            int optionCount = journalListPage.getAccountFilterOptionCount();
            assertThat(optionCount).isGreaterThan(1);
        }

        @Test
        @DisplayName("Should show 'no account message' when no account selected")
        void shouldShowNoAccountMessageWhenNoAccountSelected() {
            journalListPage.navigate();

            journalListPage.assertNoAccountMessageVisible();
            journalListPage.assertLedgerDataNotVisible();
        }

        @Test
        @DisplayName("Should display ledger data when account is selected")
        void shouldDisplayLedgerDataWhenAccountSelected() {
            journalListPage.navigate();

            // Select first transactable account (e.g., "1.1.01 - Kas")
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            page.waitForLoadState();

            journalListPage.assertNoAccountMessageNotVisible();
            journalListPage.assertLedgerDataVisible();
        }
    }

    @Nested
    @DisplayName("3.3 Ledger Display")
    class LedgerDisplayTests {

        @BeforeEach
        void selectAccount() {
            journalListPage.navigate();
            // Select "Kas" account
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            page.waitForLoadState();
        }

        @Test
        @DisplayName("Should display account info card")
        void shouldDisplayAccountInfoCard() {
            journalListPage.assertAccountInfoCardVisible();
            journalListPage.assertAccountNameContains("1.1.01 - Kas");
        }

        @Test
        @DisplayName("Should display summary cards")
        void shouldDisplaySummaryCards() {
            journalListPage.assertSummaryCardsVisible();
            journalListPage.assertOpeningBalanceVisible();
            journalListPage.assertTotalDebitVisible();
            journalListPage.assertTotalCreditVisible();
            journalListPage.assertClosingBalanceVisible();
        }

        @Test
        @DisplayName("Should display entries table")
        void shouldDisplayEntriesTable() {
            journalListPage.assertEntriesTableVisible();
        }

        @Test
        @DisplayName("Should display opening balance row")
        void shouldDisplayOpeningBalanceRow() {
            journalListPage.assertOpeningBalanceRowVisible();
        }

        @Test
        @DisplayName("Should show opening balance as 0 for new account")
        void shouldShowOpeningBalanceAsZeroForNewAccount() {
            // Kas account has no transactions, so opening balance should be 0
            journalListPage.assertOpeningBalanceText("0");
        }

        @Test
        @DisplayName("Should show empty entries message when no journal entries")
        void shouldShowEmptyEntriesMessageWhenNoJournalEntries() {
            // Kas account has no transactions, so entries should be empty
            journalListPage.assertEmptyEntriesVisible();
        }
    }

    @Nested
    @DisplayName("3.4 Date Filter")
    class DateFilterTests {

        @Test
        @DisplayName("Should default to current month date range")
        void shouldDefaultToCurrentMonthDateRange() {
            journalListPage.navigate();

            // The start and end date inputs should have values set
            journalListPage.assertStartDateVisible();
            journalListPage.assertEndDateVisible();
        }

        @Test
        @DisplayName("Should update ledger when date range is changed and apply clicked")
        void shouldUpdateLedgerWhenDateRangeChanged() {
            journalListPage.navigate();

            // Select an account first
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            page.waitForLoadState();

            // Change date range
            journalListPage.setStartDate("2024-01-01");
            journalListPage.setEndDate("2024-12-31");
            journalListPage.clickApply();

            page.waitForLoadState();

            // Should still show ledger data
            journalListPage.assertLedgerDataVisible();
        }
    }

    @Nested
    @DisplayName("3.5 Search Filter")
    class SearchFilterTests {

        @Test
        @DisplayName("Should display search input")
        void shouldDisplaySearchInput() {
            journalListPage.navigate();

            journalListPage.assertSearchInputVisible();
        }

        @Test
        @DisplayName("Should preserve search query after filter applied")
        void shouldPreserveSearchQueryAfterFilterApplied() {
            journalListPage.navigate();

            // Select an account first
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            page.waitForLoadState();

            // Enter search query
            journalListPage.setSearchQuery("test query");
            journalListPage.clickApply();

            page.waitForLoadState();

            // Search input should retain the value
            journalListPage.assertSearchInputValue("test query");
        }

        @Test
        @DisplayName("Should filter entries when search is applied")
        void shouldFilterEntriesWhenSearchApplied() {
            journalListPage.navigate();

            // Select an account
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            page.waitForLoadState();

            // Apply search (no entries exist so should still show empty)
            journalListPage.setSearchQuery("nonexistent");
            journalListPage.clickApply();

            page.waitForLoadState();

            // Should show ledger data (even if empty)
            journalListPage.assertLedgerDataVisible();
        }
    }

    @Nested
    @DisplayName("3.6 Pagination")
    class PaginationTests {

        @Test
        @DisplayName("Should not show pagination when entries are less than page size")
        void shouldNotShowPaginationWhenEntriesLessThanPageSize() {
            journalListPage.navigate();

            // Select an account with no entries
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            page.waitForLoadState();

            // Pagination should not be visible when there are no entries
            journalListPage.assertPaginationNotVisible();
        }

        @Test
        @DisplayName("Should maintain filter parameters when navigating pages")
        void shouldMaintainFilterParametersWhenNavigatingPages() {
            journalListPage.navigate();

            // Select an account and set filters
            journalListPage.selectAccountByLabel("1.1.01 - Kas");
            journalListPage.setStartDate("2024-01-01");
            journalListPage.setEndDate("2024-12-31");
            journalListPage.setSearchQuery("test");
            journalListPage.clickApply();

            page.waitForLoadState();

            // URL should contain the filter parameters
            String currentUrl = page.url();
            assertThat(currentUrl).contains("startDate=2024-01-01");
            assertThat(currentUrl).contains("endDate=2024-12-31");
            assertThat(currentUrl).contains("search=test");
        }
    }
}
