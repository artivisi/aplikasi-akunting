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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DepreciationReportService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("DepreciationReportService Integration Tests")
class DepreciationReportServiceTest {

    @Autowired
    private DepreciationReportService reportService;

    @Nested
    @DisplayName("Report Generation")
    class ReportGenerationTests {

        @Test
        @DisplayName("Should generate depreciation report for current year")
        void shouldGenerateDepreciationReportForCurrentYear() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            assertThat(report).isNotNull();
            assertThat(report.year()).isEqualTo(2025);
            assertThat(report.items()).isNotNull();
        }

        @Test
        @DisplayName("Should generate depreciation report for past year")
        void shouldGenerateDepreciationReportForPastYear() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2024);

            assertThat(report).isNotNull();
            assertThat(report.year()).isEqualTo(2024);
        }

        @Test
        @DisplayName("Should include total purchase cost")
        void shouldIncludeTotalPurchaseCost() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            assertThat(report.totalPurchaseCost()).isNotNull();
            assertThat(report.totalPurchaseCost()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should include total depreciation this year")
        void shouldIncludeTotalDepreciationThisYear() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            assertThat(report.totalDepreciationThisYear()).isNotNull();
            assertThat(report.totalDepreciationThisYear()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should include accumulated depreciation")
        void shouldIncludeAccumulatedDepreciation() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            assertThat(report.totalAccumulatedDepreciation()).isNotNull();
            assertThat(report.totalAccumulatedDepreciation()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should include total book value")
        void shouldIncludeTotalBookValue() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            assertThat(report.totalBookValue()).isNotNull();
            assertThat(report.totalBookValue()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should generate empty report for future year")
        void shouldGenerateEmptyReportForFutureYear() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2099);

            assertThat(report).isNotNull();
            assertThat(report.items()).isEmpty();
            assertThat(report.totalPurchaseCost()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Report Item Details")
    class ReportItemTests {

        @Test
        @DisplayName("Should include asset details in report items")
        void shouldIncludeAssetDetailsInReportItems() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            if (!report.items().isEmpty()) {
                DepreciationReportService.DepreciationReportItem item = report.items().get(0);
                assertThat(item.assetCode()).isNotNull();
                assertThat(item.assetName()).isNotNull();
                assertThat(item.categoryName()).isNotNull();
                assertThat(item.purchaseDate()).isNotNull();
                assertThat(item.purchaseCost()).isNotNull();
                assertThat(item.usefulLifeYears()).isGreaterThan(0);
                assertThat(item.depreciationMethod()).isNotNull();
            }
        }

        @Test
        @DisplayName("Should use Indonesian method names")
        void shouldUseIndonesianMethodNames() {
            DepreciationReportService.DepreciationReport report =
                    reportService.generateReport(2025);

            if (!report.items().isEmpty()) {
                report.items().forEach(item -> {
                    assertThat(item.depreciationMethod()).isIn("Garis Lurus", "Saldo Menurun");
                });
            }
        }
    }
}
