package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetCategoryListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CATEGORY_TABLE = "#category-table table";
    private static final String NEW_CATEGORY_BUTTON = "#btn-new-category";
    private static final String SEARCH_INPUT = "#search-input";

    public AssetCategoryListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetCategoryListPage navigate() {
        page.navigate(baseUrl + "/assets/categories");
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(CATEGORY_TABLE).isVisible()).isTrue();
    }

    public void clickNewCategoryButton() {
        page.waitForSelector(NEW_CATEGORY_BUTTON);
        page.locator(NEW_CATEGORY_BUTTON).click();
        page.waitForURL("**/assets/categories/new");
        page.waitForLoadState();
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.waitForTimeout(500); // Wait for HTMX
    }

    public int getCategoryCount() {
        return page.locator(CATEGORY_TABLE + " tbody tr").count();
    }

    public boolean hasCategoryWithCode(String code) {
        return page.locator(CATEGORY_TABLE + " tbody tr:has-text('" + code + "')").count() > 0;
    }

    public boolean hasEmptyState() {
        return page.locator("text=Tidak ada kategori ditemukan").isVisible();
    }
}
