package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ReportService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ReportService Integration Tests")
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Nested
    @DisplayName("Trial Balance Report")
    class TrialBalanceTests {

        @Test
        @DisplayName("Should generate trial balance for current date")
        void shouldGenerateTrialBalanceForCurrentDate() {
            ReportService.TrialBalanceReport report = reportService.generateTrialBalance(LocalDate.now());

            assertThat(report).isNotNull();
            assertThat(report.asOfDate()).isEqualTo(LocalDate.now());
            assertThat(report.items()).isNotNull();
        }

        @Test
        @DisplayName("Should generate trial balance for past date")
        void shouldGenerateTrialBalanceForPastDate() {
            LocalDate pastDate = LocalDate.of(2025, 6, 30);
            ReportService.TrialBalanceReport report = reportService.generateTrialBalance(pastDate);

            assertThat(report).isNotNull();
            assertThat(report.asOfDate()).isEqualTo(pastDate);
        }

        @Test
        @DisplayName("Should have balanced trial balance")
        void shouldHaveBalancedTrialBalance() {
            ReportService.TrialBalanceReport report = reportService.generateTrialBalance(LocalDate.now());

            BigDecimal totalDebit = report.items().stream()
                    .map(ReportService.TrialBalanceItem::debitBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCredit = report.items().stream()
                    .map(ReportService.TrialBalanceItem::creditBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(totalDebit).isEqualByComparingTo(totalCredit);
        }

        @Test
        @DisplayName("Should generate trial balance for far future date")
        void shouldGenerateTrialBalanceForFarFutureDate() {
            LocalDate futureDate = LocalDate.of(2099, 12, 31);
            ReportService.TrialBalanceReport report = reportService.generateTrialBalance(futureDate);

            assertThat(report).isNotNull();
        }
    }

    @Nested
    @DisplayName("Income Statement Report")
    class IncomeStatementTests {

        @Test
        @DisplayName("Should generate income statement for date range")
        void shouldGenerateIncomeStatementForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.IncomeStatementReport report = reportService.generateIncomeStatement(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should calculate net income correctly")
        void shouldCalculateNetIncomeCorrectly() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.IncomeStatementReport report = reportService.generateIncomeStatement(startDate, endDate);

            BigDecimal expectedNetIncome = report.totalRevenue().subtract(report.totalExpense());
            assertThat(report.netIncome()).isEqualByComparingTo(expectedNetIncome);
        }

        @Test
        @DisplayName("Should generate income statement for single month")
        void shouldGenerateIncomeStatementForSingleMonth() {
            LocalDate startDate = LocalDate.of(2025, 6, 1);
            LocalDate endDate = LocalDate.of(2025, 6, 30);

            ReportService.IncomeStatementReport report = reportService.generateIncomeStatement(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.revenueItems()).isNotNull();
            assertThat(report.expenseItems()).isNotNull();
        }

        @Test
        @DisplayName("Should handle empty period")
        void shouldHandleEmptyPeriod() {
            LocalDate startDate = LocalDate.of(2099, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);

            ReportService.IncomeStatementReport report = reportService.generateIncomeStatement(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(report.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Balance Sheet Report")
    class BalanceSheetTests {

        @Test
        @DisplayName("Should generate balance sheet for current date")
        void shouldGenerateBalanceSheetForCurrentDate() {
            ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(LocalDate.now());

            assertThat(report).isNotNull();
            assertThat(report.asOfDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Should have balanced balance sheet")
        void shouldHaveBalancedBalanceSheet() {
            ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(LocalDate.now());

            BigDecimal assets = report.totalAssets();
            BigDecimal liabilitiesAndEquity = report.totalLiabilities().add(report.totalEquity());

            assertThat(assets).isEqualByComparingTo(liabilitiesAndEquity);
        }

        @Test
        @DisplayName("Should generate balance sheet for past date")
        void shouldGenerateBalanceSheetForPastDate() {
            LocalDate pastDate = LocalDate.of(2025, 6, 30);
            ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(pastDate);

            assertThat(report).isNotNull();
            assertThat(report.asOfDate()).isEqualTo(pastDate);
        }

        @Test
        @DisplayName("Should include asset items")
        void shouldIncludeAssetItems() {
            ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(LocalDate.now());

            assertThat(report.assetItems()).isNotNull();
        }

        @Test
        @DisplayName("Should include liability items")
        void shouldIncludeLiabilityItems() {
            ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(LocalDate.now());

            assertThat(report.liabilityItems()).isNotNull();
        }

        @Test
        @DisplayName("Should include equity items")
        void shouldIncludeEquityItems() {
            ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(LocalDate.now());

            assertThat(report.equityItems()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Cash Flow Report")
    class CashFlowTests {

        @Test
        @DisplayName("Should generate cash flow statement for date range")
        void shouldGenerateCashFlowStatementForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.CashFlowReport report = reportService.generateCashFlowStatement(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should include operating activities")
        void shouldIncludeOperatingActivities() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.CashFlowReport report = reportService.generateCashFlowStatement(startDate, endDate);

            assertThat(report.operatingItems()).isNotNull();
        }

        @Test
        @DisplayName("Should include investing activities")
        void shouldIncludeInvestingActivities() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.CashFlowReport report = reportService.generateCashFlowStatement(startDate, endDate);

            assertThat(report.investingItems()).isNotNull();
        }

        @Test
        @DisplayName("Should include financing activities")
        void shouldIncludeFinancingActivities() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.CashFlowReport report = reportService.generateCashFlowStatement(startDate, endDate);

            assertThat(report.financingItems()).isNotNull();
        }

        @Test
        @DisplayName("Should calculate net cash flow correctly")
        void shouldCalculateNetCashFlowCorrectly() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ReportService.CashFlowReport report = reportService.generateCashFlowStatement(startDate, endDate);

            BigDecimal expectedNet = report.operatingTotal()
                    .add(report.investingTotal())
                    .add(report.financingTotal());
            assertThat(report.netCashChange()).isEqualByComparingTo(expectedNet);
        }
    }
}
