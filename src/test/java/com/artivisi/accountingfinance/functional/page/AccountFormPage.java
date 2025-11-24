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
    private static final String PARENT_SELECT = "#parentId";
    private static final String NORMAL_BALANCE_DEBIT = "input[name='normalBalance'][value='DEBIT']";
    private static final String NORMAL_BALANCE_CREDIT = "input[name='normalBalance'][value='CREDIT']";
    private static final String IS_HEADER_CHECKBOX = "#isHeader";
    private static final String PERMANENT_CHECKBOX = "#permanent";
    private static final String DESCRIPTION_TEXTAREA = "#description";
    private static final String SAVE_BUTTON = "#btn-simpan";
    private static final String VALIDATION_ERROR_BOX = ".bg-red-50";
    private static final String DUPLICATE_CODE_ERROR = ".bg-red-50";

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

    public void assertAccountTypeDisabled() {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT)).isDisabled();
    }

    public void assertAccountTypeEnabled() {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT)).isEnabled();
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

    // Additional assertions for form fields
    public void assertAccountCodeInputEmpty() {
        assertThat(page.locator(ACCOUNT_CODE_INPUT)).hasValue("");
    }

    public void assertAccountNameInputEmpty() {
        assertThat(page.locator(ACCOUNT_NAME_INPUT)).hasValue("");
    }

    public void assertNormalBalanceRadiosVisible() {
        assertThat(page.locator(NORMAL_BALANCE_DEBIT)).isVisible();
        assertThat(page.locator(NORMAL_BALANCE_CREDIT)).isVisible();
    }

    public void assertDescriptionTextareaVisible() {
        assertThat(page.locator(DESCRIPTION_TEXTAREA)).isVisible();
    }

    public void assertParentSelectVisible() {
        assertThat(page.locator(PARENT_SELECT)).isVisible();
    }

    // Validation error assertions
    public void assertValidationErrorVisible() {
        assertThat(page.locator(VALIDATION_ERROR_BOX)).isVisible();
    }

    public void assertAccountCodeErrorVisible(String errorMessage) {
        assertThat(page.locator(ACCOUNT_CODE_INPUT + " + p")).containsText(errorMessage);
    }

    public void assertAccountNameErrorVisible(String errorMessage) {
        assertThat(page.locator(ACCOUNT_NAME_INPUT + " + p")).containsText(errorMessage);
    }

    public void assertAccountTypeErrorVisible(String errorMessage) {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT + " + p")).containsText(errorMessage);
    }

    public void assertNormalBalanceErrorVisible(String errorMessage) {
        // Normal balance error is displayed after the radio button container as a <p> tag
        assertThat(page.locator("p.text-red-600:has-text(\"" + errorMessage + "\")")).isVisible();
    }

    public void assertDuplicateCodeErrorVisible() {
        assertThat(page.locator(VALIDATION_ERROR_BOX)).isVisible();
    }

    // Parent account selection
    public void selectParentAccount(String parentLabel) {
        page.selectOption(PARENT_SELECT, new com.microsoft.playwright.options.SelectOption().setLabel(parentLabel));
    }

    // Edit-specific assertions
    public void assertSaveButtonText(String expectedText) {
        assertThat(page.locator(SAVE_BUTTON)).containsText(expectedText);
    }

    public void assertAccountCodeValue(String expectedValue) {
        assertThat(page.locator(ACCOUNT_CODE_INPUT)).hasValue(expectedValue);
    }

    public void assertAccountNameValue(String expectedValue) {
        assertThat(page.locator(ACCOUNT_NAME_INPUT)).hasValue(expectedValue);
    }

    public void assertAccountTypeSelected(String expectedType) {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT)).hasValue(expectedType);
    }

    public void assertNormalBalanceDebitSelected() {
        assertThat(page.locator(NORMAL_BALANCE_DEBIT)).isChecked();
    }

    public void assertNormalBalanceCreditSelected() {
        assertThat(page.locator(NORMAL_BALANCE_CREDIT)).isChecked();
    }

    public void assertAccountTypeSelectDisabled() {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT)).isDisabled();
    }

    public void assertAccountTypeSelectEnabled() {
        assertThat(page.locator(ACCOUNT_TYPE_SELECT)).isEnabled();
    }

    // Clear and fill actions for edit scenarios
    public void clearAndFillAccountCode(String code) {
        page.locator(ACCOUNT_CODE_INPUT).clear();
        page.fill(ACCOUNT_CODE_INPUT, code);
    }

    public void clearAndFillAccountName(String name) {
        page.locator(ACCOUNT_NAME_INPUT).clear();
        page.fill(ACCOUNT_NAME_INPUT, name);
    }

    public void clearAccountName() {
        page.locator(ACCOUNT_NAME_INPUT).clear();
    }

    public void clearAccountCode() {
        page.locator(ACCOUNT_CODE_INPUT).clear();
    }

    // Parent dropdown assertions for deleted accounts test
    public void assertParentAccountOptionNotExists(String optionText) {
        assertThat(page.locator(PARENT_SELECT + " option:has-text(\"" + optionText + "\")")).hasCount(0);
    }

    public void assertParentAccountOptionExists(String optionText) {
        assertThat(page.locator(PARENT_SELECT + " option:has-text(\"" + optionText + "\")")).hasCount(1);
    }

    public int getParentAccountOptionsCount() {
        return page.locator(PARENT_SELECT + " option").count();
    }
}
