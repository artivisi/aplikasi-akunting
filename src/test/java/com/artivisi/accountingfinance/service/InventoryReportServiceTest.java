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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for InventoryReportService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("InventoryReportService Integration Tests")
class InventoryReportServiceTest {

    @Autowired
    private InventoryReportService reportService;

    @Nested
    @DisplayName("Stock Balance Report")
    class StockBalanceTests {

        @Test
        @DisplayName("Should generate stock balance report without filters")
        void shouldGenerateStockBalanceReportWithoutFilters() {
            InventoryReportService.StockBalanceReport report =
                    reportService.generateStockBalanceReport(null, null);

            assertThat(report).isNotNull();
            assertThat(report.items()).isNotNull();
        }

        @Test
        @DisplayName("Should generate stock balance report with search")
        void shouldGenerateStockBalanceReportWithSearch() {
            InventoryReportService.StockBalanceReport report =
                    reportService.generateStockBalanceReport(null, "test");

            assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should generate stock balance report with random category ID")
        void shouldGenerateStockBalanceReportWithRandomCategoryId() {
            UUID randomId = UUID.randomUUID();
            InventoryReportService.StockBalanceReport report =
                    reportService.generateStockBalanceReport(randomId, null);

            assertThat(report).isNotNull();
            assertThat(report.items()).isEmpty();
        }

        @Test
        @DisplayName("Should calculate total value correctly")
        void shouldCalculateTotalValueCorrectly() {
            InventoryReportService.StockBalanceReport report =
                    reportService.generateStockBalanceReport(null, null);

            BigDecimal calculatedTotal = report.items().stream()
                    .map(InventoryReportService.StockBalanceItem::totalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(report.totalValue()).isEqualByComparingTo(calculatedTotal);
        }
    }

    @Nested
    @DisplayName("Stock Movement Report")
    class StockMovementTests {

        @Test
        @DisplayName("Should generate stock movement report for date range")
        void shouldGenerateStockMovementReportForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            InventoryReportService.StockMovementReport report =
                    reportService.generateStockMovementReport(startDate, endDate, null, null);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should generate stock movement report with product filter")
        void shouldGenerateStockMovementReportWithProductFilter() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);
            UUID randomProductId = UUID.randomUUID();

            InventoryReportService.StockMovementReport report =
                    reportService.generateStockMovementReport(startDate, endDate, randomProductId, null);

            assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should generate stock movement report for empty period")
        void shouldGenerateStockMovementReportForEmptyPeriod() {
            LocalDate startDate = LocalDate.of(2099, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);

            InventoryReportService.StockMovementReport report =
                    reportService.generateStockMovementReport(startDate, endDate, null, null);

            assertThat(report).isNotNull();
            assertThat(report.items()).isEmpty();
        }

        @Test
        @DisplayName("Should include movement items structure")
        void shouldIncludeMovementItemsStructure() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            InventoryReportService.StockMovementReport report =
                    reportService.generateStockMovementReport(startDate, endDate, null, null);

            assertThat(report.items()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Valuation Report")
    class ValuationTests {

        @Test
        @DisplayName("Should generate valuation report without category filter")
        void shouldGenerateValuationReportWithoutCategoryFilter() {
            InventoryReportService.ValuationReport report =
                    reportService.generateValuationReport(null);

            assertThat(report).isNotNull();
            assertThat(report.items()).isNotNull();
        }

        @Test
        @DisplayName("Should generate valuation report with random category ID")
        void shouldGenerateValuationReportWithRandomCategoryId() {
            UUID randomId = UUID.randomUUID();
            InventoryReportService.ValuationReport report =
                    reportService.generateValuationReport(randomId);

            assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should calculate total value correctly")
        void shouldCalculateTotalValueCorrectly() {
            InventoryReportService.ValuationReport report =
                    reportService.generateValuationReport(null);

            BigDecimal calculatedTotal = report.items().stream()
                    .map(InventoryReportService.ValuationItem::totalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(report.totalValue()).isEqualByComparingTo(calculatedTotal);
        }
    }

    @Nested
    @DisplayName("Profitability Report")
    class ProfitabilityTests {

        @Test
        @DisplayName("Should generate profitability report for date range")
        void shouldGenerateProfitabilityReportForDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            InventoryReportService.ProfitabilityReport report =
                    reportService.generateProfitabilityReport(startDate, endDate, null, null);

            assertThat(report).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("Should generate profitability report for empty period")
        void shouldGenerateProfitabilityReportForEmptyPeriod() {
            LocalDate startDate = LocalDate.of(2099, 1, 1);
            LocalDate endDate = LocalDate.of(2099, 12, 31);

            InventoryReportService.ProfitabilityReport report =
                    reportService.generateProfitabilityReport(startDate, endDate, null, null);

            assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should calculate total margin correctly")
        void shouldCalculateTotalMarginCorrectly() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            InventoryReportService.ProfitabilityReport report =
                    reportService.generateProfitabilityReport(startDate, endDate, null, null);

            BigDecimal expectedMargin = report.totalRevenue().subtract(report.totalCogs());
            assertThat(report.totalMargin()).isEqualByComparingTo(expectedMargin);
        }

        @Test
        @DisplayName("Should include margin percentage calculation")
        void shouldIncludeMarginPercentageCalculation() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            InventoryReportService.ProfitabilityReport report =
                    reportService.generateProfitabilityReport(startDate, endDate, null, null);

            assertThat(report.getTotalMarginPercent()).isNotNull();
        }
    }
}
