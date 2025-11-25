package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String PROJECT_TABLE = "[data-testid='project-table']";
    private static final String NEW_PROJECT_BUTTON = "#btn-new-project";
    private static final String SEARCH_INPUT = "#search-input";

    public ProjectListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ProjectListPage navigate() {
        page.navigate(baseUrl + "/projects");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(PROJECT_TABLE).isVisible()).isTrue();
    }

    public void clickNewProjectButton() {
        page.click(NEW_PROJECT_BUTTON);
        page.waitForLoadState();
    }

    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.waitForTimeout(500);
    }

    public int getProjectCount() {
        return page.locator(PROJECT_TABLE + " tbody tr[data-id]").count();
    }

    public boolean hasProjectWithName(String name) {
        return page.locator(PROJECT_TABLE + " tbody tr:has-text('" + name + "')").count() > 0;
    }

    public void clickFirstProjectLink() {
        page.locator(PROJECT_TABLE + " tbody tr[data-id] a").first().click();
        page.waitForLoadState();
    }
}
