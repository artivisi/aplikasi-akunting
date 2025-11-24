package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class JournalDetailPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String JOURNAL_NUMBER = "[data-testid='journal-number']";
    private static final String JOURNAL_DESCRIPTION = "[data-testid='journal-description']";
    private static final String JOURNAL_DATE = "[data-testid='journal-date']";
    private static final String REFERENCE_NUMBER = "[data-testid='reference-number']";
    private static final String STATUS_BADGE = "[data-testid='status-badge']";
    private static final String STATUS_BANNER_DRAFT = "[data-testid='status-banner-draft']";
    private static final String STATUS_BANNER_POSTED = "[data-testid='status-banner-posted']";
    private static final String STATUS_BANNER_VOID = "[data-testid='status-banner-void']";
    private static final String EDIT_BUTTON = "[data-testid='edit-button']";
    private static final String POST_BUTTON = "[data-testid='post-button']";
    private static final String VOID_BUTTON = "[data-testid='void-button']";
    private static final String JOURNAL_LINES = "[data-testid='journal-lines']";
    private static final String JOURNAL_LINE = "[data-testid='journal-line']";
    private static final String TOTAL_DEBIT = "[data-testid='total-debit']";
    private static final String TOTAL_CREDIT = "[data-testid='total-credit']";
    private static final String BALANCE_STATUS = "[data-testid='balance-status']";
    private static final String POSTED_AT = "[data-testid='posted-at']";
    private static final String VOIDED_AT = "[data-testid='voided-at']";
    private static final String POST_DIALOG = "#postDialog";
    private static final String VOID_DIALOG = "#voidDialog";

    public JournalDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public JournalDetailPage navigate(String journalId) {
        page.navigate(baseUrl + "/journals/" + journalId);
        page.waitForLoadState();
        return this;
    }

    // Header assertions
    public void assertJournalNumberVisible() {
        assertThat(page.locator(JOURNAL_NUMBER)).isVisible();
    }

    public void assertJournalNumberText(String expectedText) {
        assertThat(page.locator(JOURNAL_NUMBER)).hasText(expectedText);
    }

    public void assertJournalDescriptionText(String expectedText) {
        assertThat(page.locator(JOURNAL_DESCRIPTION)).hasText(expectedText);
    }

    public void assertJournalDateContains(String expectedText) {
        assertThat(page.locator(JOURNAL_DATE)).containsText(expectedText);
    }

    public void assertReferenceNumberText(String expectedText) {
        assertThat(page.locator(REFERENCE_NUMBER)).hasText(expectedText);
    }

    // Status badge assertions
    public void assertStatusBadgeVisible() {
        assertThat(page.locator(STATUS_BADGE)).isVisible();
    }

    public void assertStatusBadgeText(String expectedText) {
        assertThat(page.locator(STATUS_BADGE)).containsText(expectedText);
    }

    // Status banner assertions
    public void assertDraftBannerVisible() {
        assertThat(page.locator(STATUS_BANNER_DRAFT)).isVisible();
    }

    public void assertPostedBannerVisible() {
        assertThat(page.locator(STATUS_BANNER_POSTED)).isVisible();
    }

    public void assertVoidBannerVisible() {
        assertThat(page.locator(STATUS_BANNER_VOID)).isVisible();
    }

    public void assertVoidBannerContainsReason(String reason) {
        assertThat(page.locator(STATUS_BANNER_VOID)).containsText(reason);
    }

    public void assertDraftBannerNotVisible() {
        assertThat(page.locator(STATUS_BANNER_DRAFT)).not().isVisible();
    }

    public void assertPostedBannerNotVisible() {
        assertThat(page.locator(STATUS_BANNER_POSTED)).not().isVisible();
    }

    public void assertVoidBannerNotVisible() {
        assertThat(page.locator(STATUS_BANNER_VOID)).not().isVisible();
    }

    // Button assertions
    public void assertEditButtonVisible() {
        assertThat(page.locator(EDIT_BUTTON)).isVisible();
    }

    public void assertEditButtonNotVisible() {
        assertThat(page.locator(EDIT_BUTTON)).not().isVisible();
    }

    public void assertPostButtonVisible() {
        assertThat(page.locator(POST_BUTTON)).isVisible();
    }

    public void assertPostButtonNotVisible() {
        assertThat(page.locator(POST_BUTTON)).not().isVisible();
    }

    public void assertPostButtonEnabled() {
        assertThat(page.locator(POST_BUTTON)).isEnabled();
    }

    public void assertPostButtonDisabled() {
        assertThat(page.locator(POST_BUTTON)).isDisabled();
    }

    public void assertVoidButtonVisible() {
        assertThat(page.locator(VOID_BUTTON)).isVisible();
    }

    public void assertVoidButtonNotVisible() {
        assertThat(page.locator(VOID_BUTTON)).not().isVisible();
    }

    // Journal lines assertions
    public void assertJournalLinesVisible() {
        assertThat(page.locator(JOURNAL_LINES)).isVisible();
    }

    public void assertJournalLineCount(int expected) {
        assertThat(page.locator(JOURNAL_LINE)).hasCount(expected);
    }

    // Totals assertions
    public void assertTotalDebitText(String expectedText) {
        assertThat(page.locator(TOTAL_DEBIT)).hasText(expectedText);
    }

    public void assertTotalCreditText(String expectedText) {
        assertThat(page.locator(TOTAL_CREDIT)).hasText(expectedText);
    }

    // Balance status assertions
    public void assertBalanceStatusVisible() {
        assertThat(page.locator(BALANCE_STATUS)).isVisible();
    }

    public void assertBalanced() {
        assertThat(page.locator(BALANCE_STATUS)).containsText("Jurnal Balance");
    }

    public void assertNotBalanced() {
        assertThat(page.locator(BALANCE_STATUS)).containsText("Jurnal Tidak Balance");
    }

    // Timestamp assertions
    public void assertPostedAtVisible() {
        assertThat(page.locator(POSTED_AT)).isVisible();
    }

    public void assertVoidedAtVisible() {
        assertThat(page.locator(VOIDED_AT)).isVisible();
    }

    // Actions
    public void clickEditButton() {
        page.click(EDIT_BUTTON);
    }

    public void clickPostButton() {
        page.click(POST_BUTTON);
    }

    public void clickVoidButton() {
        page.click(VOID_BUTTON);
    }

    public void confirmPost() {
        page.locator(POST_DIALOG + ":not(.hidden)").waitFor();
        page.locator(POST_DIALOG + " button:has-text('Ya, Posting')").click();
        page.waitForLoadState();
    }

    public void cancelPost() {
        page.locator(POST_DIALOG + ":not(.hidden)").waitFor();
        page.locator(POST_DIALOG + " button:has-text('Batal')").click();
    }

    public void fillVoidReason(String reason) {
        page.fill("#voidReason", reason);
    }

    public void confirmVoid() {
        page.locator(VOID_DIALOG + ":not(.hidden)").waitFor();
        page.locator(VOID_DIALOG + " button:has-text('Ya, Void')").click();
        page.waitForLoadState();
    }

    public void cancelVoid() {
        page.locator(VOID_DIALOG + ":not(.hidden)").waitFor();
        page.locator(VOID_DIALOG + " button:has-text('Batal')").click();
    }

    // Getters
    public String getJournalNumber() {
        return page.locator(JOURNAL_NUMBER).textContent();
    }

    public String getJournalDescription() {
        return page.locator(JOURNAL_DESCRIPTION).textContent();
    }

    public String getStatusBadgeText() {
        return page.locator(STATUS_BADGE).textContent();
    }

    // Post dialog assertions
    public void assertPostDialogVisible() {
        assertThat(page.locator(POST_DIALOG)).isVisible();
    }

    public void assertPostDialogNotVisible() {
        assertThat(page.locator(POST_DIALOG)).isHidden();
    }

    public void assertPostDialogContainsText(String text) {
        assertThat(page.locator(POST_DIALOG)).containsText(text);
    }

    // Void dialog assertions
    public void assertVoidDialogVisible() {
        assertThat(page.locator(VOID_DIALOG)).isVisible();
    }

    public void assertVoidDialogNotVisible() {
        assertThat(page.locator(VOID_DIALOG)).isHidden();
    }

    // Wait for page reload after post/void
    public void waitForPageReload() {
        page.waitForLoadState();
    }
}
