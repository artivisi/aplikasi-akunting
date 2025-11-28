package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UserListPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String USER_TABLE = "table";
    private static final String NEW_USER_BUTTON = "#btn-new-user";
    private static final String SEARCH_INPUT = "input[name='search']";
    private static final String SEARCH_BUTTON = "button[type='submit']";

    public UserListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public UserListPage navigate() {
        page.navigate(baseUrl + "/users");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(USER_TABLE)).isVisible();
    }

    public void clickNewUserButton() {
        page.click(NEW_USER_BUTTON);
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.click(SEARCH_BUTTON);
        // Wait for page to reload with search results
        page.waitForLoadState();
    }

    public boolean hasUserWithUsername(String username) {
        return page.locator("span:has-text('" + username + "')").count() > 0;
    }

    public void clickUserDetailLink(String username) {
        page.locator("tr:has-text('" + username + "') a:has-text('Detail')").click();
    }
}
