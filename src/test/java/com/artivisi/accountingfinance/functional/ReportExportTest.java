package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Download;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Report Export Feature")
class ReportExportTest extends PlaywrightTestBase {

    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("Reports Index Page")
    class ReportsIndexTests {

        @Test
        @DisplayName("Should display reports index page")
        void shouldDisplayReportsIndexPage() {
            page.navigate(baseUrl() + "/reports");

            assertThat(page.locator("h1")).containsText("Laporan");
        }
    }

    @Nested
    @DisplayName("Trial Balance Export")
    class TrialBalanceExportTests {

        @Test
        @DisplayName("Should export trial balance to PDF")
        void shouldExportTrialBalanceToPdf() throws IOException {
            // First navigate to trial balance page
            page.navigate(baseUrl() + "/reports/trial-balance?asOfDate=2024-06-30");

            // Then click the export PDF link
            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/pdf']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("neraca-saldo-");
            assertThat(filename).endsWith(".pdf");

            // Verify file is not empty
            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);

            // Verify PDF magic bytes
            assertThat(content[0]).isEqualTo((byte) '%');
            assertThat(content[1]).isEqualTo((byte) 'P');
            assertThat(content[2]).isEqualTo((byte) 'D');
            assertThat(content[3]).isEqualTo((byte) 'F');
        }

        @Test
        @DisplayName("Should export trial balance to Excel")
        void shouldExportTrialBalanceToExcel() throws IOException {
            page.navigate(baseUrl() + "/reports/trial-balance?asOfDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/excel']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("neraca-saldo-");
            assertThat(filename).endsWith(".xlsx");

            // Verify file is not empty
            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);

            // Verify Excel magic bytes (PK for ZIP-based format)
            assertThat(content[0]).isEqualTo((byte) 'P');
            assertThat(content[1]).isEqualTo((byte) 'K');
        }
    }

    @Nested
    @DisplayName("Balance Sheet Export")
    class BalanceSheetExportTests {

        @Test
        @DisplayName("Should export balance sheet to PDF")
        void shouldExportBalanceSheetToPdf() throws IOException {
            page.navigate(baseUrl() + "/reports/balance-sheet?asOfDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/pdf']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("laporan-posisi-keuangan-");
            assertThat(filename).endsWith(".pdf");

            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);
            assertThat(content[0]).isEqualTo((byte) '%');
            assertThat(content[1]).isEqualTo((byte) 'P');
            assertThat(content[2]).isEqualTo((byte) 'D');
            assertThat(content[3]).isEqualTo((byte) 'F');
        }

        @Test
        @DisplayName("Should export balance sheet to Excel")
        void shouldExportBalanceSheetToExcel() throws IOException {
            page.navigate(baseUrl() + "/reports/balance-sheet?asOfDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/excel']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("laporan-posisi-keuangan-");
            assertThat(filename).endsWith(".xlsx");

            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);
            assertThat(content[0]).isEqualTo((byte) 'P');
            assertThat(content[1]).isEqualTo((byte) 'K');
        }
    }

    @Nested
    @DisplayName("Income Statement Export")
    class IncomeStatementExportTests {

        @Test
        @DisplayName("Should export income statement to PDF")
        void shouldExportIncomeStatementToPdf() throws IOException {
            page.navigate(baseUrl() + "/reports/income-statement?startDate=2024-01-01&endDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/pdf']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("laporan-laba-rugi-");
            assertThat(filename).endsWith(".pdf");

            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);
            assertThat(content[0]).isEqualTo((byte) '%');
            assertThat(content[1]).isEqualTo((byte) 'P');
            assertThat(content[2]).isEqualTo((byte) 'D');
            assertThat(content[3]).isEqualTo((byte) 'F');
        }

        @Test
        @DisplayName("Should export income statement to Excel")
        void shouldExportIncomeStatementToExcel() throws IOException {
            page.navigate(baseUrl() + "/reports/income-statement?startDate=2024-01-01&endDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/excel']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("laporan-laba-rugi-");
            assertThat(filename).endsWith(".xlsx");

            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);
            assertThat(content[0]).isEqualTo((byte) 'P');
            assertThat(content[1]).isEqualTo((byte) 'K');
        }
    }

    @Nested
    @DisplayName("Cash Flow Export")
    class CashFlowExportTests {

        @Test
        @DisplayName("Should export cash flow to PDF")
        void shouldExportCashFlowToPdf() throws IOException {
            page.navigate(baseUrl() + "/reports/cash-flow?startDate=2024-01-01&endDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/pdf']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("laporan-arus-kas-");
            assertThat(filename).endsWith(".pdf");

            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);
            assertThat(content[0]).isEqualTo((byte) '%');
            assertThat(content[1]).isEqualTo((byte) 'P');
            assertThat(content[2]).isEqualTo((byte) 'D');
            assertThat(content[3]).isEqualTo((byte) 'F');
        }

        @Test
        @DisplayName("Should export cash flow to Excel")
        void shouldExportCashFlowToExcel() throws IOException {
            page.navigate(baseUrl() + "/reports/cash-flow?startDate=2024-01-01&endDate=2024-06-30");

            Download download = page.waitForDownload(() -> {
                page.locator("a[href*='/export/excel']").click();
            });

            String filename = download.suggestedFilename();
            assertThat(filename).startsWith("laporan-arus-kas-");
            assertThat(filename).endsWith(".xlsx");

            byte[] content = Files.readAllBytes(download.path());
            assertThat(content.length).isGreaterThan(0);
            assertThat(content[0]).isEqualTo((byte) 'P');
            assertThat(content[1]).isEqualTo((byte) 'K');
        }
    }

    @Nested
    @DisplayName("Print Endpoints")
    class PrintEndpointTests {

        @Test
        @DisplayName("Should display trial balance print view")
        void shouldDisplayTrialBalancePrintView() {
            page.navigate(baseUrl() + "/reports/trial-balance/print?asOfDate=2024-06-30");

            // Print view should have report content (template uses "NERACA SALDO")
            assertThat(page.locator("body")).containsText("NERACA SALDO");
        }

        @Test
        @DisplayName("Should display balance sheet print view")
        void shouldDisplayBalanceSheetPrintView() {
            page.navigate(baseUrl() + "/reports/balance-sheet/print?asOfDate=2024-06-30");

            // Template uses "NERACA"
            assertThat(page.locator("body")).containsText("NERACA");
        }

        @Test
        @DisplayName("Should display income statement print view")
        void shouldDisplayIncomeStatementPrintView() {
            page.navigate(baseUrl() + "/reports/income-statement/print?startDate=2024-01-01&endDate=2024-06-30");

            // Template uses "LABA RUGI"
            assertThat(page.locator("body")).containsText("LABA RUGI");
        }

        @Test
        @DisplayName("Should display cash flow print view")
        void shouldDisplayCashFlowPrintView() {
            page.navigate(baseUrl() + "/reports/cash-flow/print?startDate=2024-01-01&endDate=2024-06-30");

            // Template uses "ARUS KAS"
            assertThat(page.locator("body")).containsText("ARUS KAS");
        }

        @Test
        @DisplayName("Should display PPN summary print view")
        void shouldDisplayPpnSummaryPrintView() {
            page.navigate(baseUrl() + "/reports/ppn-summary/print?startDate=2024-01-01&endDate=2024-06-30");

            // Template uses "RINGKASAN PPN"
            assertThat(page.locator("body")).containsText("RINGKASAN PPN");
        }

        @Test
        @DisplayName("Should display PPh23 withholding print view")
        void shouldDisplayPph23WithholdingPrintView() {
            page.navigate(baseUrl() + "/reports/pph23-withholding/print?startDate=2024-01-01&endDate=2024-06-30");

            // Template uses "PPh PASAL 23"
            assertThat(page.locator("body")).containsText("PPh PASAL 23");
        }
    }

    @Nested
    @DisplayName("API Endpoints")
    class ApiEndpointTests {

        @Test
        @DisplayName("Should return trial balance JSON via API")
        void shouldReturnTrialBalanceJsonViaApi() {
            page.navigate(baseUrl() + "/reports/api/trial-balance?asOfDate=2024-06-30");

            String content = page.locator("body").textContent();
            assertThat(content).contains("totalDebit");
            assertThat(content).contains("totalCredit");
        }

        @Test
        @DisplayName("Should return income statement JSON via API")
        void shouldReturnIncomeStatementJsonViaApi() {
            page.navigate(baseUrl() + "/reports/api/income-statement?startDate=2024-01-01&endDate=2024-06-30");

            String content = page.locator("body").textContent();
            assertThat(content).contains("totalRevenue");
            assertThat(content).contains("totalExpense");
            assertThat(content).contains("netIncome");
        }

        @Test
        @DisplayName("Should return balance sheet JSON via API")
        void shouldReturnBalanceSheetJsonViaApi() {
            page.navigate(baseUrl() + "/reports/api/balance-sheet?asOfDate=2024-06-30");

            String content = page.locator("body").textContent();
            assertThat(content).contains("totalAssets");
            assertThat(content).contains("totalLiabilities");
            assertThat(content).contains("totalEquity");
        }
    }

    @Nested
    @DisplayName("Tax Report API Endpoints")
    class TaxReportApiTests {

        @Test
        @DisplayName("Should return PPN summary JSON via API")
        void shouldReturnPpnSummaryJsonViaApi() {
            page.navigate(baseUrl() + "/reports/api/ppn-summary?startDate=2024-01-01&endDate=2024-06-30");

            String content = page.locator("body").textContent();
            assertThat(content).contains("ppnMasukan");
            assertThat(content).contains("ppnKeluaran");
        }

        @Test
        @DisplayName("Should return PPh23 withholding JSON via API")
        void shouldReturnPph23WithholdingJsonViaApi() {
            page.navigate(baseUrl() + "/reports/api/pph23-withholding?startDate=2024-01-01&endDate=2024-06-30");

            String content = page.locator("body").textContent();
            // API returns totalWithheld, totalDeposited, balance
            assertThat(content).contains("totalWithheld");
        }

        @Test
        @DisplayName("Should return tax summary JSON via API")
        void shouldReturnTaxSummaryJsonViaApi() {
            page.navigate(baseUrl() + "/reports/api/tax-summary?startDate=2024-01-01&endDate=2024-06-30");

            String content = page.locator("body").textContent();
            assertThat(content).contains("items");
        }
    }
}
