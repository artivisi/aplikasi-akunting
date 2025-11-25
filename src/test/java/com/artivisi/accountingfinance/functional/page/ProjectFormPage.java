package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String CODE_INPUT = "#code";
    private static final String NAME_INPUT = "#name";
    private static final String CLIENT_SELECT = "#clientId";
    private static final String CONTRACT_VALUE_INPUT = "#contractValue";
    private static final String BUDGET_INPUT = "#budgetAmount";
    private static final String START_DATE_INPUT = "#startDate";
    private static final String END_DATE_INPUT = "#endDate";
    private static final String DESCRIPTION_INPUT = "#description";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public ProjectFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ProjectFormPage navigateToNew() {
        page.navigate(baseUrl + "/projects/new");
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

    public void selectClient(String clientName) {
        page.selectOption(CLIENT_SELECT, new com.microsoft.playwright.options.SelectOption().setLabel(clientName));
    }

    public void selectClientByIndex(int index) {
        page.selectOption(CLIENT_SELECT, new com.microsoft.playwright.options.SelectOption().setIndex(index));
    }

    public void fillContractValue(String value) {
        page.fill(CONTRACT_VALUE_INPUT, value);
    }

    public void fillBudget(String value) {
        page.fill(BUDGET_INPUT, value);
    }

    public void fillStartDate(String date) {
        page.fill(START_DATE_INPUT, date);
    }

    public void fillEndDate(String date) {
        page.fill(END_DATE_INPUT, date);
    }

    public void fillDescription(String description) {
        page.fill(DESCRIPTION_INPUT, description);
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }
}
