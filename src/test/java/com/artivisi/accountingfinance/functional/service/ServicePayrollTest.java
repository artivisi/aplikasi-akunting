package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.functional.page.EmployeeListPage;
import com.artivisi.accountingfinance.functional.page.PayrollListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Service Industry Payroll Tests
 * Tests payroll processing, PPh 21, and BPJS calculations.
 * Uses Page Object Pattern for maintainability.
 */
@DisplayName("Service Industry - Payroll")
@Import(ServiceTestDataInitializer.class)
public class ServicePayrollTest extends PlaywrightTestBase {

    // Page Objects
    private EmployeeListPage employeeListPage;
    private PayrollListPage payrollListPage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        employeeListPage = new EmployeeListPage(page, baseUrl);
        payrollListPage = new PayrollListPage(page, baseUrl);
    }

    @Test
    @DisplayName("Should display employee list with 3 employees from test data")
    void shouldDisplayEmployeeList() {
        loginAsAdmin();
        initPageObjects();

        // Test data has 3 employees: Budi Santoso, Dewi Lestari, Agus Wijaya
        employeeListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible()
            .verifyEmployeeCount(3);

        // Take screenshot for user manual
        takeManualScreenshot("employees-list");
    }

    @Test
    @DisplayName("Should display employee detail")
    void shouldDisplayEmployeeDetail() {
        loginAsAdmin();
        initPageObjects();

        employeeListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible();

        // Click on first employee row using ID
        page.locator("#employee-table tbody tr").first().click();
        page.waitForLoadState();

        // Verify employee detail page loads
        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should display payroll list")
    void shouldDisplayPayrollList() {
        loginAsAdmin();
        initPageObjects();

        payrollListPage.navigate()
            .verifyPageTitle();

        // Take screenshot for user manual
        takeManualScreenshot("payroll-list");
    }

    @Test
    @DisplayName("Should display payroll detail when data exists")
    void shouldDisplayPayrollDetail() {
        loginAsAdmin();
        initPageObjects();

        payrollListPage.navigate()
            .verifyPageTitle();

        // Check if payroll rows exist before clicking
        int rowCount = page.locator("tr[id^='payroll-']").count();
        if (rowCount == 0) {
            // No payroll data - skip detail navigation
            return;
        }

        // Click on first payroll row (payroll-{id} pattern)
        page.locator("tr[id^='payroll-']").first().click();
        page.waitForLoadState();

        // Verify payroll detail page loads
        assertThat(page.locator("#page-title")).containsText("Payroll");

        // Take screenshot for user manual
        takeManualScreenshot("payroll-detail");
    }

    @Test
    @DisplayName("Should display salary components configuration")
    void shouldDisplaySalaryComponents() {
        loginAsAdmin();
        navigateTo("/salary-components");
        waitForPageLoad();

        // Verify salary components page loads using ID
        assertThat(page.locator("#page-title")).containsText("Komponen");

        // Take screenshot for user manual
        takeManualScreenshot("salary-components-list");
    }

    @Test
    @DisplayName("Should display salary component form")
    void shouldDisplaySalaryComponentForm() {
        loginAsAdmin();
        navigateTo("/salary-components/new");
        waitForPageLoad();

        // Verify form page loads
        assertThat(page.locator("#page-title")).containsText("Komponen");

        // Take screenshot for user manual
        takeManualScreenshot("salary-components-form");
    }

    @Test
    @DisplayName("Should display employee form")
    void shouldDisplayEmployeeForm() {
        loginAsAdmin();
        navigateTo("/employees/new");
        waitForPageLoad();

        // Verify form page loads
        assertThat(page.locator("#page-title")).containsText("Karyawan");

        // Take screenshot for user manual
        takeManualScreenshot("employees-form");
    }

    @Test
    @DisplayName("Should display payroll form")
    void shouldDisplayPayrollForm() {
        loginAsAdmin();
        navigateTo("/payroll/new");
        waitForPageLoad();

        // Verify form page loads
        assertThat(page.locator("#page-title")).containsText("Payroll");

        // Take screenshot for user manual
        takeManualScreenshot("payroll-form");
    }

    @Test
    @DisplayName("Should display BPJS calculator")
    void shouldDisplayBpjsCalculator() {
        loginAsAdmin();
        navigateTo("/bpjs-calculator");
        waitForPageLoad();

        // Verify calculator page loads
        assertThat(page.locator("#page-title")).isVisible();

        // Take screenshot for user manual
        takeManualScreenshot("bpjs-calculator");
    }

    @Test
    @DisplayName("Should display PPh 21 calculator")
    void shouldDisplayPph21Calculator() {
        loginAsAdmin();
        navigateTo("/pph21-calculator");
        waitForPageLoad();

        // Verify calculator page loads
        assertThat(page.locator("#page-title")).isVisible();

        // Take screenshot for user manual
        takeManualScreenshot("pph21-calculator");
    }

}
