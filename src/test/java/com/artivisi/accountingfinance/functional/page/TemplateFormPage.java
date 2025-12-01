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
        // Wait for Alpine.js to fully populate the select options
        page.waitForTimeout(500);
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
        // Click the position button (DEBIT or CREDIT) using ID
        String buttonId = position.equals("DEBIT") ? "#btn-debit-" + lineIndex : "#btn-credit-" + lineIndex;
        page.locator(buttonId).click();
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

    // Formula Help Panel methods

    public void clickFormulaHelpButton() {
        page.locator("button:has-text('Bantuan Formula')").click();
        page.waitForTimeout(300); // Wait for collapse animation
    }

    public void assertFormulaHelpPanelVisible() {
        // Look for the tab button specifically
        assertThat(page.locator("button:has-text('Coba Formula')")).isVisible();
    }

    public void clickCobaFormulaTab() {
        page.locator("button:has-text('Coba Formula')").click();
    }

    public void clickSintaksTab() {
        page.locator("button:has-text('Sintaks')").click();
    }

    public void clickContohSkenarioTab() {
        page.locator("button:has-text('Contoh Skenario')").click();
    }

    public void fillTryFormula(String formula) {
        page.locator("input[x-model='tryFormula']").fill(formula);
    }

    public void fillTryAmount(String amount) {
        page.locator("input[x-model='tryAmount']").fill(amount);
    }

    public String getTryResult() {
        page.waitForTimeout(500); // Wait for API call
        return page.locator("span[x-text='tryResult']").textContent();
    }

    public void assertTryResultVisible() {
        page.waitForTimeout(500); // Wait for API call
        assertThat(page.locator("span[x-text='tryResult']")).isVisible();
    }

    public void assertTryErrorVisible() {
        page.waitForTimeout(500); // Wait for API call
        assertThat(page.locator("span[x-text='tryError']")).isVisible();
    }

    public void clickQuickExample(String exampleText) {
        page.locator("button:has-text('" + exampleText + "')").click();
    }

    // Preview Amount methods

    public void fillPreviewAmount(String amount) {
        page.locator("input[x-model='previewAmount']").fill(amount);
    }

    public String getLinePreviewResult(int lineIndex) {
        page.waitForTimeout(500); // Wait for API call
        // The preview result is in the 4th column (col-span-2) after formula input
        return page.locator(".grid.grid-cols-12").nth(lineIndex + 1) // +1 to skip header
                .locator(".col-span-2.text-right span[x-text='line.previewResult']").textContent();
    }

    public void assertLinePreviewResultVisible(int lineIndex) {
        page.waitForTimeout(500);
        assertThat(page.locator("span[x-text='line.previewResult']").nth(lineIndex)).isVisible();
    }
}
