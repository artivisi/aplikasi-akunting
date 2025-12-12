package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.functional.page.BalanceSheetPage;
import com.artivisi.accountingfinance.functional.page.ChartOfAccountsPage;
import com.artivisi.accountingfinance.functional.page.IncomeStatementPage;
import com.artivisi.accountingfinance.functional.page.JournalTemplateListPage;
import com.artivisi.accountingfinance.functional.page.TransactionListPage;
import com.artivisi.accountingfinance.functional.page.TrialBalancePage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Service Industry Accounting Tests
 * Tests COA, templates, and transaction functionality for IT Services / PKP company.
 * Uses Page Object Pattern for maintainability.
 */
@DisplayName("Service Industry - Accounting")
@Import(ServiceTestDataInitializer.class)
public class ServiceAccountingTest extends PlaywrightTestBase {

    // Page Objects
    private ChartOfAccountsPage chartOfAccountsPage;
    private JournalTemplateListPage templateListPage;
    private TransactionListPage transactionListPage;
    private IncomeStatementPage incomeStatementPage;
    private BalanceSheetPage balanceSheetPage;
    private TrialBalancePage trialBalancePage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        chartOfAccountsPage = new ChartOfAccountsPage(page, baseUrl);
        templateListPage = new JournalTemplateListPage(page, baseUrl);
        transactionListPage = new TransactionListPage(page, baseUrl);
        incomeStatementPage = new IncomeStatementPage(page, baseUrl);
        balanceSheetPage = new BalanceSheetPage(page, baseUrl);
        trialBalancePage = new TrialBalancePage(page, baseUrl);
    }

    @Test
    @DisplayName("Should display Chart of Accounts with seed data")
    void shouldDisplayChartOfAccounts() {
        loginAsAdmin();
        initPageObjects();

        // IT Service seed has 75 accounts (76 lines - 1 header)
        chartOfAccountsPage.navigate()
            .verifyPageTitle()
            .verifyContentVisible()
            .verifyTableVisible()
            .verifyMinimumAccountCount(70)  // At least 70 accounts from seed
            .verifyAccountExists("1.1.01")  // Kas
            .verifyAccountExists("4.1.01")  // Pendapatan Jasa Konsultasi
            .verifyAccountExists("5.2.01"); // Beban Software & Lisensi

        // Take screenshot for user manual (generic version)
        takeManualScreenshot("accounts-list");
    }

    @Test
    @DisplayName("Should display Journal Templates")
    void shouldDisplayJournalTemplates() {
        loginAsAdmin();
        initPageObjects();

        templateListPage.navigate()
            .verifyPageTitle()
            .verifyContentVisible();
    }

    @Test
    @DisplayName("Should display Transaction List")
    void shouldDisplayTransactionList() {
        loginAsAdmin();
        initPageObjects();

        transactionListPage.navigate()
            .verifyPageTitle()
            .verifyContentVisible();
    }

    @Test
    @DisplayName("Should display Income Statement Report")
    void shouldDisplayIncomeStatement() {
        loginAsAdmin();
        initPageObjects();

        incomeStatementPage.navigate()
            .verifyPageTitle();
    }

    @Test
    @DisplayName("Should display Balance Sheet Report")
    void shouldDisplayBalanceSheet() {
        loginAsAdmin();
        initPageObjects();

        balanceSheetPage.navigate()
            .verifyPageTitle();
    }

    @Test
    @DisplayName("Should display Trial Balance Report")
    void shouldDisplayTrialBalance() {
        loginAsAdmin();
        initPageObjects();

        trialBalancePage.navigate()
            .verifyPageTitle();
    }

    @Test
    @DisplayName("Should display account form")
    void shouldDisplayAccountForm() {
        loginAsAdmin();
        navigateTo("/accounts/new");
        waitForPageLoad();

        // Verify account form page loads
        assertThat(page.locator("#page-title")).containsText("Akun");

        // Take screenshot for user manual
        takeManualScreenshot("accounts-form");
    }

    @Test
    @DisplayName("Should display transaction form")
    void shouldDisplayTransactionForm() {
        loginAsAdmin();
        navigateTo("/transactions/new");
        waitForPageLoad();

        // Verify transaction form page loads
        assertThat(page.locator("#page-title")).containsText("Transaksi");

        // Take screenshot for user manual
        takeManualScreenshot("transactions-form");
    }

    @Test
    @DisplayName("Should display fiscal periods list")
    void shouldDisplayFiscalPeriodsList() {
        loginAsAdmin();
        navigateTo("/fiscal-periods");
        waitForPageLoad();

        // Verify fiscal periods page loads
        assertThat(page.locator("#page-title")).containsText("Periode");

        // Take screenshot for user manual
        takeManualScreenshot("fiscal-periods-list");
    }

    @Test
    @DisplayName("Should display amortization list")
    void shouldDisplayAmortizationList() {
        loginAsAdmin();
        navigateTo("/amortization");
        waitForPageLoad();

        // Verify amortization page loads
        assertThat(page.locator("#page-title")).containsText("Amortisasi");

        // Take screenshot for user manual
        takeManualScreenshot("amortization-list");
    }

    @Test
    @DisplayName("Should display amortization form")
    void shouldDisplayAmortizationForm() {
        loginAsAdmin();
        navigateTo("/amortization/new");
        waitForPageLoad();

        // Verify amortization form page loads
        assertThat(page.locator("#page-title")).containsText("Amortisasi");

        // Take screenshot for user manual
        takeManualScreenshot("amortization-form");
    }

}
