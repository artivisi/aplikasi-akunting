package com.artivisi.accountingfinance.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Login and Dashboard UI Tests")
class LoginAndDashboardTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display login page")
    void shouldDisplayLoginPage() {
        navigateTo("/login");
        waitForPageLoad();

        assertThat(page.locator("input[name='username']")).isVisible();
        assertThat(page.locator("input[name='password']")).isVisible();
        assertThat(page.locator("button[type='submit']")).isVisible();

        takeScreenshot("01-login-page");
    }

    @Test
    @DisplayName("Should login successfully and redirect to dashboard")
    void shouldLoginSuccessfully() {
        loginAsAdmin();
        waitForPageLoad();

        assertThat(page).hasURL(baseUrl() + "/dashboard");
        assertThat(page.locator("text=Dashboard")).isVisible();

        takeScreenshot("02-dashboard");
    }

    @Test
    @DisplayName("Should display dashboard with all sections")
    void shouldDisplayDashboardSections() {
        loginAsAdmin();
        waitForPageLoad();

        // Summary cards
        assertThat(page.locator("text=Pendapatan")).isVisible();
        assertThat(page.locator("text=Pengeluaran")).isVisible();
        assertThat(page.locator("text=Laba Bersih")).isVisible();

        // Recent transactions
        assertThat(page.locator("text=Transaksi Terbaru")).isVisible();

        // Quick actions
        assertThat(page.locator("text=Aksi Cepat")).isVisible();

        // Bank balances
        assertThat(page.locator("text=Saldo Kas")).isVisible();

        takeScreenshot("03-dashboard-sections");
    }

    @Test
    @DisplayName("Should navigate to Chart of Accounts")
    void shouldNavigateToChartOfAccounts() {
        loginAsAdmin();
        waitForPageLoad();

        page.click("a:has-text('Akun')");
        waitForPageLoad();

        assertThat(page).hasURL(baseUrl() + "/accounts");
        assertThat(page.locator("text=Bagan Akun")).isVisible();

        takeScreenshot("04-chart-of-accounts");
    }

    @Test
    @DisplayName("Should display account form for new account")
    void shouldDisplayNewAccountForm() {
        loginAsAdmin();
        navigateTo("/accounts/new");
        waitForPageLoad();

        assertThat(page.locator("text=Tambah Akun")).isVisible();
        assertThat(page.locator("text=Panduan Pengisian")).isVisible();
        assertThat(page.locator("input[name='code']")).isVisible();
        assertThat(page.locator("input[name='name']")).isVisible();

        takeScreenshot("05-new-account-form");
    }

    @Test
    @DisplayName("Should expand help panel on new account form")
    void shouldExpandHelpPanel() {
        loginAsAdmin();
        navigateTo("/accounts/new");
        waitForPageLoad();

        // Help panel should be expanded by default for new accounts
        assertThat(page.locator("text=Format Kode Akun")).isVisible();
        assertThat(page.locator("text=Saldo Normal")).isVisible();

        takeScreenshot("06-help-panel-expanded");
    }

    @Test
    @DisplayName("Should show tooltip on help icon click")
    void shouldShowTooltipOnClick() {
        loginAsAdmin();
        navigateTo("/accounts/new");
        waitForPageLoad();

        // Click the help icon next to "Kode Akun"
        page.locator("label:has-text('Kode Akun') + div button").first().click();

        // Tooltip should appear
        assertThat(page.locator("text=Format: X.X.XX")).isVisible();

        takeScreenshot("07-tooltip-visible");
    }

    @Test
    @DisplayName("Take mobile viewport screenshots")
    void takeMobileScreenshots() {
        loginAsAdmin();
        waitForPageLoad();

        // Mobile viewport
        page.setViewportSize(375, 812);

        navigateTo("/dashboard");
        waitForPageLoad();
        takeScreenshot("mobile-01-dashboard");

        navigateTo("/accounts");
        waitForPageLoad();
        takeScreenshot("mobile-02-accounts");

        navigateTo("/accounts/new");
        waitForPageLoad();
        takeScreenshot("mobile-03-new-account");

        // Reset viewport
        page.setViewportSize(1920, 1080);
    }
}
