package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AccountFormPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String ACCOUNT_CODE_INPUT = "#accountCode";
    private static final String ACCOUNT_NAME_INPUT = "#accountName";
    private static final String ACCOUNT_TYPE_SELECT = "#accountType";
    private static final String NORMAL_BALANCE_DEBIT = "input[name='normalBalance'][value='DEBIT']";
    private static final String NORMAL_BALANCE_CREDIT = "input[name='normalBalance'][value='CREDIT']";
    private static final String IS_HEADER_CHECKBOX = "#isHeader";
    private static final String PERMANENT_CHECKBOX = "#permanent";
    private static final String DESCRIPTION_TEXTAREA = "#description";
    private static final String SAVE_BUTTON = "#btn-simpan";

    public AccountFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AccountFormPage navigateToNew() {
        page.navigate(baseUrl + "/accounts/new");
        return this;
    }

    public AccountFormPage navigateToEdit(String accountId) {
        page.navigate(baseUrl + "/accounts/" + accountId + "/edit");
        return this;
    }

    // Assertions
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expectedText);
    }

    public void assertAccountCodeInputVisible() {
        assertThat(page.locator(ACCOUNT_CODE_INPUT)).isVisible();
    }

    public void assertAccountNameInputVisible() {
        assertThat(page.locator(ACCOUNT_NAME_INPUT)).isVisible();
    }

    public void assertAccountTypeSelectVisible() {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT)).isVisible();
    }

    public void assertPermanentCheckboxVisible() {
        assertThat(page.locator(PERMANENT_CHECKBOX)).isVisible();
    }

    public void assertPermanentCheckboxChecked() {
        assertThat(page.locator(PERMANENT_CHECKBOX)).isChecked();
    }

    public void assertPermanentCheckboxUnchecked() {
        assertThat(page.locator(PERMANENT_CHECKBOX)).not().isChecked();
    }

    public void assertIsHeaderCheckboxVisible() {
        assertThat(page.locator(IS_HEADER_CHECKBOX)).isVisible();
    }

    public void assertSaveButtonVisible() {
        assertThat(page.locator(SAVE_BUTTON)).isVisible();
    }

    // Actions
    public void fillAccountCode(String code) {
        page.fill(ACCOUNT_CODE_INPUT, code);
    }

    public void fillAccountName(String name) {
        page.fill(ACCOUNT_NAME_INPUT, name);
    }

    public void selectAccountType(String type) {
        page.selectOption(ACCOUNT_TYPE_SELECT, type);
    }

    public void selectNormalBalanceDebit() {
        page.click(NORMAL_BALANCE_DEBIT);
    }

    public void selectNormalBalanceCredit() {
        page.click(NORMAL_BALANCE_CREDIT);
    }

    public void checkPermanent() {
        page.locator(PERMANENT_CHECKBOX).check();
    }

    public void uncheckPermanent() {
        page.locator(PERMANENT_CHECKBOX).uncheck();
    }

    public void checkIsHeader() {
        page.locator(IS_HEADER_CHECKBOX).check();
    }

    public void fillDescription(String description) {
        page.fill(DESCRIPTION_TEXTAREA, description);
    }

    public void clickSave() {
        page.click(SAVE_BUTTON);
    }
}
