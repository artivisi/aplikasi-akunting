package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetCategoryFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CODE_INPUT = "#code";
    private static final String NAME_INPUT = "#name";
    private static final String DESCRIPTION_INPUT = "#description";
    private static final String DEPRECIATION_METHOD_SELECT = "#depreciationMethod";
    private static final String USEFUL_LIFE_MONTHS_INPUT = "#usefulLifeMonths";
    private static final String DEPRECIATION_RATE_INPUT = "#depreciationRate";
    private static final String ASSET_ACCOUNT_SELECT = "#assetAccount";
    private static final String ACCUMULATED_DEPRECIATION_ACCOUNT_SELECT = "#accumulatedDepreciationAccount";
    private static final String DEPRECIATION_EXPENSE_ACCOUNT_SELECT = "#depreciationExpenseAccount";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public AssetCategoryFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetCategoryFormPage navigateToNew() {
        page.navigate(baseUrl + "/assets/categories/new");
        page.waitForLoadState();
        return this;
    }

    public AssetCategoryFormPage navigateToEdit(String categoryId) {
        page.navigate(baseUrl + "/assets/categories/" + categoryId + "/edit");
        page.waitForLoadState();
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

    public void selectDepreciationMethod(String method) {
        page.selectOption(DEPRECIATION_METHOD_SELECT, method);
    }

    public void fillUsefulLifeMonths(String months) {
        page.fill(USEFUL_LIFE_MONTHS_INPUT, months);
    }

    public void fillDepreciationRate(String rate) {
        page.fill(DEPRECIATION_RATE_INPUT, rate);
    }

    public void selectAssetAccount(String accountId) {
        page.selectOption(ASSET_ACCOUNT_SELECT, accountId);
    }

    public void selectFirstAssetAccount() {
        page.selectOption(ASSET_ACCOUNT_SELECT, new String[]{page.locator(ASSET_ACCOUNT_SELECT + " option").nth(1).getAttribute("value")});
    }

    public void selectAccumulatedDepreciationAccount(String accountId) {
        page.selectOption(ACCUMULATED_DEPRECIATION_ACCOUNT_SELECT, accountId);
    }

    public void selectFirstAccumulatedDepreciationAccount() {
        page.selectOption(ACCUMULATED_DEPRECIATION_ACCOUNT_SELECT, new String[]{page.locator(ACCUMULATED_DEPRECIATION_ACCOUNT_SELECT + " option").nth(1).getAttribute("value")});
    }

    public void selectDepreciationExpenseAccount(String accountId) {
        page.selectOption(DEPRECIATION_EXPENSE_ACCOUNT_SELECT, accountId);
    }

    public void selectFirstDepreciationExpenseAccount() {
        page.selectOption(DEPRECIATION_EXPENSE_ACCOUNT_SELECT, new String[]{page.locator(DEPRECIATION_EXPENSE_ACCOUNT_SELECT + " option").nth(1).getAttribute("value")});
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }
}
