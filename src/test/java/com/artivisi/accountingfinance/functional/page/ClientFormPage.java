package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CODE_INPUT = "#code";
    private static final String NAME_INPUT = "#name";
    private static final String CONTACT_INPUT = "#contactPerson";
    private static final String EMAIL_INPUT = "#email";
    private static final String PHONE_INPUT = "#phone";
    private static final String ADDRESS_INPUT = "#address";
    private static final String NOTES_INPUT = "#notes";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public ClientFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ClientFormPage navigateToNew() {
        page.navigate(baseUrl + "/clients/new");
        page.waitForLoadState();
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillCode(String code) {
        page.fill(CODE_INPUT, code);
    }

    public void fillName(String name) {
        page.fill(NAME_INPUT, name);
    }

    public void fillContactPerson(String contact) {
        page.fill(CONTACT_INPUT, contact);
    }

    public void fillEmail(String email) {
        page.fill(EMAIL_INPUT, email);
    }

    public void fillPhone(String phone) {
        page.fill(PHONE_INPUT, phone);
    }

    public void fillAddress(String address) {
        page.fill(ADDRESS_INPUT, address);
    }

    public void fillNotes(String notes) {
        page.fill(NOTES_INPUT, notes);
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }
}
