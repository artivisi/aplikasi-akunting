package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String EMPLOYEE_TABLE = "#employee-table";
    private static final String NEW_EMPLOYEE_BUTTON = "#btn-new-employee";
    private static final String SEARCH_INPUT = "#search-input";

    public EmployeeListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public EmployeeListPage navigate() {
        page.navigate(baseUrl + "/employees");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(EMPLOYEE_TABLE).isVisible()).isTrue();
    }

    public void clickNewEmployeeButton() {
        page.click(NEW_EMPLOYEE_BUTTON);
        page.waitForLoadState();
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.waitForTimeout(500);
    }

    public int getEmployeeCount() {
        return page.locator(EMPLOYEE_TABLE + " tbody tr").count();
    }

    public boolean hasEmployeeWithName(String name) {
        return page.locator(EMPLOYEE_TABLE + " tbody tr:has-text('" + name + "')").count() > 0;
    }
}
