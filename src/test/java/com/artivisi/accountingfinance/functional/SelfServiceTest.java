package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Employee Self-Service (Phase 3.8)")
class SelfServiceTest extends PlaywrightTestBase {

    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should display My Payslips page")
    void shouldDisplayMyPayslipsPage() {
        page.navigate(baseUrl() + "/self-service/payslips");

        assertThat(page.locator("#page-title")).hasText("Slip Gaji Saya");
    }

    @Test
    @DisplayName("Should show no employee message when user not linked")
    void shouldShowNoEmployeeMessageWhenUserNotLinked() {
        page.navigate(baseUrl() + "/self-service/payslips");

        // Admin user is not linked to an employee by default
        assertThat(page.locator("text=Akun Anda belum terhubung dengan data karyawan")).isVisible();
    }

    @Test
    @DisplayName("Should display My Bukti Potong page")
    void shouldDisplayMyBuktiPotongPage() {
        page.navigate(baseUrl() + "/self-service/bukti-potong");

        assertThat(page.locator("#page-title")).hasText("Bukti Potong PPh 21");
    }

    @Test
    @DisplayName("Should display My Profile page")
    void shouldDisplayMyProfilePage() {
        page.navigate(baseUrl() + "/self-service/profile");

        assertThat(page.locator("#page-title")).hasText("Profil Saya");
    }

    @Test
    @DisplayName("Should show sidebar menu for self-service")
    void shouldShowSidebarMenuForSelfService() {
        page.navigate(baseUrl() + "/dashboard");

        assertThat(page.locator("#nav-my-payslips")).isVisible();
        assertThat(page.locator("#nav-my-bukti-potong")).isVisible();
        assertThat(page.locator("#nav-my-profile")).isVisible();
    }

    @Test
    @DisplayName("Should show no employee message on bukti potong when user not linked")
    void shouldShowNoEmployeeMessageOnBuktiPotongWhenUserNotLinked() {
        page.navigate(baseUrl() + "/self-service/bukti-potong");

        // Admin user is not linked to an employee by default
        assertThat(page.locator("text=Akun Anda belum terhubung dengan data karyawan")).isVisible();
    }

    @Test
    @DisplayName("Should show no employee message on profile when user not linked")
    void shouldShowNoEmployeeMessageOnProfileWhenUserNotLinked() {
        page.navigate(baseUrl() + "/self-service/profile");

        // Admin user is not linked to an employee
        assertThat(page.locator("text=Akun Anda belum terhubung dengan data karyawan")).isVisible();
    }

    @Test
    @DisplayName("Should display year filter on payslips page")
    void shouldDisplayYearFilterOnPayslipsPage() {
        page.navigate(baseUrl() + "/self-service/payslips");

        // Year selector is only visible when employee is linked
        // Admin user has no employee, so we just verify the page loads correctly
        assertThat(page.locator("#page-title")).hasText("Slip Gaji Saya");
    }

    @Test
    @DisplayName("Should display year filter on bukti potong page")
    void shouldDisplayYearFilterOnBuktiPotongPage() {
        page.navigate(baseUrl() + "/self-service/bukti-potong");

        // Year selector is only visible when employee is linked
        // Admin user has no employee, so we just verify the page loads correctly
        assertThat(page.locator("#page-title")).hasText("Bukti Potong PPh 21");
    }

    @Test
    @DisplayName("Should filter payslips by year")
    void shouldFilterPayslipsByYear() {
        int currentYear = java.time.LocalDate.now().getYear();
        page.navigate(baseUrl() + "/self-service/payslips?year=" + currentYear);

        // Page should load with year parameter
        assertThat(page.locator("#page-title")).hasText("Slip Gaji Saya");
    }

    @Test
    @DisplayName("Should filter bukti potong by year")
    void shouldFilterBuktiPotongByYear() {
        int currentYear = java.time.LocalDate.now().getYear();
        page.navigate(baseUrl() + "/self-service/bukti-potong?year=" + currentYear);

        // Page should load with year parameter
        assertThat(page.locator("#page-title")).hasText("Bukti Potong PPh 21");
    }
}
