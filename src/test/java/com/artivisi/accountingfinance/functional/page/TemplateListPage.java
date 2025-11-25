package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TemplateListPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String TEMPLATE_LIST = "[data-testid='template-list']";
    private static final String TEMPLATE_CARD = "[data-testid='template-card']";
    private static final String CATEGORY_FILTER = "[data-testid='category-filter']";
    private static final String SEARCH_INPUT = "[data-testid='search-input']";
    private static final String NEW_TEMPLATE_BUTTON = "#btn-new-template";

    public TemplateListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TemplateListPage navigate() {
        page.navigate(baseUrl + "/templates");
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).containsText(expectedText);
    }

    public void assertTemplateListVisible() {
        assertThat(page.locator(TEMPLATE_LIST)).isVisible();
    }

    public int getTemplateCount() {
        return page.locator(TEMPLATE_CARD).count();
    }

    public void assertTemplateCountGreaterThan(int min) {
        int count = page.locator(TEMPLATE_CARD).count();
        org.assertj.core.api.Assertions.assertThat(count).isGreaterThan(min);
    }

    public void clickTemplate(String templateName) {
        page.click(TEMPLATE_CARD + ":has-text('" + templateName + "')");
        page.waitForLoadState();
    }

    public void assertTemplateVisible(String templateName) {
        assertThat(page.locator(TEMPLATE_CARD + ":has-text('" + templateName + "')")).isVisible();
    }

    public void assertTemplateNotVisible(String templateName) {
        assertThat(page.locator(TEMPLATE_CARD + ":has-text('" + templateName + "')")).not().isVisible();
    }

    public void clickNewTemplateButton() {
        page.click(NEW_TEMPLATE_BUTTON);
        page.waitForLoadState();
    }

    public void assertNewTemplateButtonVisible() {
        assertThat(page.locator(NEW_TEMPLATE_BUTTON)).isVisible();
    }

    public void clickViewDetail(String templateName) {
        page.locator(TEMPLATE_CARD + ":has-text('" + templateName + "') a[title='Lihat Detail']").click();
        page.waitForLoadState();
    }
}
