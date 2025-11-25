package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TemplateDetailPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String TEMPLATE_NAME = "#template-name";
    private static final String TEMPLATE_NAME_BREADCRUMB = "#template-name-breadcrumb";
    private static final String TEMPLATE_VERSION = "#template-version";
    private static final String EXECUTE_BUTTON = "#btn-execute";
    private static final String EDIT_BUTTON = "#btn-edit";
    private static final String DELETE_BUTTON = "#btn-delete";
    private static final String DETAIL_CONTENT = "#detail-content";

    public TemplateDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TemplateDetailPage navigate(String templateId) {
        page.navigate(baseUrl + "/templates/" + templateId);
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertTemplateNameVisible() {
        assertThat(page.locator(TEMPLATE_NAME)).isVisible();
    }

    public void assertTemplateNameText(String expectedText) {
        assertThat(page.locator(TEMPLATE_NAME)).hasText(expectedText);
    }

    public void assertExecuteButtonVisible() {
        assertThat(page.locator(EXECUTE_BUTTON)).isVisible();
    }

    public void clickExecuteButton() {
        page.click(EXECUTE_BUTTON);
        page.waitForLoadState();
    }

    public String getTemplateName() {
        return page.locator(TEMPLATE_NAME).textContent();
    }

    public String getVersion() {
        return page.locator(TEMPLATE_VERSION).textContent();
    }

    public void assertVersionText(String expectedVersion) {
        assertThat(page.locator(TEMPLATE_VERSION)).hasText(expectedVersion);
    }

    public void assertVersionVisible() {
        assertThat(page.locator(TEMPLATE_VERSION)).isVisible();
    }

    public void assertEditButtonVisible() {
        assertThat(page.locator(EDIT_BUTTON)).isVisible();
    }

    public void assertEditButtonNotVisible() {
        assertThat(page.locator(EDIT_BUTTON)).not().isVisible();
    }

    public void assertDeleteButtonVisible() {
        assertThat(page.locator(DELETE_BUTTON)).isVisible();
    }

    public void assertDeleteButtonNotVisible() {
        assertThat(page.locator(DELETE_BUTTON)).not().isVisible();
    }

    public void clickEditButton() {
        page.waitForSelector(EDIT_BUTTON, new Page.WaitForSelectorOptions().setTimeout(10000));
        page.click(EDIT_BUTTON);
        page.waitForLoadState();
    }

    public void clickDeleteButton() {
        // Handle the confirmation dialog
        page.onceDialog(dialog -> dialog.accept());
        page.click(DELETE_BUTTON);
        page.waitForLoadState();
    }
}
