package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.enums.AccountType;
import com.artivisi.accountingfinance.enums.NormalBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * This test generates actual export files to /tmp/reports for manual verification.
 */
@DisplayName("Generate Export Files for Manual Verification")
class GenerateExportFilesTest {

    private ReportExportService exportService;
    private Path outputDir;

    @BeforeEach
    void setUp() throws IOException {
        exportService = new ReportExportService();
        outputDir = Paths.get("/tmp/reports");
        Files.createDirectories(outputDir);
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

    @Test
    @DisplayName("Generate Trial Balance PDF and Excel")
    void generateTrialBalanceFiles() throws IOException {
        ChartOfAccount cash = createAccount("1.1.01", "Kas", AccountType.ASSET, NormalBalance.DEBIT);
        ChartOfAccount bank = createAccount("1.1.02", "Bank BCA", AccountType.ASSET, NormalBalance.DEBIT);
        ChartOfAccount equipment = createAccount("1.2.01", "Peralatan Komputer", AccountType.ASSET, NormalBalance.DEBIT);
        ChartOfAccount accDep = createAccount("1.2.02", "Akum. Peny. Peralatan", AccountType.ASSET, NormalBalance.CREDIT);
        ChartOfAccount hutang = createAccount("2.1.01", "Hutang Usaha", AccountType.LIABILITY, NormalBalance.CREDIT);
        ChartOfAccount modal = createAccount("3.1.01", "Modal Disetor", AccountType.EQUITY, NormalBalance.CREDIT);
        ChartOfAccount revenue1 = createAccount("4.1.01", "Pendapatan Jasa Konsultasi", AccountType.REVENUE, NormalBalance.CREDIT);
        ChartOfAccount revenue2 = createAccount("4.1.02", "Pendapatan Jasa Development", AccountType.REVENUE, NormalBalance.CREDIT);
        ChartOfAccount expense1 = createAccount("5.1.01", "Beban Gaji", AccountType.EXPENSE, NormalBalance.DEBIT);
        ChartOfAccount expense2 = createAccount("5.1.02", "Beban Server & Cloud", AccountType.EXPENSE, NormalBalance.DEBIT);
        ChartOfAccount expense3 = createAccount("5.1.07", "Beban Penyusutan", AccountType.EXPENSE, NormalBalance.DEBIT);

        List<ReportService.TrialBalanceItem> items = List.of(
                new ReportService.TrialBalanceItem(cash, new BigDecimal("134000000"), BigDecimal.ZERO),
                new ReportService.TrialBalanceItem(bank, new BigDecimal("40000000"), BigDecimal.ZERO),
                new ReportService.TrialBalanceItem(equipment, new BigDecimal("30000000"), BigDecimal.ZERO),
                new ReportService.TrialBalanceItem(accDep, BigDecimal.ZERO, new BigDecimal("1000000")),
                new ReportService.TrialBalanceItem(hutang, BigDecimal.ZERO, new BigDecimal("10000000")),
                new ReportService.TrialBalanceItem(modal, BigDecimal.ZERO, new BigDecimal("150000000")),
                new ReportService.TrialBalanceItem(revenue1, BigDecimal.ZERO, new BigDecimal("37000000")),
                new ReportService.TrialBalanceItem(revenue2, BigDecimal.ZERO, new BigDecimal("25000000")),
                new ReportService.TrialBalanceItem(expense1, new BigDecimal("16000000"), BigDecimal.ZERO),
                new ReportService.TrialBalanceItem(expense2, new BigDecimal("2000000"), BigDecimal.ZERO),
                new ReportService.TrialBalanceItem(expense3, new BigDecimal("1000000"), BigDecimal.ZERO)
        );

        ReportService.TrialBalanceReport report = new ReportService.TrialBalanceReport(
                LocalDate.of(2024, 6, 30),
                items,
                new BigDecimal("223000000"),
                new BigDecimal("223000000")
        );

        byte[] pdfBytes = exportService.exportTrialBalanceToPdf(report);
        byte[] excelBytes = exportService.exportTrialBalanceToExcel(report);

        Files.write(outputDir.resolve("neraca-saldo.pdf"), pdfBytes);
        Files.write(outputDir.resolve("neraca-saldo.xlsx"), excelBytes);

        System.out.println("Generated: " + outputDir.resolve("neraca-saldo.pdf"));
        System.out.println("Generated: " + outputDir.resolve("neraca-saldo.xlsx"));
    }

    @Test
    @DisplayName("Generate Balance Sheet PDF and Excel")
    void generateBalanceSheetFiles() throws IOException {
        ChartOfAccount cash = createAccount("1.1.01", "Kas", AccountType.ASSET, NormalBalance.DEBIT);
        ChartOfAccount bank = createAccount("1.1.02", "Bank BCA", AccountType.ASSET, NormalBalance.DEBIT);
        ChartOfAccount equipment = createAccount("1.2.01", "Peralatan Komputer", AccountType.ASSET, NormalBalance.DEBIT);
        ChartOfAccount accDep = createAccount("1.2.02", "Akum. Peny. Peralatan", AccountType.ASSET, NormalBalance.CREDIT);
        ChartOfAccount hutang = createAccount("2.1.01", "Hutang Usaha", AccountType.LIABILITY, NormalBalance.CREDIT);
        ChartOfAccount modal = createAccount("3.1.01", "Modal Disetor", AccountType.EQUITY, NormalBalance.CREDIT);

        List<ReportService.BalanceSheetItem> assetItems = List.of(
                new ReportService.BalanceSheetItem(cash, new BigDecimal("134000000")),
                new ReportService.BalanceSheetItem(bank, new BigDecimal("40000000")),
                new ReportService.BalanceSheetItem(equipment, new BigDecimal("30000000")),
                new ReportService.BalanceSheetItem(accDep, new BigDecimal("1000000"))
        );

        List<ReportService.BalanceSheetItem> liabilityItems = List.of(
                new ReportService.BalanceSheetItem(hutang, new BigDecimal("10000000"))
        );

        List<ReportService.BalanceSheetItem> equityItems = List.of(
                new ReportService.BalanceSheetItem(modal, new BigDecimal("150000000"))
        );

        ReportService.BalanceSheetReport report = new ReportService.BalanceSheetReport(
                LocalDate.of(2024, 6, 30),
                assetItems,
                liabilityItems,
                equityItems,
                new BigDecimal("203000000"),
                new BigDecimal("10000000"),
                new BigDecimal("193000000"),
                new BigDecimal("33000000")
        );

        byte[] pdfBytes = exportService.exportBalanceSheetToPdf(report);
        byte[] excelBytes = exportService.exportBalanceSheetToExcel(report);

        Files.write(outputDir.resolve("laporan-posisi-keuangan.pdf"), pdfBytes);
        Files.write(outputDir.resolve("laporan-posisi-keuangan.xlsx"), excelBytes);

        System.out.println("Generated: " + outputDir.resolve("laporan-posisi-keuangan.pdf"));
        System.out.println("Generated: " + outputDir.resolve("laporan-posisi-keuangan.xlsx"));
    }

    @Test
    @DisplayName("Generate Income Statement PDF and Excel")
    void generateIncomeStatementFiles() throws IOException {
        ChartOfAccount revenue1 = createAccount("4.1.01", "Pendapatan Jasa Konsultasi", AccountType.REVENUE, NormalBalance.CREDIT);
        ChartOfAccount revenue2 = createAccount("4.1.02", "Pendapatan Jasa Development", AccountType.REVENUE, NormalBalance.CREDIT);
        ChartOfAccount expense1 = createAccount("5.1.01", "Beban Gaji", AccountType.EXPENSE, NormalBalance.DEBIT);
        ChartOfAccount expense2 = createAccount("5.1.02", "Beban Server & Cloud", AccountType.EXPENSE, NormalBalance.DEBIT);
        ChartOfAccount expense3 = createAccount("5.1.07", "Beban Penyusutan", AccountType.EXPENSE, NormalBalance.DEBIT);

        List<ReportService.IncomeStatementItem> revenueItems = List.of(
                new ReportService.IncomeStatementItem(revenue1, new BigDecimal("27000000")),
                new ReportService.IncomeStatementItem(revenue2, new BigDecimal("25000000"))
        );

        List<ReportService.IncomeStatementItem> expenseItems = List.of(
                new ReportService.IncomeStatementItem(expense1, new BigDecimal("16000000")),
                new ReportService.IncomeStatementItem(expense2, new BigDecimal("2000000")),
                new ReportService.IncomeStatementItem(expense3, new BigDecimal("1000000"))
        );

        ReportService.IncomeStatementReport report = new ReportService.IncomeStatementReport(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30),
                revenueItems,
                expenseItems,
                new BigDecimal("52000000"),
                new BigDecimal("19000000"),
                new BigDecimal("33000000")
        );

        byte[] pdfBytes = exportService.exportIncomeStatementToPdf(report);
        byte[] excelBytes = exportService.exportIncomeStatementToExcel(report);

        Files.write(outputDir.resolve("laporan-laba-rugi.pdf"), pdfBytes);
        Files.write(outputDir.resolve("laporan-laba-rugi.xlsx"), excelBytes);

        System.out.println("Generated: " + outputDir.resolve("laporan-laba-rugi.pdf"));
        System.out.println("Generated: " + outputDir.resolve("laporan-laba-rugi.xlsx"));
    }
}
