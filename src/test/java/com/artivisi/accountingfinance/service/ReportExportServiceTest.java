package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.enums.AccountType;
import com.artivisi.accountingfinance.enums.NormalBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReportExportService Tests")
class ReportExportServiceTest {

    private ReportExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new ReportExportService();
    }

    private ChartOfAccount createAccount(String code, String name, AccountType type, NormalBalance normalBalance) {
        ChartOfAccount account = new ChartOfAccount();
        account.setId(UUID.randomUUID());
        account.setAccountCode(code);
        account.setAccountName(name);
        account.setAccountType(type);
        account.setNormalBalance(normalBalance);
        account.setActive(true);
        account.setIsHeader(false);
        return account;
    }

    @Nested
    @DisplayName("Trial Balance Export")
    class TrialBalanceExportTests {

        @Test
        @DisplayName("Should generate valid PDF for Trial Balance")
        void shouldGenerateValidPdfForTrialBalance() {
            ChartOfAccount cash = createAccount("1.1.01", "Kas", AccountType.ASSET, NormalBalance.DEBIT);
            ChartOfAccount capital = createAccount("3.1.01", "Modal", AccountType.EQUITY, NormalBalance.CREDIT);

            ReportService.TrialBalanceItem item1 = new ReportService.TrialBalanceItem(
                    cash, new BigDecimal("100000000"), BigDecimal.ZERO);
            ReportService.TrialBalanceItem item2 = new ReportService.TrialBalanceItem(
                    capital, BigDecimal.ZERO, new BigDecimal("100000000"));

            ReportService.TrialBalanceReport report = new ReportService.TrialBalanceReport(
                    LocalDate.of(2024, 6, 30),
                    List.of(item1, item2),
                    new BigDecimal("100000000"),
                    new BigDecimal("100000000")
            );

            byte[] pdfBytes = exportService.exportTrialBalanceToPdf(report);

            assertThat(pdfBytes).isNotNull();
            assertThat(pdfBytes.length).isGreaterThan(0);
            // PDF files start with %PDF
            assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
        }

        @Test
        @DisplayName("Should generate valid Excel for Trial Balance")
        void shouldGenerateValidExcelForTrialBalance() {
            ChartOfAccount cash = createAccount("1.1.01", "Kas", AccountType.ASSET, NormalBalance.DEBIT);

            ReportService.TrialBalanceItem item = new ReportService.TrialBalanceItem(
                    cash, new BigDecimal("100000000"), BigDecimal.ZERO);

            ReportService.TrialBalanceReport report = new ReportService.TrialBalanceReport(
                    LocalDate.of(2024, 6, 30),
                    List.of(item),
                    new BigDecimal("100000000"),
                    BigDecimal.ZERO
            );

            byte[] excelBytes = exportService.exportTrialBalanceToExcel(report);

            assertThat(excelBytes).isNotNull();
            assertThat(excelBytes.length).isGreaterThan(0);
            // XLSX files start with PK (zip format)
            assertThat(new String(excelBytes, 0, 2)).isEqualTo("PK");
        }
    }

    @Nested
    @DisplayName("Balance Sheet Export")
    class BalanceSheetExportTests {

        @Test
        @DisplayName("Should generate valid PDF for Balance Sheet")
        void shouldGenerateValidPdfForBalanceSheet() {
            ChartOfAccount cash = createAccount("1.1.01", "Kas", AccountType.ASSET, NormalBalance.DEBIT);
            ChartOfAccount hutang = createAccount("2.1.01", "Hutang Usaha", AccountType.LIABILITY, NormalBalance.CREDIT);
            ChartOfAccount modal = createAccount("3.1.01", "Modal", AccountType.EQUITY, NormalBalance.CREDIT);

            ReportService.BalanceSheetItem assetItem = new ReportService.BalanceSheetItem(
                    cash, new BigDecimal("100000000"));
            ReportService.BalanceSheetItem liabilityItem = new ReportService.BalanceSheetItem(
                    hutang, new BigDecimal("20000000"));
            ReportService.BalanceSheetItem equityItem = new ReportService.BalanceSheetItem(
                    modal, new BigDecimal("50000000"));

            ReportService.BalanceSheetReport report = new ReportService.BalanceSheetReport(
                    LocalDate.of(2024, 6, 30),
                    List.of(assetItem),
                    List.of(liabilityItem),
                    List.of(equityItem),
                    new BigDecimal("100000000"),
                    new BigDecimal("20000000"),
                    new BigDecimal("80000000"),
                    new BigDecimal("30000000")
            );

            byte[] pdfBytes = exportService.exportBalanceSheetToPdf(report);

            assertThat(pdfBytes).isNotNull();
            assertThat(pdfBytes.length).isGreaterThan(0);
            assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
        }

        @Test
        @DisplayName("Should generate valid Excel for Balance Sheet")
        void shouldGenerateValidExcelForBalanceSheet() {
            ChartOfAccount cash = createAccount("1.1.01", "Kas", AccountType.ASSET, NormalBalance.DEBIT);

            ReportService.BalanceSheetItem assetItem = new ReportService.BalanceSheetItem(
                    cash, new BigDecimal("100000000"));

            ReportService.BalanceSheetReport report = new ReportService.BalanceSheetReport(
                    LocalDate.of(2024, 6, 30),
                    List.of(assetItem),
                    List.of(),
                    List.of(),
                    new BigDecimal("100000000"),
                    BigDecimal.ZERO,
                    new BigDecimal("100000000"),
                    new BigDecimal("100000000")
            );

            byte[] excelBytes = exportService.exportBalanceSheetToExcel(report);

            assertThat(excelBytes).isNotNull();
            assertThat(excelBytes.length).isGreaterThan(0);
            assertThat(new String(excelBytes, 0, 2)).isEqualTo("PK");
        }
    }

    @Nested
    @DisplayName("Income Statement Export")
    class IncomeStatementExportTests {

        @Test
        @DisplayName("Should generate valid PDF for Income Statement")
        void shouldGenerateValidPdfForIncomeStatement() {
            ChartOfAccount revenue = createAccount("4.1.01", "Pendapatan", AccountType.REVENUE, NormalBalance.CREDIT);
            ChartOfAccount expense = createAccount("5.1.01", "Beban Gaji", AccountType.EXPENSE, NormalBalance.DEBIT);

            ReportService.IncomeStatementItem revenueItem = new ReportService.IncomeStatementItem(
                    revenue, new BigDecimal("50000000"));
            ReportService.IncomeStatementItem expenseItem = new ReportService.IncomeStatementItem(
                    expense, new BigDecimal("20000000"));

            ReportService.IncomeStatementReport report = new ReportService.IncomeStatementReport(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 6, 30),
                    List.of(revenueItem),
                    List.of(expenseItem),
                    new BigDecimal("50000000"),
                    new BigDecimal("20000000"),
                    new BigDecimal("30000000")
            );

            byte[] pdfBytes = exportService.exportIncomeStatementToPdf(report);

            assertThat(pdfBytes).isNotNull();
            assertThat(pdfBytes.length).isGreaterThan(0);
            assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
        }

        @Test
        @DisplayName("Should generate valid Excel for Income Statement")
        void shouldGenerateValidExcelForIncomeStatement() {
            ChartOfAccount revenue = createAccount("4.1.01", "Pendapatan", AccountType.REVENUE, NormalBalance.CREDIT);

            ReportService.IncomeStatementItem revenueItem = new ReportService.IncomeStatementItem(
                    revenue, new BigDecimal("50000000"));

            ReportService.IncomeStatementReport report = new ReportService.IncomeStatementReport(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 6, 30),
                    List.of(revenueItem),
                    List.of(),
                    new BigDecimal("50000000"),
                    BigDecimal.ZERO,
                    new BigDecimal("50000000")
            );

            byte[] excelBytes = exportService.exportIncomeStatementToExcel(report);

            assertThat(excelBytes).isNotNull();
            assertThat(excelBytes.length).isGreaterThan(0);
            assertThat(new String(excelBytes, 0, 2)).isEqualTo("PK");
        }

        @Test
        @DisplayName("Should handle net loss in Income Statement PDF")
        void shouldHandleNetLossInIncomeStatementPdf() {
            ChartOfAccount expense = createAccount("5.1.01", "Beban Gaji", AccountType.EXPENSE, NormalBalance.DEBIT);

            ReportService.IncomeStatementItem expenseItem = new ReportService.IncomeStatementItem(
                    expense, new BigDecimal("50000000"));

            ReportService.IncomeStatementReport report = new ReportService.IncomeStatementReport(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 6, 30),
                    List.of(),
                    List.of(expenseItem),
                    BigDecimal.ZERO,
                    new BigDecimal("50000000"),
                    new BigDecimal("-50000000")
            );

            byte[] pdfBytes = exportService.exportIncomeStatementToPdf(report);

            assertThat(pdfBytes).isNotNull();
            assertThat(pdfBytes.length).isGreaterThan(0);
            assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");
        }
    }
}
