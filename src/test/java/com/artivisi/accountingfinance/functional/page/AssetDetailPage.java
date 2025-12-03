package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetDetailPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String ASSET_CODE = "#asset-code";
    private static final String ASSET_NAME = "#asset-name";
    private static final String ASSET_STATUS = "#asset-status";
    private static final String EDIT_BUTTON = "#btn-edit";
    private static final String DISPOSE_BUTTON = "#btn-dispose";

    public AssetDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetDetailPage navigate(String assetId) {
        page.navigate(baseUrl + "/assets/" + assetId);
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleContains(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertAssetCodeText(String expected) {
        assertThat(page.locator(ASSET_CODE).textContent()).contains(expected);
    }

    public void assertAssetNameText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertStatusText(String expected) {
        assertThat(page.locator(ASSET_STATUS).textContent()).contains(expected);
    }

    public boolean isEditButtonVisible() {
        return page.locator(EDIT_BUTTON).count() > 0;
    }

    public boolean isDisposeButtonVisible() {
        return page.locator(DISPOSE_BUTTON).count() > 0;
    }

    public void clickEditButton() {
        page.locator(EDIT_BUTTON).click();
        page.waitForLoadState();
    }

    public void clickDisposeButton() {
        page.locator(DISPOSE_BUTTON).click();
        page.waitForLoadState();
    }
}
