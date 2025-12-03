package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetDisposePage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String DISPOSAL_TYPE_SELECT = "#disposalType";
    private static final String DISPOSAL_DATE_INPUT = "#disposalDate";
    private static final String PROCEEDS_INPUT = "#proceeds";
    private static final String NOTES_INPUT = "#notes";
    private static final String GAIN_LOSS_PREVIEW = "#gainLossPreview";
    private static final String DISPOSE_BUTTON = "#btn-dispose";
    private static final String CANCEL_BUTTON = "a:has-text('Batal')";

    public AssetDisposePage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetDisposePage navigate(String assetId) {
        page.navigate(baseUrl + "/assets/" + assetId + "/dispose");
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void selectDisposalType(String type) {
        page.selectOption(DISPOSAL_TYPE_SELECT, type);
    }

    public void fillDisposalDate(String date) {
        page.fill(DISPOSAL_DATE_INPUT, date);
    }

    public void fillProceeds(String amount) {
        page.fill(PROCEEDS_INPUT, amount);
    }

    public void fillNotes(String notes) {
        page.fill(NOTES_INPUT, notes);
    }

    public String getGainLossPreview() {
        return page.locator(GAIN_LOSS_PREVIEW).textContent();
    }

    public boolean isGainLossPositive() {
        String className = page.locator(GAIN_LOSS_PREVIEW).getAttribute("class");
        return className != null && className.contains("text-green");
    }

    public boolean isGainLossNegative() {
        String className = page.locator(GAIN_LOSS_PREVIEW).getAttribute("class");
        return className != null && className.contains("text-red");
    }

    public void clickDispose() {
        page.click(DISPOSE_BUTTON);
        page.waitForLoadState();
    }

    public void clickCancel() {
        page.click(CANCEL_BUTTON);
        page.waitForLoadState();
    }

    public String getAssetCode() {
        return page.locator("dd.font-mono").first().textContent();
    }

    public String getBookValue() {
        return page.locator("dd.text-xl.font-bold").textContent();
    }
}
