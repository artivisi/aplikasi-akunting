package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CLIENT_TABLE = "[data-testid='client-table']";
    private static final String NEW_CLIENT_BUTTON = "#btn-new-client";
    private static final String SEARCH_INPUT = "#search-input";

    public ClientListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ClientListPage navigate() {
        page.navigate(baseUrl + "/clients");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(CLIENT_TABLE).isVisible()).isTrue();
    }

    public void clickNewClientButton() {
        page.click(NEW_CLIENT_BUTTON);
        page.waitForLoadState();
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.waitForTimeout(500);
    }

    public int getClientCount() {
        return page.locator(CLIENT_TABLE + " tbody tr[data-id]").count();
    }

    public boolean hasClientWithName(String name) {
        return page.locator(CLIENT_TABLE + " tbody tr:has-text('" + name + "')").count() > 0;
    }
}
