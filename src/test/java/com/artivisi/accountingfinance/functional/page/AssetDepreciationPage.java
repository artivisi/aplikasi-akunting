package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetDepreciationPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String PERIOD_INPUT = "#period";
    private static final String GENERATE_BUTTON = "button:has-text('Generate')";
    private static final String PENDING_TABLE = "table";
    private static final String PENDING_ROWS = "table tbody tr";
    private static final String POST_BUTTON = "button:has-text('Posting')";
    private static final String SKIP_BUTTON = "button:has-text('Skip')";
    private static final String POST_ALL_BUTTON = "button:has-text('Posting Semua')";

    public AssetDepreciationPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetDepreciationPage navigate() {
        page.navigate(baseUrl + "/assets/depreciation");
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillPeriod(String period) {
        page.fill(PERIOD_INPUT, period);
    }

    public void clickGenerate() {
        page.click(GENERATE_BUTTON);
        page.waitForLoadState();
    }

    public int getPendingEntryCount() {
        int count = page.locator(PENDING_ROWS).count();
        if (count == 1 && page.locator(PENDING_ROWS + ":has-text('Tidak ada penyusutan tertunda')").count() > 0) {
            return 0;
        }
        return count;
    }

    public boolean hasPendingEntries() {
        return getPendingEntryCount() > 0;
    }

    public void postFirstPendingEntry() {
        page.locator(POST_BUTTON).first().click();
        page.waitForLoadState();
    }

    public void skipFirstPendingEntry() {
        page.locator(SKIP_BUTTON).first().click();
        page.waitForLoadState();
    }

    public void clickPostAll() {
        page.click(POST_ALL_BUTTON);
        page.waitForLoadState();
    }

    public boolean hasEmptyState() {
        return page.locator("text=Tidak ada penyusutan tertunda").isVisible();
    }
}
