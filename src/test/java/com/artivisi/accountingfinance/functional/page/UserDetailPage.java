package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UserDetailPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String USER_FULLNAME = "h2";
    private static final String ACTIVE_BADGE = "span:has-text('Aktif')";
    private static final String INACTIVE_BADGE = "span:has-text('Nonaktif')";

    public UserDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void assertUserFullNameText(String expected) {
        assertThat(page.locator(USER_FULLNAME).first()).containsText(expected);
    }

    public void assertActiveStatus() {
        assertThat(page.locator(ACTIVE_BADGE).first()).isVisible();
    }

    public void assertInactiveStatus() {
        assertThat(page.locator(INACTIVE_BADGE).first()).isVisible();
    }

    public boolean hasRole(String roleName) {
        return page.locator("span:has-text('" + roleName + "')").count() > 0;
    }

    public void clickEditButton() {
        page.locator("a:has-text('Edit')").click();
    }

    public void clickToggleActiveButton() {
        page.locator("button:has-text('Nonaktifkan'), button:has-text('Aktifkan')").click();
    }
}
