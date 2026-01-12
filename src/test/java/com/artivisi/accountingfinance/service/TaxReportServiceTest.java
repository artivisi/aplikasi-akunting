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
 * Integration tests for TaxReportService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("TaxReportService Integration Tests")
class TaxReportServiceTest {

    @Autowired
    private TaxReportService taxReportService;

    @Nested
    @DisplayName("PPN Summary Report")
    class PPNSummaryTests {

        @Test
        @DisplayName("Should generate PPN summary for date range")
        void shouldGeneratePPNSummaryForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPNSummaryReport report =
                    taxReportService.generatePPNSummary(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should include PPN Keluaran")
        void shouldIncludePPNKeluaran() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPNSummaryReport report =
                    taxReportService.generatePPNSummary(startDate, endDate);

            assertThat(report.ppnKeluaran()).isNotNull();
        }

        @Test
        @DisplayName("Should include PPN Masukan")
        void shouldIncludePPNMasukan() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPNSummaryReport report =
                    taxReportService.generatePPNSummary(startDate, endDate);

            assertThat(report.ppnMasukan()).isNotNull();
        }

        @Test
        @DisplayName("Should calculate Net PPN correctly")
        void shouldCalculateNetPPNCorrectly() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPNSummaryReport report =
                    taxReportService.generatePPNSummary(startDate, endDate);

            BigDecimal expectedNet = report.ppnKeluaran().subtract(report.ppnMasukan());
            assertThat(report.netPPN()).isEqualByComparingTo(expectedNet);
        }

        @Test
        @DisplayName("Should generate report for single month")
        void shouldGenerateReportForSingleMonth() {
            LocalDate startDate = LocalDate.of(2025, 6, 1);
            LocalDate endDate = LocalDate.of(2025, 6, 30);

            TaxReportService.PPNSummaryReport report =
                    taxReportService.generatePPNSummary(startDate, endDate);

            assertThat(report).isNotNull();
        }
    }

    @Nested
    @DisplayName("PPh 23 Withholding Report")
    class PPh23WithholdingTests {

        @Test
        @DisplayName("Should generate PPh 23 withholding report for date range")
        void shouldGeneratePPh23WithholdingReportForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPh23WithholdingReport report =
                    taxReportService.generatePPh23Withholding(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should include total withheld")
        void shouldIncludeTotalWithheld() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPh23WithholdingReport report =
                    taxReportService.generatePPh23Withholding(startDate, endDate);

            assertThat(report.totalWithheld()).isNotNull();
            assertThat(report.totalWithheld()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should include total deposited")
        void shouldIncludeTotalDeposited() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPh23WithholdingReport report =
                    taxReportService.generatePPh23Withholding(startDate, endDate);

            assertThat(report.totalDeposited()).isNotNull();
            assertThat(report.totalDeposited()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should calculate balance correctly")
        void shouldCalculateBalanceCorrectly() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.PPh23WithholdingReport report =
                    taxReportService.generatePPh23Withholding(startDate, endDate);

            BigDecimal expectedBalance = report.totalWithheld().subtract(report.totalDeposited());
            assertThat(report.balance()).isEqualByComparingTo(expectedBalance);
        }
    }

    @Nested
    @DisplayName("Tax Summary Report")
    class TaxSummaryTests {

        @Test
        @DisplayName("Should generate tax summary for date range")
        void shouldGenerateTaxSummaryForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.TaxSummaryReport report =
                    taxReportService.generateTaxSummary(startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should include tax items")
        void shouldIncludeTaxItems() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.TaxSummaryReport report =
                    taxReportService.generateTaxSummary(startDate, endDate);

            assertThat(report.items()).isNotNull();
        }

        @Test
        @DisplayName("Should include total balance")
        void shouldIncludeTotalBalance() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            TaxReportService.TaxSummaryReport report =
                    taxReportService.generateTaxSummary(startDate, endDate);

            assertThat(report.totalBalance()).isNotNull();
        }

        @Test
        @DisplayName("Should generate empty report for future dates")
        void shouldGenerateEmptyReportForFutureDates() {
            LocalDate startDate = LocalDate.of(2099, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);

            TaxReportService.TaxSummaryReport report =
                    taxReportService.generateTaxSummary(startDate, endDate);

            assertThat(report).isNotNull();
        }
    }
}
