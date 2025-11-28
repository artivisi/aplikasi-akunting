package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeDetailPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String EMPLOYEE_DETAIL = "[data-testid='employee-detail']";
    private static final String EMPLOYEE_ID = "[data-testid='employee-id']";
    private static final String DEACTIVATE_BUTTON = "button:has-text('Nonaktifkan')";
    private static final String ACTIVATE_BUTTON = "button:has-text('Aktifkan')";
    private static final String EDIT_BUTTON = "a:has-text('Edit')";
    private static final String STATUS_BADGE = "[data-testid='active-status']";

    public EmployeeDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void assertEmployeeNameText(String expected) {
        assertThat(page.locator(EMPLOYEE_DETAIL + " h2").textContent()).contains(expected);
    }

    public void assertEmployeeIdText(String expected) {
        assertThat(page.locator(EMPLOYEE_ID).textContent()).contains(expected);
    }

    public void assertStatusText(String expected) {
        assertThat(page.locator(STATUS_BADGE).textContent()).contains(expected);
    }

    public boolean hasDeactivateButton() {
        return page.locator(DEACTIVATE_BUTTON).count() > 0;
    }

    public boolean hasActivateButton() {
        return page.locator(ACTIVATE_BUTTON).count() > 0;
    }

    public void clickDeactivateButton() {
        page.onDialog(dialog -> dialog.accept());
        page.click(DEACTIVATE_BUTTON);
        page.waitForLoadState();
    }

    public void clickActivateButton() {
        page.click(ACTIVATE_BUTTON);
        page.waitForLoadState();
    }

    public void clickEditButton() {
        page.click(EDIT_BUTTON);
        page.waitForLoadState();
    }
}
