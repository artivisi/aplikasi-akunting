package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class SalaryComponentListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String COMPONENT_TABLE = "#component-table";
    private static final String NEW_COMPONENT_BUTTON = "#btn-new-component";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String TYPE_SELECT = "select[name='type']";
    private static final String ACTIVE_SELECT = "select[name='active']";

    public SalaryComponentListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public SalaryComponentListPage navigate() {
        page.navigate(baseUrl + "/salary-components");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(COMPONENT_TABLE).isVisible()).isTrue();
    }

    public void clickNewComponentButton() {
        page.click(NEW_COMPONENT_BUTTON);
        page.waitForLoadState();
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.waitForTimeout(500);
    }

    public void selectType(String type) {
        page.selectOption(TYPE_SELECT, type);
        page.waitForTimeout(500);
    }

    public void selectActiveFilter(String active) {
        page.selectOption(ACTIVE_SELECT, active);
        page.waitForTimeout(500);
    }

    public int getComponentCount() {
        return page.locator(COMPONENT_TABLE + " tbody tr").count();
    }

    public boolean hasComponentWithCode(String code) {
        return page.locator(COMPONENT_TABLE + " tbody tr:has-text('" + code + "')").count() > 0;
    }

    public boolean hasComponentWithName(String name) {
        return page.locator(COMPONENT_TABLE + " tbody tr:has-text('" + name + "')").count() > 0;
    }

    public void clickViewLink(String code) {
        page.click(COMPONENT_TABLE + " tbody tr:has-text('" + code + "') a:has-text('Lihat')");
        page.waitForLoadState();
    }
}
