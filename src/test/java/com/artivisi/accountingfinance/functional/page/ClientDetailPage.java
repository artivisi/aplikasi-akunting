package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientDetailPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CLIENT_DETAIL = "[data-testid='client-detail']";
    private static final String CLIENT_NAME = "[data-testid='client-detail'] h2";
    private static final String CLIENT_CODE = "[data-testid='client-detail'] p.font-mono";
    private static final String DEACTIVATE_BUTTON = "button:has-text('Nonaktifkan')";
    private static final String ACTIVATE_BUTTON = "button:has-text('Aktifkan')";

    public ClientDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ClientDetailPage navigate(String clientId) {
        page.navigate(baseUrl + "/clients/" + clientId);
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertClientNameText(String expected) {
        assertThat(page.locator(CLIENT_NAME).textContent()).contains(expected);
    }

    public void assertClientCodeText(String expected) {
        assertThat(page.locator(CLIENT_CODE).textContent()).contains(expected);
    }

    public void assertStatusText(String expected) {
        assertThat(page.locator(CLIENT_DETAIL).textContent()).contains(expected);
    }

    public void clickDeactivateButton() {
        page.onceDialog(dialog -> dialog.accept());
        page.click(DEACTIVATE_BUTTON);
        page.waitForLoadState();
    }

    public void clickActivateButton() {
        page.click(ACTIVATE_BUTTON);
        page.waitForLoadState();
    }

    public boolean hasDeactivateButton() {
        return page.locator(DEACTIVATE_BUTTON).count() > 0;
    }

    public boolean hasActivateButton() {
        return page.locator(ACTIVATE_BUTTON).count() > 0;
    }
}
