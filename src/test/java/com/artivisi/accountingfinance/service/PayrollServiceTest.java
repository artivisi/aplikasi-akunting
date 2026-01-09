package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.PayrollRun;
import com.artivisi.accountingfinance.entity.PayrollStatus;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.PayrollRunRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for PayrollService.
 * Tests actual database operations and journal entry creation.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("PayrollService Integration Tests")
class PayrollServiceTest {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    // Account codes from V004__app_seed_data.sql
    private static final String ACCOUNT_BEBAN_GAJI = "5.1.01";
    private static final String ACCOUNT_BEBAN_BPJS = "5.1.11";
    private static final String ACCOUNT_HUTANG_GAJI = "2.1.07";
    private static final String ACCOUNT_HUTANG_BPJS = "2.1.08";
    private static final String ACCOUNT_HUTANG_PPH21 = "2.1.20";

    // Account UUIDs from V004__app_seed_data.sql
    private static final UUID ACCOUNT_ID_BEBAN_GAJI = UUID.fromString("50000000-0000-0000-0000-000000000101");
    private static final UUID ACCOUNT_ID_BEBAN_BPJS = UUID.fromString("50000000-0000-0000-0000-000000000111");
    private static final UUID ACCOUNT_ID_HUTANG_GAJI = UUID.fromString("20000000-0000-0000-0000-000000000107");
    private static final UUID ACCOUNT_ID_HUTANG_BPJS = UUID.fromString("20000000-0000-0000-0000-000000000108");
    private static final UUID ACCOUNT_ID_HUTANG_PPH21 = UUID.fromString("20000000-0000-0000-0000-000000000120");

    @Nested
    @DisplayName("Post Payroll Tests")
    class PostPayrollTests {

        @Test
        @DisplayName("postPayroll should create journal entries in database")
        void postPayrollShouldCreateJournalEntriesInDatabase() {
            // Arrange: Create and calculate payroll
            YearMonth period = YearMonth.of(2030, 1);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            BigDecimal baseSalary = new BigDecimal("10000000");
            int jkkRiskClass = 1;

            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), baseSalary, jkkRiskClass);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Act: Post the payroll
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert: Journal entries should exist in database
            assertThat(postedPayroll.getStatus()).isEqualTo(PayrollStatus.POSTED);
            assertThat(postedPayroll.getTransaction()).isNotNull();

            Transaction transaction = postedPayroll.getTransaction();
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.POSTED);

            // Verify journal entries exist in database
            List<JournalEntry> journalEntries = journalEntryRepository
                    .findByTransactionIdWithAccount(transaction.getId());

            assertThat(journalEntries).isNotEmpty().hasSize(5); // 2 debit + 3 credit entries
        }

        @Test
        @DisplayName("postPayroll should create journal entries with correct accounts")
        void postPayrollShouldCreateJournalEntriesWithCorrectAccounts() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 2);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            BigDecimal baseSalary = new BigDecimal("10000000");
            int jkkRiskClass = 1;

            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), baseSalary, jkkRiskClass);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert: Verify correct accounts are used
            List<JournalEntry> journalEntries = journalEntryRepository
                    .findByTransactionIdWithAccount(postedPayroll.getTransaction().getId());

            Map<UUID, JournalEntry> entriesByAccount = journalEntries.stream()
                    .collect(Collectors.toMap(
                            je -> je.getAccount().getId(),
                            je -> je
                    ));

            // Verify all required accounts are present
            assertThat(entriesByAccount).containsKeys(
                    ACCOUNT_ID_BEBAN_GAJI,
                    ACCOUNT_ID_BEBAN_BPJS,
                    ACCOUNT_ID_HUTANG_GAJI,
                    ACCOUNT_ID_HUTANG_BPJS,
                    ACCOUNT_ID_HUTANG_PPH21);

            // Verify account codes
            assertThat(entriesByAccount.get(ACCOUNT_ID_BEBAN_GAJI).getAccount().getAccountCode())
                    .isEqualTo(ACCOUNT_BEBAN_GAJI);
            assertThat(entriesByAccount.get(ACCOUNT_ID_BEBAN_BPJS).getAccount().getAccountCode())
                    .isEqualTo(ACCOUNT_BEBAN_BPJS);
            assertThat(entriesByAccount.get(ACCOUNT_ID_HUTANG_GAJI).getAccount().getAccountCode())
                    .isEqualTo(ACCOUNT_HUTANG_GAJI);
            assertThat(entriesByAccount.get(ACCOUNT_ID_HUTANG_BPJS).getAccount().getAccountCode())
                    .isEqualTo(ACCOUNT_HUTANG_BPJS);
            assertThat(entriesByAccount.get(ACCOUNT_ID_HUTANG_PPH21).getAccount().getAccountCode())
                    .isEqualTo(ACCOUNT_HUTANG_PPH21);
        }

        @Test
        @DisplayName("postPayroll should create journal entries with correct amounts")
        void postPayrollShouldCreateJournalEntriesWithCorrectAmounts() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 3);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            BigDecimal baseSalary = new BigDecimal("10000000");
            int jkkRiskClass = 1;

            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), baseSalary, jkkRiskClass);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Get totals from payroll run before posting
            BigDecimal expectedGrossSalary = payrollRun.getTotalGross();
            BigDecimal expectedCompanyBpjs = payrollRun.getTotalCompanyBpjs();
            BigDecimal expectedNetPay = payrollRun.getTotalNetPay();
            BigDecimal expectedPph21 = payrollRun.getTotalPph21();

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert: Verify amounts match
            List<JournalEntry> journalEntries = journalEntryRepository
                    .findByTransactionIdWithAccount(postedPayroll.getTransaction().getId());

            Map<UUID, JournalEntry> entriesByAccount = journalEntries.stream()
                    .collect(Collectors.toMap(
                            je -> je.getAccount().getId(),
                            je -> je
                    ));

            // Debit entries (expenses)
            JournalEntry bebanGaji = entriesByAccount.get(ACCOUNT_ID_BEBAN_GAJI);
            assertThat(bebanGaji.getDebitAmount()).isEqualByComparingTo(expectedGrossSalary);
            assertThat(bebanGaji.getCreditAmount()).isEqualByComparingTo(BigDecimal.ZERO);

            JournalEntry bebanBpjs = entriesByAccount.get(ACCOUNT_ID_BEBAN_BPJS);
            assertThat(bebanBpjs.getDebitAmount()).isEqualByComparingTo(expectedCompanyBpjs);
            assertThat(bebanBpjs.getCreditAmount()).isEqualByComparingTo(BigDecimal.ZERO);

            // Credit entries (liabilities)
            JournalEntry hutangGaji = entriesByAccount.get(ACCOUNT_ID_HUTANG_GAJI);
            assertThat(hutangGaji.getCreditAmount()).isEqualByComparingTo(expectedNetPay);
            assertThat(hutangGaji.getDebitAmount()).isEqualByComparingTo(BigDecimal.ZERO);

            JournalEntry hutangPph21 = entriesByAccount.get(ACCOUNT_ID_HUTANG_PPH21);
            assertThat(hutangPph21.getCreditAmount()).isEqualByComparingTo(expectedPph21);
            assertThat(hutangPph21.getDebitAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("postPayroll should create balanced journal entries (total debit equals total credit)")
        void postPayrollShouldCreateBalancedJournalEntries() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 4);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            BigDecimal baseSalary = new BigDecimal("15000000");
            int jkkRiskClass = 3;

            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), baseSalary, jkkRiskClass);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert: Total debit must equal total credit
            List<JournalEntry> journalEntries = journalEntryRepository
                    .findByTransactionIdWithAccount(postedPayroll.getTransaction().getId());

            BigDecimal totalDebit = journalEntries.stream()
                    .map(JournalEntry::getDebitAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCredit = journalEntries.stream()
                    .map(JournalEntry::getCreditAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(totalDebit).isEqualByComparingTo(totalCredit);
        }

        @Test
        @DisplayName("postPayroll should calculate total BPJS correctly (company + employee)")
        void postPayrollShouldCalculateTotalBpjsCorrectly() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 5);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            BigDecimal baseSalary = new BigDecimal("10000000");
            int jkkRiskClass = 1;

            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), baseSalary, jkkRiskClass);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Calculate expected total BPJS (company + employee contributions)
            BigDecimal expectedCompanyBpjs = payrollRun.getTotalCompanyBpjs();
            BigDecimal expectedEmployeeBpjs = payrollRun.getDetails().stream()
                    .map(d -> d.getBpjsKesEmployee()
                            .add(d.getBpjsJhtEmployee())
                            .add(d.getBpjsJpEmployee()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expectedTotalBpjs = expectedCompanyBpjs.add(expectedEmployeeBpjs);

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert: Hutang BPJS should equal total BPJS (company + employee)
            List<JournalEntry> journalEntries = journalEntryRepository
                    .findByTransactionIdWithAccount(postedPayroll.getTransaction().getId());

            JournalEntry hutangBpjs = journalEntries.stream()
                    .filter(je -> je.getAccount().getId().equals(ACCOUNT_ID_HUTANG_BPJS))
                    .findFirst()
                    .orElseThrow();

            assertThat(hutangBpjs.getCreditAmount()).isEqualByComparingTo(expectedTotalBpjs);
        }

        @Test
        @DisplayName("postPayroll should update payroll status to POSTED")
        void postPayrollShouldUpdateStatusToPosted() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 6);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), new BigDecimal("10000000"), 1);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert
            assertThat(postedPayroll.getStatus()).isEqualTo(PayrollStatus.POSTED);
            assertThat(postedPayroll.getPostedAt()).isNotNull();

            // Verify in database
            PayrollRun fromDb = payrollRunRepository.findById(payrollRun.getId()).orElseThrow();
            assertThat(fromDb.getStatus()).isEqualTo(PayrollStatus.POSTED);
        }

        @Test
        @DisplayName("postPayroll should fail for non-approved payroll")
        void postPayrollShouldFailForNonApprovedPayroll() {
            // Arrange: Create calculated but not approved payroll
            YearMonth period = YearMonth.of(2030, 7);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), new BigDecimal("10000000"), 1);

            UUID payrollId = payrollRun.getId();

            // Act & Assert
            assertThatThrownBy(() -> payrollService.postPayroll(payrollId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("APPROVED");
        }

        @Test
        @DisplayName("postPayroll should set transaction reference number")
        void postPayrollShouldSetTransactionReferenceNumber() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 8);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), new BigDecimal("10000000"), 1);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert
            Transaction transaction = postedPayroll.getTransaction();
            assertThat(transaction.getReferenceNumber()).isEqualTo("PAYROLL-2030-08");
            assertThat(transaction.getDescription()).contains("Payroll");
        }

        @Test
        @DisplayName("postPayroll should create transaction with correct amount (gross salary)")
        void postPayrollShouldCreateTransactionWithCorrectAmount() {
            // Arrange
            YearMonth period = YearMonth.of(2030, 9);
            PayrollRun payrollRun = payrollService.createPayrollRun(period);
            BigDecimal baseSalary = new BigDecimal("12000000");
            payrollRun = payrollService.calculatePayroll(payrollRun.getId(), baseSalary, 1);
            payrollRun = payrollService.approvePayroll(payrollRun.getId());

            BigDecimal expectedTotalGross = payrollRun.getTotalGross();

            // Act
            PayrollRun postedPayroll = payrollService.postPayroll(payrollRun.getId());

            // Assert
            Transaction transaction = postedPayroll.getTransaction();
            assertThat(transaction.getAmount()).isEqualByComparingTo(expectedTotalGross);
        }
    }
}
