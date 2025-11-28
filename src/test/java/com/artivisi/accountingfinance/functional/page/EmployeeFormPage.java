package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String EMPLOYEE_ID_INPUT = "#employeeId";
    private static final String NAME_INPUT = "#name";
    private static final String EMAIL_INPUT = "#email";
    private static final String PHONE_INPUT = "#phone";
    private static final String NIK_KTP_INPUT = "#nikKtp";
    private static final String NPWP_INPUT = "#npwp";
    private static final String PTKP_STATUS_SELECT = "#ptkpStatus";
    private static final String HIRE_DATE_INPUT = "#hireDate";
    private static final String EMPLOYMENT_TYPE_SELECT = "#employmentType";
    private static final String EMPLOYMENT_STATUS_SELECT = "#employmentStatus";
    private static final String JOB_TITLE_INPUT = "#jobTitle";
    private static final String DEPARTMENT_INPUT = "#department";
    private static final String BANK_NAME_INPUT = "#bankName";
    private static final String BANK_ACCOUNT_NUMBER_INPUT = "#bankAccountNumber";
    private static final String BANK_ACCOUNT_NAME_INPUT = "#bankAccountName";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public EmployeeFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public EmployeeFormPage navigateToNew() {
        page.navigate(baseUrl + "/employees/new");
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillEmployeeId(String employeeId) {
        page.fill(EMPLOYEE_ID_INPUT, employeeId);
    }

    public void fillName(String name) {
        page.fill(NAME_INPUT, name);
    }

    public void fillEmail(String email) {
        page.fill(EMAIL_INPUT, email);
    }

    public void fillPhone(String phone) {
        page.fill(PHONE_INPUT, phone);
    }

    public void fillNikKtp(String nikKtp) {
        page.fill(NIK_KTP_INPUT, nikKtp);
    }

    public void fillNpwp(String npwp) {
        page.fill(NPWP_INPUT, npwp);
    }

    public void selectPtkpStatus(String status) {
        page.selectOption(PTKP_STATUS_SELECT, status);
    }

    public void fillHireDate(String date) {
        page.fill(HIRE_DATE_INPUT, date);
    }

    public void selectEmploymentType(String type) {
        page.selectOption(EMPLOYMENT_TYPE_SELECT, type);
    }

    public void selectEmploymentStatus(String status) {
        page.selectOption(EMPLOYMENT_STATUS_SELECT, status);
    }

    public void fillJobTitle(String jobTitle) {
        page.fill(JOB_TITLE_INPUT, jobTitle);
    }

    public void fillDepartment(String department) {
        page.fill(DEPARTMENT_INPUT, department);
    }

    public void fillBankName(String bankName) {
        page.fill(BANK_NAME_INPUT, bankName);
    }

    public void fillBankAccountNumber(String accountNumber) {
        page.fill(BANK_ACCOUNT_NUMBER_INPUT, accountNumber);
    }

    public void fillBankAccountName(String accountName) {
        page.fill(BANK_ACCOUNT_NAME_INPUT, accountName);
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }

    public boolean hasValidationError() {
        return page.locator(".text-red-500, .border-red-500").count() > 0;
    }
}
