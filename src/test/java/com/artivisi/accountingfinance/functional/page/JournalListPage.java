package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class JournalListPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String ACCOUNT_FILTER = "#account-filter";
    private static final String START_DATE = "#start-date";
    private static final String END_DATE = "#end-date";
    private static final String BTN_APPLY = "#btn-apply";
    private static final String NO_ACCOUNT_MESSAGE = "#no-account-message";
    private static final String LEDGER_DATA = "#ledger-data";
    private static final String ACCOUNT_INFO_CARD = "#account-info-card";
    private static final String ACCOUNT_NAME = "#account-name";
    private static final String SUMMARY_CARDS = "#summary-cards";
    private static final String OPENING_BALANCE = "#opening-balance";
    private static final String TOTAL_DEBIT = "#total-debit";
    private static final String TOTAL_CREDIT = "#total-credit";
    private static final String CLOSING_BALANCE = "#closing-balance";
    private static final String ENTRIES_TABLE = "#entries-table";
    private static final String ENTRIES_CONTENT = "#entries-content";
    private static final String OPENING_BALANCE_ROW = "#opening-balance-row";
    private static final String EMPTY_ENTRIES = "#empty-entries";

    // Search and Pagination
    private static final String SEARCH_INPUT = "#search-input";
    private static final String PAGINATION = "#pagination";
    private static final String BTN_PREV_PAGE = "#btn-prev-page";
    private static final String BTN_NEXT_PAGE = "#btn-next-page";
    private static final String CURRENT_PAGE_NUMBER = "#current-page-number";
    private static final String TOTAL_ENTRIES = "#total-entries";
    private static final String OPENING_BALANCE_VALUE = "#opening-balance-value";

    public JournalListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public JournalListPage navigate() {
        page.navigate(baseUrl + "/journals");
        return this;
    }

    // Actions
    public void selectAccountByLabel(String label) {
        page.selectOption(ACCOUNT_FILTER, new com.microsoft.playwright.options.SelectOption().setLabel(label));
        // Wait for HTMX to complete the request
        page.waitForTimeout(500);
        page.waitForSelector("#journal-ledger");
    }

    public void setStartDate(String date) {
        page.fill(START_DATE, date);
    }

    public void setEndDate(String date) {
        page.fill(END_DATE, date);
    }

    public void clickApply() {
        page.click(BTN_APPLY);
        // Wait for HTMX to complete the request
        page.waitForTimeout(500);
        page.waitForSelector("#journal-ledger");
    }

    // Page title assertions
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expectedText);
    }

    // Filter assertions
    public void assertAccountFilterVisible() {
        assertThat(page.locator(ACCOUNT_FILTER)).isVisible();
    }

    public void assertAccountFilterHasOptions() {
        // Check that there are options beyond "Pilih Akun"
        assertThat(page.locator(ACCOUNT_FILTER + " option")).hasCount(page.locator(ACCOUNT_FILTER + " option").count());
    }

    public void assertAccountFilterContainsOption(String text) {
        assertThat(page.locator(ACCOUNT_FILTER + " option:has-text('" + text + "')")).hasCount(1);
    }

    public void assertStartDateVisible() {
        assertThat(page.locator(START_DATE)).isVisible();
    }

    public void assertEndDateVisible() {
        assertThat(page.locator(END_DATE)).isVisible();
    }

    public void assertApplyButtonVisible() {
        assertThat(page.locator(BTN_APPLY)).isVisible();
    }

    // No account selected state
    public void assertNoAccountMessageVisible() {
        assertThat(page.locator(NO_ACCOUNT_MESSAGE)).isVisible();
    }

    public void assertNoAccountMessageNotVisible() {
        assertThat(page.locator(NO_ACCOUNT_MESSAGE)).not().isVisible();
    }

    // Ledger data state
    public void assertLedgerDataVisible() {
        assertThat(page.locator(LEDGER_DATA)).isVisible();
    }

    public void assertLedgerDataNotVisible() {
        assertThat(page.locator(LEDGER_DATA)).not().isVisible();
    }

    // Account info card
    public void assertAccountInfoCardVisible() {
        assertThat(page.locator(ACCOUNT_INFO_CARD)).isVisible();
    }

    public void assertAccountNameContains(String expectedText) {
        assertThat(page.locator(ACCOUNT_NAME)).containsText(expectedText);
    }

    // Summary cards
    public void assertSummaryCardsVisible() {
        assertThat(page.locator(SUMMARY_CARDS)).isVisible();
    }

    public void assertOpeningBalanceVisible() {
        assertThat(page.locator(OPENING_BALANCE)).isVisible();
    }

    public void assertTotalDebitVisible() {
        assertThat(page.locator(TOTAL_DEBIT)).isVisible();
    }

    public void assertTotalCreditVisible() {
        assertThat(page.locator(TOTAL_CREDIT)).isVisible();
    }

    public void assertClosingBalanceVisible() {
        assertThat(page.locator(CLOSING_BALANCE)).isVisible();
    }

    public void assertOpeningBalanceText(String expectedText) {
        assertThat(page.locator(OPENING_BALANCE)).hasText(expectedText);
    }

    public void assertTotalDebitText(String expectedText) {
        assertThat(page.locator(TOTAL_DEBIT)).hasText(expectedText);
    }

    public void assertTotalCreditText(String expectedText) {
        assertThat(page.locator(TOTAL_CREDIT)).hasText(expectedText);
    }

    public void assertClosingBalanceText(String expectedText) {
        assertThat(page.locator(CLOSING_BALANCE)).hasText(expectedText);
    }

    // Entries table
    public void assertEntriesTableVisible() {
        assertThat(page.locator(ENTRIES_TABLE)).isVisible();
    }

    public void assertOpeningBalanceRowVisible() {
        assertThat(page.locator(OPENING_BALANCE_ROW)).isVisible();
    }

    // Empty entries state
    public void assertEmptyEntriesVisible() {
        assertThat(page.locator(EMPTY_ENTRIES)).isVisible();
    }

    public void assertEmptyEntriesNotVisible() {
        assertThat(page.locator(EMPTY_ENTRIES)).not().isVisible();
    }

    // Helper method to get account filter option count
    public int getAccountFilterOptionCount() {
        return page.locator(ACCOUNT_FILTER + " option").count();
    }

    // Search actions
    public void setSearchQuery(String query) {
        page.fill(SEARCH_INPUT, query);
    }

    public void assertSearchInputVisible() {
        assertThat(page.locator(SEARCH_INPUT)).isVisible();
    }

    public void assertSearchInputValue(String expectedValue) {
        assertThat(page.locator(SEARCH_INPUT)).hasValue(expectedValue);
    }

    // Pagination assertions
    public void assertPaginationVisible() {
        assertThat(page.locator(PAGINATION)).isVisible();
    }

    public void assertPaginationNotVisible() {
        assertThat(page.locator(PAGINATION)).not().isVisible();
    }

    public void assertPrevPageButtonVisible() {
        assertThat(page.locator(BTN_PREV_PAGE)).isVisible();
    }

    public void assertNextPageButtonVisible() {
        assertThat(page.locator(BTN_NEXT_PAGE)).isVisible();
    }

    public void assertCurrentPageNumber(String expectedNumber) {
        assertThat(page.locator(CURRENT_PAGE_NUMBER)).hasText(expectedNumber);
    }

    public void assertTotalEntriesText(String expectedText) {
        assertThat(page.locator(TOTAL_ENTRIES)).hasText(expectedText);
    }

    // Pagination actions
    public void clickNextPage() {
        page.click(BTN_NEXT_PAGE);
        // Wait for HTMX to complete the request
        page.waitForTimeout(500);
        page.waitForSelector("#journal-ledger");
    }

    public void clickPrevPage() {
        page.click(BTN_PREV_PAGE);
        // Wait for HTMX to complete the request
        page.waitForTimeout(500);
        page.waitForSelector("#journal-ledger");
    }

    // Opening balance value from the row
    public void assertOpeningBalanceRowText(String expectedText) {
        assertThat(page.locator(OPENING_BALANCE_VALUE)).hasText(expectedText);
    }

    // Helper to get entry count in table
    public int getJournalEntryRowCount() {
        // Count rows except opening balance row and empty entries
        return page.locator(ENTRIES_CONTENT + " > a").count();
    }

    public String getSearchInputValue() {
        return page.inputValue(SEARCH_INPUT);
    }
}
