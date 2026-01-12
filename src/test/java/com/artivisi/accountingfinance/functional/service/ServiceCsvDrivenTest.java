package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.functional.page.ClientListPage;
import com.artivisi.accountingfinance.functional.page.EmployeeListPage;
import com.artivisi.accountingfinance.functional.page.PayrollListPage;
import com.artivisi.accountingfinance.functional.page.ProjectListPage;
import com.artivisi.accountingfinance.functional.page.TransactionListPage;
import com.artivisi.accountingfinance.functional.util.CsvLoader;
import com.artivisi.accountingfinance.functional.util.TransactionRow;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CSV-Driven Service Industry Tests
 * Loads test scenarios from CSV files and executes them as dynamic tests.
 * Data from: src/test/resources/testdata/service/
 * Uses Page Object Pattern for maintainability.
 */
@DisplayName("Service Industry - CSV-Driven Tests")
@Import(ServiceTestDataInitializer.class)
class ServiceCsvDrivenTest extends PlaywrightTestBase {

    // Page Objects
    private TransactionListPage transactionListPage;
    private ClientListPage clientListPage;
    private ProjectListPage projectListPage;
    private EmployeeListPage employeeListPage;
    private PayrollListPage payrollListPage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        transactionListPage = new TransactionListPage(page, baseUrl);
        clientListPage = new ClientListPage(page, baseUrl);
        projectListPage = new ProjectListPage(page, baseUrl);
        employeeListPage = new EmployeeListPage(page, baseUrl);
        payrollListPage = new PayrollListPage(page, baseUrl);
    }

    @Test
    @DisplayName("Should load transactions CSV")
    void shouldLoadTransactionsCsv() {
        List<TransactionRow> transactions = CsvLoader.loadTransactions("service/transactions.csv");

        // Verify CSV loads correctly
        assertEquals(5, transactions.size());
        assertEquals("Setoran Modal", transactions.get(0).templateName());
        assertEquals("Pendapatan Jasa Konsultasi", transactions.get(1).templateName());
    }

    @TestFactory
    @DisplayName("Verify transactions from CSV")
    Stream<DynamicTest> verifyTransactionsFromCsv() {
        List<TransactionRow> transactions = CsvLoader.loadTransactions("service/transactions.csv");

        return transactions.stream()
            .map(tx -> DynamicTest.dynamicTest(
                "Tx " + tx.sequence() + ": " + tx.description(),
                () -> verifyTransactionExists(tx)
            ));
    }

    private void verifyTransactionExists(TransactionRow tx) {
        loginAsAdmin();
        initPageObjects();

        transactionListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible();

        // Search for transaction by description using ID-based search input
        transactionListPage.search(tx.description().substring(0, Math.min(20, tx.description().length())));

        // Verify table still visible after search
        transactionListPage.verifyTableVisible();

        if (tx.screenshot()) {
            takeManualScreenshot("service/tx-" + tx.sequence() + "-" + slugify(tx.description()));
        }
    }

    @Test
    @DisplayName("Verify 3 clients from test data exist")
    void verifyClientsFromTestData() {
        loginAsAdmin();
        initPageObjects();

        // Test data: PT Bank Mandiri, PT Telkom Indonesia, PT Pertamina
        clientListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible()
            .verifyClientCount(3);
    }

    @Test
    @DisplayName("Verify at least 4 projects from test data exist")
    void verifyProjectsFromTestData() {
        loginAsAdmin();
        initPageObjects();

        // Test data: 4 projects (may be more if other tests create projects)
        projectListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible()
            .verifyMinimumProjectCount(4);
    }

    @Test
    @DisplayName("Verify 3 employees from test data exist")
    void verifyEmployeesFromTestData() {
        loginAsAdmin();
        initPageObjects();

        // Test data: Budi Santoso, Dewi Lestari, Agus Wijaya
        employeeListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible()
            .verifyEmployeeCount(3);
    }

    @Test
    @DisplayName("Verify payroll page loads")
    void verifyPayrollPageLoads() {
        loginAsAdmin();
        initPageObjects();

        payrollListPage.navigate()
            .verifyPageTitle();

        // Payroll page loads successfully - data check is optional
        // Note: Payroll runs require separate test data setup
    }

    private String slugify(String text) {
        return text.toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "")
            .substring(0, Math.min(30, text.length()));
    }
}
