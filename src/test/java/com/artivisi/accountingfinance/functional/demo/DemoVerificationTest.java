package com.artivisi.accountingfinance.functional.demo;

import com.artivisi.accountingfinance.entity.*;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.repository.*;
import com.artivisi.accountingfinance.service.ReportService;
import com.artivisi.accountingfinance.service.ReportService.TrialBalanceReport;
import com.artivisi.accountingfinance.service.ReportService.TrialBalanceItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.artivisi.accountingfinance.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verification test that runs AFTER DemoItServiceDataLoader.
 * Dumps and verifies all critical financial data:
 * 1. Payroll journal entries (BPJS, PPh 21)
 * 2. Depreciation journal entries
 * 3. Tax amounts (PPN, PPh 21, PPh 23)
 * 4. Per-account balances at end of each month
 * 5. Trial balance at year-end
 *
 * Run after demo loader: ./mvnw test -Dtest="DemoItServiceDataLoader,DemoVerificationTest"
 * Or standalone (will load data first): ./mvnw test -Dtest=DemoVerificationTest
 */
@Slf4j
@DisplayName("Demo Data Verification")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoVerificationTest extends DemoDataLoaderBase {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private JournalEntryRepository journalEntryRepository;
    @Autowired private PayrollRunRepository payrollRunRepository;
    @Autowired private PayrollDetailRepository payrollDetailRepository;
    @Autowired private ChartOfAccountRepository chartOfAccountRepository;
    @Autowired private ReportService reportService;

    private static final NumberFormat IDR = NumberFormat.getNumberInstance(new Locale("id", "ID"));

    @Override protected String industryName() { return "IT Service"; }
    @Override protected String seedDataPath() { return "industry-seed/it-service/seed-data"; }
    @Override protected String demoDataPath() { return "src/test/resources/demo-data/it-service"; }
    @Override protected long baseSalary() { return 15000000; }

    @Test @Order(1) @DisplayName("0. Load demo data")
    void loadData() throws Exception {
        importSeedData();
        importMasterData();
        createDemoUsers();
        executeDemoTransactions("demo-data/it-service/demo-transactions.csv");
    }

    @Test @Order(2) @DisplayName("1. Verify payroll journal entries")
    void verifyPayroll() {
        log.info("========== PAYROLL VERIFICATION ==========");

        var payrollRuns = payrollRunRepository.findAll().stream()
                .sorted(Comparator.comparing(PayrollRun::getPayrollPeriod))
                .toList();

        log.info("Payroll runs: {}", payrollRuns.size());
        assertThat(payrollRuns).hasSize(12);

        for (var run : payrollRuns) {
            var details = payrollDetailRepository.findByPayrollRunId(run.getId());
            log.info("Period {} | Status={} | Employees={} | Gross={} | Deductions={} | NetPay={} | CompanyBPJS={} | PPh21={}",
                    run.getPayrollPeriod(), run.getStatus(), details.size(),
                    fmt(run.getTotalGross()), fmt(run.getTotalDeductions()),
                    fmt(run.getTotalNetPay()), fmt(run.getTotalCompanyBpjs()),
                    fmt(run.getTotalPph21()));

            // Verify consistency: gross - deductions = net pay
            BigDecimal expectedNet = run.getTotalGross().subtract(run.getTotalDeductions());
            assertThat(run.getTotalNetPay())
                    .as("Period %s: gross - deductions should equal net pay", run.getPayrollPeriod())
                    .isEqualByComparingTo(expectedNet);

            // Log per-employee details for first month
            if (run.getPayrollPeriod().equals("2025-01")) {
                for (var d : details) {
                    log.info("  {} | Gross={} | BPJSKesEmp={} | BPJSKesCo={} | JHTEmp={} | JHTCo={} | JPEmp={} | JPCo={} | JKK={} | JKM={} | PPh21={} | Net={}",
                            d.getEmployee().getName(),
                            fmt(d.getGrossSalary()),
                            fmt(d.getBpjsKesEmployee()), fmt(d.getBpjsKesCompany()),
                            fmt(d.getBpjsJhtEmployee()), fmt(d.getBpjsJhtCompany()),
                            fmt(d.getBpjsJpEmployee()), fmt(d.getBpjsJpCompany()),
                            fmt(d.getBpjsJkk()), fmt(d.getBpjsJkm()),
                            fmt(d.getPph21()), fmt(d.getNetPay()));
                }
            }

            // Verify payroll has associated journal entries (transaction with Post Gaji Bulanan)
            assertThat(run.getTransaction())
                    .as("Period %s should have posted transaction", run.getPayrollPeriod())
                    .isNotNull();
        }
    }

    @Test @Order(3) @DisplayName("2. Verify depreciation journal entries")
    void verifyDepreciation() {
        log.info("========== DEPRECIATION VERIFICATION ==========");

        // Find all transactions related to depreciation (template = Pembelian Aset Tetap or depreciation entries)
        var allTx = transactionRepository.findAll();
        var assetTx = allTx.stream()
                .filter(t -> t.getJournalTemplate() != null &&
                        t.getJournalTemplate().getTemplateName().equals("Pembelian Aset Tetap"))
                .toList();

        log.info("Fixed asset purchase transactions: {}", assetTx.size());

        // Check depreciation account balances
        var depreciationAccounts = chartOfAccountRepository.findAll().stream()
                .filter(a -> a.getAccountName().contains("Penyusutan") || a.getAccountName().contains("Akum"))
                .toList();

        for (var acct : depreciationAccounts) {
            BigDecimal debit = journalEntryRepository.sumDebitByAccountAndDateRange(
                    acct.getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            BigDecimal credit = journalEntryRepository.sumCreditByAccountAndDateRange(
                    acct.getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            if (debit.signum() != 0 || credit.signum() != 0) {
                log.info("  {} {} | Debit={} | Credit={}",
                        acct.getAccountCode(), acct.getAccountName(), fmt(debit), fmt(credit));
            }
        }
    }

    @Test @Order(4) @DisplayName("3. Verify tax amounts")
    void verifyTax() {
        log.info("========== TAX VERIFICATION ==========");

        // PPN: Sum all PPN Keluaran (credit to 2.1.03) and PPN deposits (debit to 2.1.03)
        var ppnAccount = chartOfAccountRepository.findByAccountCode("2.1.03");
        if (ppnAccount.isPresent()) {
            BigDecimal ppnDebit = journalEntryRepository.sumDebitByAccountAndDateRange(
                    ppnAccount.get().getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            BigDecimal ppnCredit = journalEntryRepository.sumCreditByAccountAndDateRange(
                    ppnAccount.get().getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            log.info("PPN (2.1.03) | Keluaran (Credit)={} | Deposits (Debit)={} | Outstanding={}",
                    fmt(ppnCredit), fmt(ppnDebit), fmt(ppnCredit.subtract(ppnDebit)));
        }

        // PPh 21: Sum from payroll vs deposits
        var pph21Account = chartOfAccountRepository.findByAccountCode("2.1.20");
        if (pph21Account.isPresent()) {
            BigDecimal pph21Debit = journalEntryRepository.sumDebitByAccountAndDateRange(
                    pph21Account.get().getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            BigDecimal pph21Credit = journalEntryRepository.sumCreditByAccountAndDateRange(
                    pph21Account.get().getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            log.info("PPh 21 (2.1.20) | Payroll Accrued (Credit)={} | Deposits (Debit)={} | Outstanding={}",
                    fmt(pph21Credit), fmt(pph21Debit), fmt(pph21Credit.subtract(pph21Debit)));
        }

        // PPh 23 kredit pajak
        var pph23Account = chartOfAccountRepository.findByAccountCode("1.1.26");
        if (pph23Account.isPresent()) {
            BigDecimal pph23Debit = journalEntryRepository.sumDebitByAccountAndDateRange(
                    pph23Account.get().getId(), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
            log.info("Kredit Pajak PPh 23 (1.1.26) | Total={}", fmt(pph23Debit));
        }

        // Sum PPh 21 from payroll runs
        BigDecimal totalPayrollPph21 = payrollRunRepository.findAll().stream()
                .map(PayrollRun::getTotalPph21)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Total PPh 21 from payroll runs: {}", fmt(totalPayrollPph21));

        // Sum PPh 21 deposits from Setor PPh 21 transactions
        BigDecimal totalPph21Deposits = transactionRepository.findAll().stream()
                .filter(t -> t.getJournalTemplate() != null &&
                        t.getJournalTemplate().getTemplateName().equals("Setor PPh 21") &&
                        t.getStatus() == TransactionStatus.POSTED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Total PPh 21 deposits (Setor PPh 21): {}", fmt(totalPph21Deposits));
    }

    @Test @Order(5) @DisplayName("4. Verify monthly account balances")
    void verifyMonthlyBalances() {
        log.info("========== MONTHLY BALANCE VERIFICATION ==========");

        // Key accounts to track monthly
        String[] keyAccounts = {"1.1.02", "2.1.03", "2.1.10", "2.1.13", "2.1.20", "3.1.01"};
        String[] accountNames = {"Bank BCA", "Hutang PPN", "Hutang Gaji", "Hutang BPJS", "Hutang PPh 21", "Modal"};

        // Header
        StringBuilder header = new StringBuilder(String.format("%-10s", "Month"));
        for (String name : accountNames) {
            header.append(String.format(" | %15s", name));
        }
        log.info(header.toString());
        log.info("-".repeat(header.length()));

        for (int month = 1; month <= 12; month++) {
            LocalDate endOfMonth = LocalDate.of(2025, month, 1).plusMonths(1).minusDays(1);
            StringBuilder row = new StringBuilder(String.format("%-10s", "2025-" + String.format("%02d", month)));

            for (String code : keyAccounts) {
                var acct = chartOfAccountRepository.findByAccountCode(code);
                if (acct.isPresent()) {
                    BigDecimal debit = journalEntryRepository.sumDebitByAccountAndDateRange(
                            acct.get().getId(), LocalDate.of(1900, 1, 1), endOfMonth);
                    BigDecimal credit = journalEntryRepository.sumCreditByAccountAndDateRange(
                            acct.get().getId(), LocalDate.of(1900, 1, 1), endOfMonth);
                    BigDecimal balance = acct.get().getNormalBalance().name().equals("DEBIT")
                            ? debit.subtract(credit) : credit.subtract(debit);
                    row.append(String.format(" | %15s", fmtShort(balance)));
                } else {
                    row.append(String.format(" | %15s", "N/A"));
                }
            }
            log.info(row.toString());
        }
    }

    @Test @Order(6) @DisplayName("5. Verify year-end trial balance")
    void verifyYearEndTrialBalance() {
        validateTrialBalance(LocalDate.of(2025, 12, 31));
    }

    @Test @Order(7) @DisplayName("6. Verify transaction count by template")
    void verifyTransactionsByTemplate() {
        log.info("========== TRANSACTION SUMMARY BY TEMPLATE ==========");
        var allTx = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.POSTED)
                .collect(Collectors.groupingBy(
                        t -> t.getJournalTemplate() != null ? t.getJournalTemplate().getTemplateName() : "(no template)",
                        Collectors.counting()));

        allTx.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> log.info("  {} : {} transactions", e.getKey(), e.getValue()));

        log.info("Total POSTED transactions: {}", allTx.values().stream().mapToLong(Long::longValue).sum());
    }

    private String fmt(BigDecimal amount) {
        if (amount == null || amount.signum() == 0) return "0";
        return IDR.format(amount);
    }

    private String fmtShort(BigDecimal amount) {
        if (amount == null || amount.signum() == 0) return "0";
        long val = amount.longValue();
        if (Math.abs(val) >= 1_000_000) return String.format("%,.1fM", val / 1_000_000.0);
        if (Math.abs(val) >= 1_000) return String.format("%,.1fK", val / 1_000.0);
        return String.valueOf(val);
    }
}
