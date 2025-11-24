package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class JournalFormPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String JOURNAL_DATE = "#journalDate";
    private static final String REFERENCE_NUMBER = "#referenceNumber";
    private static final String DESCRIPTION = "#description";
    private static final String BTN_ADD_LINE = "#btn-add-line";
    private static final String JOURNAL_LINES_TABLE = "#journal-lines-table";
    private static final String TOTALS_ROW = "#totals-row";
    private static final String TOTAL_DEBIT = "#total-debit";
    private static final String TOTAL_CREDIT = "#total-credit";
    private static final String BALANCE_INDICATOR = "#balance-indicator";
    private static final String BALANCE_STATUS = "#balance-status";
    private static final String BALANCE_DIFFERENCE = "#balance-difference";
    private static final String BTN_SAVE_DRAFT = "#btn-save-draft";
    private static final String BTN_SAVE_POST = "#btn-save-post";
    private static final String BTN_CANCEL = "#btn-cancel";
    private static final String ERROR_MESSAGE = "#error-message";

    public JournalFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public JournalFormPage navigate() {
        page.navigate(baseUrl + "/journals/new");
        page.waitForLoadState();
        return this;
    }

    public void waitForAlpineInit() {
        page.waitForSelector("[x-data]");
        page.waitForSelector("#line-account-0", new Page.WaitForSelectorOptions().setTimeout(10000));
    }

    // Actions
    public void setJournalDate(String date) {
        page.fill(JOURNAL_DATE, date);
    }

    public void setReferenceNumber(String reference) {
        page.fill(REFERENCE_NUMBER, reference);
    }

    public void setDescription(String description) {
        page.fill(DESCRIPTION, description);
    }

    public void clickAddLine() {
        page.click(BTN_ADD_LINE);
    }

    public void selectLineAccount(int lineIndex, String accountLabel) {
        String selector = "#line-account-" + lineIndex;
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
        page.selectOption(selector,
                new com.microsoft.playwright.options.SelectOption().setLabel(accountLabel));
    }

    public void setLineDebit(int lineIndex, String amount) {
        page.fill("#line-debit-" + lineIndex, amount);
    }

    public void setLineCredit(int lineIndex, String amount) {
        page.fill("#line-credit-" + lineIndex, amount);
    }

    public void setLineDescription(int lineIndex, String description) {
        page.fill("#line-desc-" + lineIndex, description);
    }

    public void removeLineAt(int lineIndex) {
        page.click("#btn-remove-line-" + lineIndex);
    }

    public void clickSaveDraft() {
        page.click(BTN_SAVE_DRAFT);
    }

    public void clickSaveAndPost() {
        page.click(BTN_SAVE_POST);
    }

    public void clickCancel() {
        page.click(BTN_CANCEL);
    }

    // Page title assertions
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expectedText);
    }

    // Form field assertions
    public void assertJournalDateVisible() {
        assertThat(page.locator(JOURNAL_DATE)).isVisible();
    }

    public void assertReferenceNumberVisible() {
        assertThat(page.locator(REFERENCE_NUMBER)).isVisible();
    }

    public void assertDescriptionVisible() {
        assertThat(page.locator(DESCRIPTION)).isVisible();
    }

    public void assertAddLineButtonVisible() {
        assertThat(page.locator(BTN_ADD_LINE)).isVisible();
    }

    public void assertJournalLinesTableVisible() {
        assertThat(page.locator(JOURNAL_LINES_TABLE)).isVisible();
    }

    // Totals assertions
    public void assertTotalsRowVisible() {
        assertThat(page.locator(TOTALS_ROW)).isVisible();
    }

    public void assertTotalDebitText(String expectedText) {
        assertThat(page.locator(TOTAL_DEBIT)).hasText(expectedText);
    }

    public void assertTotalCreditText(String expectedText) {
        assertThat(page.locator(TOTAL_CREDIT)).hasText(expectedText);
    }

    // Balance indicator assertions
    public void assertBalanceIndicatorVisible() {
        assertThat(page.locator(BALANCE_INDICATOR)).isVisible();
    }

    public void assertBalanceStatusText(String expectedText) {
        assertThat(page.locator(BALANCE_STATUS)).hasText(expectedText);
    }

    public void assertBalanced() {
        assertThat(page.locator(BALANCE_STATUS)).hasText("Jurnal Balance");
    }

    public void assertNotBalanced() {
        assertThat(page.locator(BALANCE_STATUS)).hasText("Jurnal Tidak Balance");
    }

    // Button assertions
    public void assertSaveDraftButtonVisible() {
        assertThat(page.locator(BTN_SAVE_DRAFT)).isVisible();
    }

    public void assertSavePostButtonVisible() {
        assertThat(page.locator(BTN_SAVE_POST)).isVisible();
    }

    public void assertSavePostButtonDisabled() {
        assertThat(page.locator(BTN_SAVE_POST)).isDisabled();
    }

    public void assertSavePostButtonEnabled() {
        assertThat(page.locator(BTN_SAVE_POST)).isEnabled();
    }

    public void assertCancelButtonVisible() {
        assertThat(page.locator(BTN_CANCEL)).isVisible();
    }

    // Error message assertions
    public void assertErrorMessageVisible() {
        assertThat(page.locator(ERROR_MESSAGE)).isVisible();
    }

    public void assertErrorMessageContains(String expectedText) {
        assertThat(page.locator(ERROR_MESSAGE)).containsText(expectedText);
    }

    // Helper methods
    public int getLineCount() {
        return page.locator("[id^='line-account-']").count();
    }

    public void assertLineCount(int expected) {
        assertThat(page.locator("[id^='line-account-']")).hasCount(expected);
    }

    public void assertRemoveLineButtonDisabled(int lineIndex) {
        assertThat(page.locator("#btn-remove-line-" + lineIndex)).isDisabled();
    }

    public void assertRemoveLineButtonEnabled(int lineIndex) {
        assertThat(page.locator("#btn-remove-line-" + lineIndex)).isEnabled();
    }
}
