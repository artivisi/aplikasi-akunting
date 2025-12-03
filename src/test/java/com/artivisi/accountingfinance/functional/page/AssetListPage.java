package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String ASSET_TABLE = "#asset-table table";
    private static final String NEW_ASSET_BUTTON = "#btn-new-asset";
    private static final String SEARCH_INPUT = "#search-input";

    public AssetListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetListPage navigate() {
        page.navigate(baseUrl + "/assets");
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
        assertThat(page.locator(ASSET_TABLE).isVisible()).isTrue();
    }

    public void clickNewAssetButton() {
        page.locator(NEW_ASSET_BUTTON).click();
        page.waitForURL("**/assets/new");
        page.waitForLoadState();
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        // Wait for HTMX: 300ms delay + request/response time
        page.waitForTimeout(1000);
    }

    public int getAssetCount() {
        return page.locator(ASSET_TABLE + " tbody tr").count();
    }

    public boolean hasAssetWithCode(String code) {
        return page.locator(ASSET_TABLE + " tbody tr:has-text('" + code + "')").count() > 0;
    }

    public void clickAssetDetail(String code) {
        page.locator(ASSET_TABLE + " tbody tr:has-text('" + code + "') a").first().click();
        page.waitForLoadState();
    }

    public boolean hasEmptyState() {
        return page.locator("#empty-state").isVisible();
    }

    public String getTotalBookValue() {
        return page.locator("text=Total Nilai Buku >> xpath=.. >> p >> nth=1").textContent();
    }
}
