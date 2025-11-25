package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TemplateFormPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String TEMPLATE_NAME_INPUT = "#templateName";
    private static final String CATEGORY_SELECT = "#category";
    private static final String CASH_FLOW_SELECT = "#cashFlow";
    private static final String TEMPLATE_TYPE_SELECT = "#templateType";
    private static final String DESCRIPTION_INPUT = "#description";
    private static final String SAVE_BUTTON = "#btn-simpan";
    private static final String CANCEL_LINK = "a:has-text('Batal')";
    private static final String ADD_LINE_BUTTON = "button:has-text('Tambah Baris')";

    public TemplateFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TemplateFormPage navigateToNew() {
        page.navigate(baseUrl + "/templates/new");
        page.waitForLoadState();
        waitForAlpineInit();
        return this;
    }

    public TemplateFormPage navigateToEdit(String templateId) {
        page.navigate(baseUrl + "/templates/" + templateId + "/edit");
        page.waitForLoadState();
        waitForAlpineInit();
        return this;
    }

    private void waitForAlpineInit() {
        // Wait for Alpine.js to initialize (same pattern as JournalFormPage)
        page.waitForSelector("[x-data]");
        page.waitForSelector("#line-account-0", new Page.WaitForSelectorOptions().setTimeout(10000));
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expectedText);
    }

    public void fillTemplateName(String name) {
        page.locator(TEMPLATE_NAME_INPUT).fill(name);
    }

    public void selectCategory(String category) {
        page.locator(CATEGORY_SELECT).selectOption(category);
    }

    public void selectCashFlowCategory(String cashFlowCategory) {
        page.locator(CASH_FLOW_SELECT).selectOption(cashFlowCategory);
    }

    public void selectTemplateType(String templateType) {
        page.locator(TEMPLATE_TYPE_SELECT).selectOption(templateType);
    }

    public void fillDescription(String description) {
        page.locator(DESCRIPTION_INPUT).fill(description);
    }

    public void selectAccountForLine(int lineIndex, String accountId) {
        String selector = "#line-account-" + lineIndex;
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
        page.locator(selector).selectOption(accountId);
    }

    public void setPositionForLine(int lineIndex, String position) {
        // Click the position button (DEBIT or CREDIT)
        String buttonText = position.equals("DEBIT") ? "Debit" : "Kredit";
        // Use the x-for template index to find the correct row
        page.locator("button:has-text('" + buttonText + "')").nth(lineIndex).click();
    }

    public void fillFormulaForLine(int lineIndex, String formula) {
        String selector = "input[name='lines[" + lineIndex + "].formula']";
        page.locator(selector).fill(formula);
    }

    public void clickAddLine() {
        page.locator(ADD_LINE_BUTTON).click();
    }

    public void clickSave() {
        page.locator(SAVE_BUTTON).click();
        page.waitForLoadState();
    }

    public void clickCancel() {
        page.locator(CANCEL_LINK).click();
        page.waitForLoadState();
    }

    public String getTemplateNameValue() {
        return page.locator(TEMPLATE_NAME_INPUT).inputValue();
    }

    public String getCategoryValue() {
        return page.locator(CATEGORY_SELECT).inputValue();
    }

    public void assertSaveButtonVisible() {
        assertThat(page.locator(SAVE_BUTTON)).isVisible();
    }

    public void assertTemplateNameInputVisible() {
        assertThat(page.locator(TEMPLATE_NAME_INPUT)).isVisible();
    }

    public void assertCategorySelectVisible() {
        assertThat(page.locator(CATEGORY_SELECT)).isVisible();
    }

    public int getAccountOptionsCount() {
        // Count options in the first account select dropdown (excluding "Pilih akun" placeholder)
        return page.locator("#line-account-0 option").count() - 1;
    }

    public String getFirstAccountId() {
        return page.locator("#line-account-0 option").nth(1).getAttribute("value");
    }

    public String getSecondAccountId() {
        return page.locator("#line-account-0 option").nth(2).getAttribute("value");
    }
}
