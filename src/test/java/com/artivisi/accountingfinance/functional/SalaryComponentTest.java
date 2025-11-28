package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.SalaryComponentDetailPage;
import com.artivisi.accountingfinance.functional.page.SalaryComponentFormPage;
import com.artivisi.accountingfinance.functional.page.SalaryComponentListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Salary Component Management (Phase 3.2)")
class SalaryComponentTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private SalaryComponentListPage listPage;
    private SalaryComponentFormPage formPage;
    private SalaryComponentDetailPage detailPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new SalaryComponentListPage(page, baseUrl());
        formPage = new SalaryComponentFormPage(page, baseUrl());
        detailPage = new SalaryComponentDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should display salary component list page")
    void shouldDisplaySalaryComponentListPage() {
        listPage.navigate();

        listPage.assertPageTitleVisible();
        listPage.assertPageTitleText("Komponen Gaji");
    }

    @Test
    @DisplayName("Should display salary component table with seed data")
    void shouldDisplaySalaryComponentTableWithSeedData() {
        listPage.navigate();

        listPage.assertTableVisible();
        // Check for seed data - Gaji Pokok should exist
        assertThat(listPage.hasComponentWithCode("GAPOK")).isTrue();
    }

    @Test
    @DisplayName("Should display new salary component form")
    void shouldDisplayNewSalaryComponentForm() {
        formPage.navigateToNew();

        formPage.assertPageTitleText("Komponen Gaji Baru");
    }

    @Test
    @DisplayName("Should navigate to form from list page")
    void shouldNavigateToFormFromListPage() {
        listPage.navigate();
        listPage.clickNewComponentButton();

        formPage.assertPageTitleText("Komponen Gaji Baru");
    }

    @Test
    @DisplayName("Should create new earning component with fixed amount")
    void shouldCreateNewEarningComponentWithFixedAmount() {
        formPage.navigateToNew();

        String uniqueCode = "TST" + System.currentTimeMillis() % 100000;
        String uniqueName = "Test Komponen " + System.currentTimeMillis();

        formPage.fillCode(uniqueCode);
        formPage.fillName(uniqueName);
        formPage.fillDescription("Komponen test untuk functional test");
        formPage.selectComponentType("EARNING");
        formPage.selectFixedAmount();
        formPage.fillDefaultAmount("1000000");
        formPage.fillDisplayOrder("100");
        formPage.checkTaxable();
        formPage.clickSubmit();

        // Should redirect to detail page
        detailPage.assertComponentCodeText(uniqueCode);
        detailPage.assertComponentNameText(uniqueName);
        detailPage.assertComponentTypeText("Pendapatan");
        detailPage.assertValueTypeText("Nominal Tetap");
        detailPage.assertDefaultAmountText("Rp 1,000,000");
    }

    @Test
    @DisplayName("Should create new deduction component with percentage")
    void shouldCreateNewDeductionComponentWithPercentage() {
        formPage.navigateToNew();

        String uniqueCode = "PTST" + System.currentTimeMillis() % 100000;
        String uniqueName = "Potongan Test " + System.currentTimeMillis();

        formPage.fillCode(uniqueCode);
        formPage.fillName(uniqueName);
        formPage.selectComponentType("DEDUCTION");
        formPage.selectPercentage();
        formPage.fillDefaultRate("5.5");
        formPage.clickSubmit();

        // Should redirect to detail page
        detailPage.assertComponentCodeText(uniqueCode);
        detailPage.assertComponentNameText(uniqueName);
        detailPage.assertComponentTypeText("Potongan");
    }

    @Test
    @DisplayName("Should show component in list after creation")
    void shouldShowComponentInListAfterCreation() {
        formPage.navigateToNew();

        String uniqueCode = "LST" + System.currentTimeMillis() % 100000;
        String uniqueName = "List Test " + System.currentTimeMillis();

        formPage.fillCode(uniqueCode);
        formPage.fillName(uniqueName);
        formPage.selectComponentType("EARNING");
        formPage.selectFixedAmount();
        formPage.clickSubmit();

        // Navigate to list and search
        listPage.navigate();
        listPage.search(uniqueCode);

        assertThat(listPage.hasComponentWithCode(uniqueCode)).isTrue();
    }

    @Test
    @DisplayName("Should filter by component type")
    void shouldFilterByComponentType() {
        listPage.navigate();

        // Filter by DEDUCTION type
        listPage.selectType("DEDUCTION");

        // Should have deduction components (from seed data)
        assertThat(listPage.hasComponentWithCode("BPJS-KES-K")).isTrue();
    }

    @Test
    @DisplayName("Should view seed data component details")
    void shouldViewSeedDataComponentDetails() {
        listPage.navigate();
        listPage.clickViewLink("GAPOK");

        detailPage.assertComponentCodeText("GAPOK");
        detailPage.assertComponentNameText("Gaji Pokok");
        detailPage.assertComponentTypeText("Pendapatan");
        detailPage.assertTaxableText("Ya");
    }

    @Test
    @DisplayName("Should deactivate active component")
    void shouldDeactivateActiveComponent() {
        // Create a component first
        formPage.navigateToNew();

        String uniqueCode = "DEACT" + System.currentTimeMillis() % 100000;
        String uniqueName = "Deactivate Test " + System.currentTimeMillis();

        formPage.fillCode(uniqueCode);
        formPage.fillName(uniqueName);
        formPage.selectComponentType("EARNING");
        formPage.selectFixedAmount();
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
    @DisplayName("Should activate inactive component")
    void shouldActivateInactiveComponent() {
        // Create and deactivate a component first
        formPage.navigateToNew();

        String uniqueCode = "ACT" + System.currentTimeMillis() % 100000;
        String uniqueName = "Activate Test " + System.currentTimeMillis();

        formPage.fillCode(uniqueCode);
        formPage.fillName(uniqueName);
        formPage.selectComponentType("EARNING");
        formPage.selectFixedAmount();
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
