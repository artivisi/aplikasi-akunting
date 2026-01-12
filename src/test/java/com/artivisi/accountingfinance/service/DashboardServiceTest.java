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

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DashboardService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("DashboardService Integration Tests")
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Nested
    @DisplayName("KPI Calculation")
    class KpiTests {

        @Test
        @DisplayName("Should calculate KPIs for current month")
        void shouldCalculateKpisForCurrentMonth() {
            YearMonth currentMonth = YearMonth.now();
            DashboardService.DashboardKPI kpi = dashboardService.calculateKPIs(currentMonth);

            assertThat(kpi).isNotNull();
            assertThat(kpi.revenue()).isNotNull();
            assertThat(kpi.expense()).isNotNull();
            assertThat(kpi.netProfit()).isNotNull();
        }

        @Test
        @DisplayName("Should calculate KPIs for past month")
        void shouldCalculateKpisForPastMonth() {
            YearMonth pastMonth = YearMonth.of(2025, 6);
            DashboardService.DashboardKPI kpi = dashboardService.calculateKPIs(pastMonth);

            assertThat(kpi).isNotNull();
        }

        @Test
        @DisplayName("Should calculate net profit correctly")
        void shouldCalculateNetProfitCorrectly() {
            YearMonth currentMonth = YearMonth.now();
            DashboardService.DashboardKPI kpi = dashboardService.calculateKPIs(currentMonth);

            assertThat(kpi.netProfit())
                    .isEqualByComparingTo(kpi.revenue().subtract(kpi.expense()));
        }

        @Test
        @DisplayName("Should include transaction count")
        void shouldIncludeTransactionCount() {
            YearMonth currentMonth = YearMonth.now();
            DashboardService.DashboardKPI kpi = dashboardService.calculateKPIs(currentMonth);

            assertThat(kpi.transactionCount()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Should include cash bank items")
        void shouldIncludeCashBankItems() {
            YearMonth currentMonth = YearMonth.now();
            DashboardService.DashboardKPI kpi = dashboardService.calculateKPIs(currentMonth);

            assertThat(kpi.cashBankItems()).isNotNull();
        }

        @Test
        @DisplayName("Should handle empty month data")
        void shouldHandleEmptyMonthData() {
            YearMonth futureMonth = YearMonth.of(2099, 12);
            DashboardService.DashboardKPI kpi = dashboardService.calculateKPIs(futureMonth);

            assertThat(kpi).isNotNull();
        }
    }

    @Nested
    @DisplayName("Amortization Summary")
    class AmortizationSummaryTests {

        @Test
        @DisplayName("Should get amortization summary")
        void shouldGetAmortizationSummary() {
            DashboardService.AmortizationSummary summary = dashboardService.getAmortizationSummary();

            assertThat(summary).isNotNull();
            assertThat(summary.totalPending()).isGreaterThanOrEqualTo(0);
            assertThat(summary.overdueCount()).isGreaterThanOrEqualTo(0);
            assertThat(summary.dueThisMonth()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Should have valid overdue counts")
        void shouldHaveValidOverdueCounts() {
            DashboardService.AmortizationSummary summary = dashboardService.getAmortizationSummary();

            assertThat(summary.overdueCount()).isLessThanOrEqualTo(summary.totalPending());
        }

        @Test
        @DisplayName("Should include upcoming entries")
        void shouldIncludeUpcomingEntries() {
            DashboardService.AmortizationSummary summary = dashboardService.getAmortizationSummary();

            assertThat(summary.upcomingEntries()).isNotNull();
        }
    }
}
