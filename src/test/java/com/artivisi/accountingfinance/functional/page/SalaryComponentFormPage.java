package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class SalaryComponentFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CODE_INPUT = "#code";
    private static final String NAME_INPUT = "#name";
    private static final String DESCRIPTION_INPUT = "#description";
    private static final String COMPONENT_TYPE_SELECT = "#componentType";
    private static final String IS_PERCENTAGE_FALSE = "input[name='isPercentage'][value='false']";
    private static final String IS_PERCENTAGE_TRUE = "input[name='isPercentage'][value='true']";
    private static final String DEFAULT_AMOUNT_INPUT = "#defaultAmount";
    private static final String DEFAULT_RATE_INPUT = "#defaultRate";
    private static final String DISPLAY_ORDER_INPUT = "#displayOrder";
    private static final String IS_TAXABLE_CHECKBOX = "input[type='checkbox'][name='isTaxable']";
    private static final String BPJS_CATEGORY_SELECT = "#bpjsCategory";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public SalaryComponentFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public SalaryComponentFormPage navigateToNew() {
        page.navigate(baseUrl + "/salary-components/new");
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillCode(String code) {
        page.fill(CODE_INPUT, code);
    }

    public void fillName(String name) {
        page.fill(NAME_INPUT, name);
    }

    public void fillDescription(String description) {
        page.fill(DESCRIPTION_INPUT, description);
    }

    public void selectComponentType(String type) {
        page.selectOption(COMPONENT_TYPE_SELECT, type);
    }

    public void selectFixedAmount() {
        page.click(IS_PERCENTAGE_FALSE);
    }

    public void selectPercentage() {
        page.click(IS_PERCENTAGE_TRUE);
        page.waitForTimeout(500); // Wait for Alpine.js to toggle fields
    }

    public void fillDefaultAmount(String amount) {
        page.fill(DEFAULT_AMOUNT_INPUT, amount);
    }

    public void fillDefaultRate(String rate) {
        page.fill(DEFAULT_RATE_INPUT, rate);
    }

    public void fillDisplayOrder(String order) {
        page.fill(DISPLAY_ORDER_INPUT, order);
    }

    public void checkTaxable() {
        if (!page.isChecked(IS_TAXABLE_CHECKBOX)) {
            page.click(IS_TAXABLE_CHECKBOX);
        }
    }

    public void uncheckTaxable() {
        if (page.isChecked(IS_TAXABLE_CHECKBOX)) {
            page.click(IS_TAXABLE_CHECKBOX);
        }
    }

    public void selectBpjsCategory(String category) {
        page.selectOption(BPJS_CATEGORY_SELECT, category);
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }

    public boolean hasValidationError() {
        return page.locator(".text-red-500, .border-red-500").count() > 0;
    }
}
