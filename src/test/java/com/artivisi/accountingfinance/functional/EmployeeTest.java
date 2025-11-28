package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.EmployeeDetailPage;
import com.artivisi.accountingfinance.functional.page.EmployeeFormPage;
import com.artivisi.accountingfinance.functional.page.EmployeeListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Employee Management (Phase 3.1)")
class EmployeeTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private EmployeeListPage listPage;
    private EmployeeFormPage formPage;
    private EmployeeDetailPage detailPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new EmployeeListPage(page, baseUrl());
        formPage = new EmployeeFormPage(page, baseUrl());
        detailPage = new EmployeeDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should display employee list page")
    void shouldDisplayEmployeeListPage() {
        listPage.navigate();

        listPage.assertPageTitleVisible();
        listPage.assertPageTitleText("Daftar Karyawan");
    }

    @Test
    @DisplayName("Should display employee table")
    void shouldDisplayEmployeeTable() {
        listPage.navigate();

        listPage.assertTableVisible();
    }

    @Test
    @DisplayName("Should display new employee form")
    void shouldDisplayNewEmployeeForm() {
        formPage.navigateToNew();

        formPage.assertPageTitleText("Karyawan Baru");
    }

    @Test
    @DisplayName("Should navigate to form from list page")
    void shouldNavigateToFormFromListPage() {
        listPage.navigate();
        listPage.clickNewEmployeeButton();

        formPage.assertPageTitleText("Karyawan Baru");
    }

    @Test
    @DisplayName("Should create new employee")
    void shouldCreateNewEmployee() {
        formPage.navigateToNew();

        String uniqueId = "EMP" + System.currentTimeMillis() % 100000;
        String uniqueName = "Test Employee " + System.currentTimeMillis();

        formPage.fillEmployeeId(uniqueId);
        formPage.fillName(uniqueName);
        formPage.fillEmail("test@example.com");
        formPage.fillPhone("081234567890");
        formPage.fillHireDate("2025-01-15");
        formPage.selectPtkpStatus("TK_0");
        formPage.selectEmploymentType("PERMANENT");
        formPage.selectEmploymentStatus("ACTIVE");
        formPage.fillJobTitle("Software Developer");
        formPage.fillDepartment("IT");
        formPage.clickSubmit();

        // Should redirect to detail page
        detailPage.assertEmployeeNameText(uniqueName);
        detailPage.assertEmployeeIdText(uniqueId);
    }

    @Test
    @DisplayName("Should show employee in list after creation")
    void shouldShowEmployeeInListAfterCreation() {
        formPage.navigateToNew();

        String uniqueId = "EMLST" + System.currentTimeMillis() % 100000;
        String uniqueName = "List Test " + System.currentTimeMillis();

        formPage.fillEmployeeId(uniqueId);
        formPage.fillName(uniqueName);
        formPage.fillHireDate("2025-01-15");
        formPage.clickSubmit();

        // Navigate to list and search
        listPage.navigate();
        listPage.search(uniqueId);

        assertThat(listPage.hasEmployeeWithName(uniqueName)).isTrue();
    }

    @Test
    @DisplayName("Should create employee with tax information")
    void shouldCreateEmployeeWithTaxInfo() {
        formPage.navigateToNew();

        String uniqueId = "EMTAX" + System.currentTimeMillis() % 100000;
        String uniqueName = "Tax Test " + System.currentTimeMillis();

        formPage.fillEmployeeId(uniqueId);
        formPage.fillName(uniqueName);
        formPage.fillHireDate("2025-01-15");
        formPage.fillNikKtp("3201234567890001");
        formPage.fillNpwp("12.345.678.9-012.345");
        formPage.selectPtkpStatus("K_1");
        formPage.clickSubmit();

        // Should redirect to detail page
        detailPage.assertEmployeeNameText(uniqueName);
    }

    @Test
    @DisplayName("Should create employee with bank account")
    void shouldCreateEmployeeWithBankAccount() {
        formPage.navigateToNew();

        String uniqueId = "EMBNK" + System.currentTimeMillis() % 100000;
        String uniqueName = "Bank Test " + System.currentTimeMillis();

        formPage.fillEmployeeId(uniqueId);
        formPage.fillName(uniqueName);
        formPage.fillHireDate("2025-01-15");
        formPage.fillBankName("BCA");
        formPage.fillBankAccountNumber("1234567890");
        formPage.fillBankAccountName(uniqueName);
        formPage.clickSubmit();

        // Should redirect to detail page
        detailPage.assertEmployeeNameText(uniqueName);
    }

    @Test
    @DisplayName("Should deactivate active employee")
    void shouldDeactivateActiveEmployee() {
        // Create an employee first
        formPage.navigateToNew();

        String uniqueId = "EMDEACT" + System.currentTimeMillis() % 100000;
        String uniqueName = "Deactivate Test " + System.currentTimeMillis();

        formPage.fillEmployeeId(uniqueId);
        formPage.fillName(uniqueName);
        formPage.fillHireDate("2025-01-15");
        formPage.clickSubmit();

        // Should be active by default
        detailPage.assertStatusText("Aktif");
        assertThat(detailPage.hasDeactivateButton()).isTrue();

        // Deactivate
        detailPage.clickDeactivateButton();

        // Should show inactive status
        detailPage.assertStatusText("Nonaktif");
        assertThat(detailPage.hasActivateButton()).isTrue();
    }

    @Test
    @DisplayName("Should activate inactive employee")
    void shouldActivateInactiveEmployee() {
        // Create and deactivate an employee first
        formPage.navigateToNew();

        String uniqueId = "EMACT" + System.currentTimeMillis() % 100000;
        String uniqueName = "Activate Test " + System.currentTimeMillis();

        formPage.fillEmployeeId(uniqueId);
        formPage.fillName(uniqueName);
        formPage.fillHireDate("2025-01-15");
        formPage.clickSubmit();

        detailPage.clickDeactivateButton();
        detailPage.assertStatusText("Nonaktif");

        // Activate
        detailPage.clickActivateButton();

        // Should show active status
        detailPage.assertStatusText("Aktif");
        assertThat(detailPage.hasDeactivateButton()).isTrue();
    }
}
