package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UserFormPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String USERNAME_INPUT = "#username";
    private static final String PASSWORD_INPUT = "#password";
    private static final String FULLNAME_INPUT = "#fullName";
    private static final String EMAIL_INPUT = "#email";
    private static final String SAVE_BUTTON = "#btn-save";

    public UserFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public UserFormPage navigateToNew() {
        page.navigate(baseUrl + "/users/new");
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expected);
    }

    public void fillUsername(String username) {
        page.fill(USERNAME_INPUT, username);
    }

    public void fillPassword(String password) {
        page.fill(PASSWORD_INPUT, password);
    }

    public void fillFullName(String fullName) {
        page.fill(FULLNAME_INPUT, fullName);
    }

    public void fillEmail(String email) {
        page.fill(EMAIL_INPUT, email);
    }

    public void selectRole(String roleName) {
        page.locator("input[name='selectedRoles'][value='" + roleName + "']").check();
    }

    public void clickSubmit() {
        page.click(SAVE_BUTTON);
    }
}
