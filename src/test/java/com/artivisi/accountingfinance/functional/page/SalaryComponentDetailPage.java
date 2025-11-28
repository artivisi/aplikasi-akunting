package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class SalaryComponentDetailPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String COMPONENT_DETAIL = "[data-testid='component-detail']";
    private static final String COMPONENT_CODE = "[data-testid='component-code']";
    private static final String COMPONENT_NAME = "[data-testid='component-name']";
    private static final String COMPONENT_TYPE = "[data-testid='component-type']";
    private static final String ACTIVE_STATUS = "[data-testid='active-status']";
    private static final String VALUE_TYPE = "[data-testid='value-type']";
    private static final String DEFAULT_AMOUNT = "[data-testid='default-amount']";
    private static final String DEFAULT_RATE = "[data-testid='default-rate']";
    private static final String IS_TAXABLE = "[data-testid='is-taxable']";
    private static final String BPJS_CATEGORY = "[data-testid='bpjs-category']";
    private static final String DEACTIVATE_BUTTON = "button:has-text('Nonaktifkan')";
    private static final String ACTIVATE_BUTTON = "button:has-text('Aktifkan')";
    private static final String EDIT_BUTTON = "a:has-text('Edit')";

    public SalaryComponentDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void assertComponentCodeText(String expected) {
        assertThat(page.locator(COMPONENT_CODE).textContent()).contains(expected);
    }

    public void assertComponentNameText(String expected) {
        assertThat(page.locator(COMPONENT_NAME).textContent()).contains(expected);
    }

    public void assertComponentTypeText(String expected) {
        assertThat(page.locator(COMPONENT_TYPE).textContent()).contains(expected);
    }

    public void assertStatusText(String expected) {
        assertThat(page.locator(ACTIVE_STATUS).textContent()).contains(expected);
    }

    public void assertValueTypeText(String expected) {
        assertThat(page.locator(VALUE_TYPE).textContent()).contains(expected);
    }

    public void assertDefaultAmountText(String expected) {
        assertThat(page.locator(DEFAULT_AMOUNT).textContent()).contains(expected);
    }

    public void assertDefaultRateText(String expected) {
        assertThat(page.locator(DEFAULT_RATE).textContent()).contains(expected);
    }

    public void assertTaxableText(String expected) {
        assertThat(page.locator(IS_TAXABLE).textContent()).contains(expected);
    }

    public void assertBpjsCategoryText(String expected) {
        assertThat(page.locator(BPJS_CATEGORY).textContent()).contains(expected);
    }

    public boolean hasDeactivateButton() {
        return page.locator(DEACTIVATE_BUTTON).count() > 0;
    }

    public boolean hasActivateButton() {
        return page.locator(ACTIVATE_BUTTON).count() > 0;
    }

    public boolean hasEditButton() {
        return page.locator(EDIT_BUTTON).count() > 0;
    }

    public void clickDeactivateButton() {
        page.onDialog(dialog -> dialog.accept());
        page.click(DEACTIVATE_BUTTON);
        page.waitForLoadState();
    }

    public void clickActivateButton() {
        page.click(ACTIVATE_BUTTON);
        page.waitForLoadState();
    }

    public void clickEditButton() {
        page.click(EDIT_BUTTON);
        page.waitForLoadState();
    }
}
