package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentTermFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String SEQUENCE = "#sequence";
    private static final String NAME = "#name";
    private static final String DUE_TRIGGER = "#dueTrigger";
    private static final String MILESTONE = "#milestone";
    private static final String DUE_DATE = "#dueDate";
    private static final String PERCENTAGE = "#percentage";
    private static final String AMOUNT = "#amount";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public PaymentTermFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillSequence(String sequence) {
        page.fill(SEQUENCE, sequence);
    }

    public void fillName(String name) {
        page.fill(NAME, name);
    }

    public void selectDueTrigger(String trigger) {
        page.selectOption(DUE_TRIGGER, trigger);
    }

    public void selectMilestoneByIndex(int index) {
        page.selectOption(MILESTONE, new com.microsoft.playwright.options.SelectOption().setIndex(index));
    }

    public void fillDueDate(String date) {
        page.fill(DUE_DATE, date);
    }

    public void fillPercentage(String percentage) {
        page.fill(PERCENTAGE, percentage);
    }

    public void fillAmount(String amount) {
        page.fill(AMOUNT, amount);
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }
}
