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
        assertThat(page.locator(TEMPLATE_CARD + ":has-text('" + templateName + "')").first()).isVisible();
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

    // Search functionality
    public void searchTemplates(String query) {
        page.fill(SEARCH_INPUT, query);
        page.click("[data-testid='search-button']");
        // Wait for HTMX to complete the request
        page.waitForTimeout(500);
        page.waitForSelector("#template-grid");
    }

    public void assertSearchInputVisible() {
        assertThat(page.locator(SEARCH_INPUT)).isVisible();
    }

    // Favorites functionality
    public void assertFavoriteButtonVisible() {
        assertThat(page.locator("[data-testid='favorite-button']").first()).isVisible();
    }

    public void assertFavoritesFilterVisible() {
        assertThat(page.locator("[data-testid='filter-favorites']")).isVisible();
    }

    public void clickFavoritesFilter() {
        page.click("[data-testid='filter-favorites']");
        page.waitForLoadState();
    }

    public void clickFavoriteButton(String templateName) {
        // Use first() to handle multiple matches and scroll into view
        var button = page.locator(TEMPLATE_CARD + ":has-text('" + templateName + "') [data-testid='favorite-button']").first();
        button.scrollIntoViewIfNeeded();
        button.click();
        page.waitForTimeout(500); // Wait for HTMX to complete
    }

    public boolean isFavoriteActive(String templateName) {
        var button = page.locator(TEMPLATE_CARD + ":has-text('" + templateName + "') [data-testid='favorite-button']").first();
        String classes = button.getAttribute("class");
        return classes != null && classes.contains("text-amber-400");
    }

    // Tag functionality
    public void clickTagFilter(String tag) {
        page.click("[data-testid='tag-filter']:has-text('" + tag + "')");
        page.waitForLoadState();
    }

    public void assertTagFilterVisible(String tag) {
        assertThat(page.locator("[data-testid='tag-filter']:has-text('" + tag + "')")).isVisible();
    }

    public void assertNoTagFiltersVisible() {
        assertThat(page.locator("[data-testid='tag-filter']")).not().isVisible();
    }

    // Recently used section
    public void assertRecentlyUsedSectionVisible() {
        assertThat(page.locator("[data-testid='recent-template']")).isVisible();
    }

    public void assertRecentlyUsedSectionNotVisible() {
        assertThat(page.locator("[data-testid='recent-template']")).not().isVisible();
    }

    public int getRecentlyUsedCount() {
        return page.locator("[data-testid='recent-template']").count();
    }

    public void clickRecentTemplate(String templateName) {
        page.locator("[data-testid='recent-template']:has-text('" + templateName + "')").click();
        page.waitForLoadState();
    }
}
