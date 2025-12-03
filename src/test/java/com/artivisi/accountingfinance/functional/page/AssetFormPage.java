package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String ASSET_CODE_INPUT = "#assetCode";
    private static final String NAME_INPUT = "#name";
    private static final String CATEGORY_SELECT = "#category";
    private static final String LOCATION_INPUT = "#location";
    private static final String DESCRIPTION_INPUT = "#description";
    private static final String PURCHASE_DATE_INPUT = "#purchaseDate";
    private static final String PURCHASE_COST_INPUT = "#purchaseCost";
    private static final String SUPPLIER_INPUT = "#supplier";
    private static final String INVOICE_NUMBER_INPUT = "#invoiceNumber";
    private static final String SERIAL_NUMBER_INPUT = "#serialNumber";
    private static final String DEPRECIATION_START_DATE_INPUT = "#depreciationStartDate";
    private static final String DEPRECIATION_METHOD_SELECT = "#depreciationMethod";
    private static final String USEFUL_LIFE_MONTHS_INPUT = "#usefulLifeMonths";
    private static final String RESIDUAL_VALUE_INPUT = "#residualValue";
    private static final String DEPRECIATION_RATE_INPUT = "#depreciationRate";
    private static final String NOTES_INPUT = "#notes";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public AssetFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public AssetFormPage navigateToNew() {
        page.navigate(baseUrl + "/assets/new");
        page.waitForLoadState();
        return this;
    }

    public AssetFormPage navigateToEdit(String assetId) {
        page.navigate(baseUrl + "/assets/" + assetId + "/edit");
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillAssetCode(String code) {
        page.fill(ASSET_CODE_INPUT, code);
    }

    public void fillName(String name) {
        page.fill(NAME_INPUT, name);
    }

    public void selectCategory(String categoryId) {
        page.selectOption(CATEGORY_SELECT, categoryId);
    }

    public void selectFirstCategory() {
        page.selectOption(CATEGORY_SELECT, new String[]{page.locator(CATEGORY_SELECT + " option").nth(1).getAttribute("value")});
    }

    public void fillLocation(String location) {
        page.fill(LOCATION_INPUT, location);
    }

    public void fillDescription(String description) {
        page.fill(DESCRIPTION_INPUT, description);
    }

    public void fillPurchaseDate(String date) {
        page.fill(PURCHASE_DATE_INPUT, date);
    }

    public void fillPurchaseCost(String cost) {
        page.fill(PURCHASE_COST_INPUT, cost);
    }

    public void fillSupplier(String supplier) {
        page.fill(SUPPLIER_INPUT, supplier);
    }

    public void fillInvoiceNumber(String invoiceNumber) {
        page.fill(INVOICE_NUMBER_INPUT, invoiceNumber);
    }

    public void fillSerialNumber(String serialNumber) {
        page.fill(SERIAL_NUMBER_INPUT, serialNumber);
    }

    public void fillDepreciationStartDate(String date) {
        page.fill(DEPRECIATION_START_DATE_INPUT, date);
    }

    public void selectDepreciationMethod(String method) {
        page.selectOption(DEPRECIATION_METHOD_SELECT, method);
    }

    public void fillUsefulLifeMonths(String months) {
        page.fill(USEFUL_LIFE_MONTHS_INPUT, months);
    }

    public void fillResidualValue(String value) {
        page.fill(RESIDUAL_VALUE_INPUT, value);
    }

    public void fillDepreciationRate(String rate) {
        page.fill(DEPRECIATION_RATE_INPUT, rate);
    }

    public void fillNotes(String notes) {
        page.fill(NOTES_INPUT, notes);
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }

    public String getFirstCategoryId() {
        return page.locator(CATEGORY_SELECT + " option").nth(1).getAttribute("value");
    }
}
