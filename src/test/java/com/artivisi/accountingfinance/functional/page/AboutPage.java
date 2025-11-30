package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AboutPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_CONTENT = "#about-page-content";
    private static final String PAGE_TITLE = "h1";
    private static final String VERSION_SECTION = "#version-info";
    private static final String COMMIT_ID = "#commit-id";
    private static final String GIT_TAG = "#git-tag";

    public AboutPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void navigate() {
        page.navigate(baseUrl + "/about");
        page.waitForLoadState();
    }

    public void assertPageLoaded() {
        assertThat(page.locator(PAGE_CONTENT)).isVisible();
    }

    public void assertPageTitle(String expectedTitle) {
        assertThat(page.locator(PAGE_CONTENT + " " + PAGE_TITLE)).containsText(expectedTitle);
    }

    public void assertVersionSectionVisible() {
        assertThat(page.locator(VERSION_SECTION)).isVisible();
    }

    public void assertCommitIdVisible() {
        assertThat(page.locator(COMMIT_ID)).isVisible();
    }

    public String getCommitId() {
        return page.locator(COMMIT_ID).textContent().trim();
    }

    public boolean hasGitTag() {
        return page.locator(GIT_TAG).count() > 0 && page.locator(GIT_TAG).isVisible();
    }

    public String getGitTag() {
        return page.locator(GIT_TAG).textContent().trim();
    }
}
