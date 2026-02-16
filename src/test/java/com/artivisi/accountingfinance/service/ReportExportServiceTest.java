package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ReportExportService.
 * Tests PDF and Excel export for financial reports using actual database data.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ReportExportService - Financial Report Export")
class ReportExportServiceTest {

    @Autowired
    private ReportExportService reportExportService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DepreciationReportService depreciationReportService;

    @Autowired
    private InventoryReportService inventoryReportService;

    // ==================== Trial Balance ====================

    @Test
    @DisplayName("Should export trial balance to PDF")
    void shouldExportTrialBalanceToPdf() {
        var report = reportService.generateTrialBalance(LocalDate.now());
        byte[] pdf = reportExportService.exportTrialBalanceToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export trial balance to Excel")
    void shouldExportTrialBalanceToExcel() throws Exception {
        var report = reportService.generateTrialBalance(LocalDate.now());
        byte[] excel = reportExportService.exportTrialBalanceToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Balance Sheet ====================

    @Test
    @DisplayName("Should export balance sheet to PDF")
    void shouldExportBalanceSheetToPdf() {
        var report = reportService.generateBalanceSheet(LocalDate.now());
        byte[] pdf = reportExportService.exportBalanceSheetToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export balance sheet to Excel")
    void shouldExportBalanceSheetToExcel() throws Exception {
        var report = reportService.generateBalanceSheet(LocalDate.now());
        byte[] excel = reportExportService.exportBalanceSheetToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Income Statement ====================

    @Test
    @DisplayName("Should export income statement to PDF")
    void shouldExportIncomeStatementToPdf() {
        var report = reportService.generateIncomeStatement(
                LocalDate.now().minusMonths(1), LocalDate.now());
        byte[] pdf = reportExportService.exportIncomeStatementToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export income statement to Excel")
    void shouldExportIncomeStatementToExcel() throws Exception {
        var report = reportService.generateIncomeStatement(
                LocalDate.now().minusMonths(1), LocalDate.now());
        byte[] excel = reportExportService.exportIncomeStatementToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Cash Flow ====================

    @Test
    @DisplayName("Should export cash flow to PDF")
    void shouldExportCashFlowToPdf() {
        var report = reportService.generateCashFlowStatement(
                LocalDate.now().minusMonths(1), LocalDate.now());
        byte[] pdf = reportExportService.exportCashFlowToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export cash flow to Excel")
    void shouldExportCashFlowToExcel() throws Exception {
        var report = reportService.generateCashFlowStatement(
                LocalDate.now().minusMonths(1), LocalDate.now());
        byte[] excel = reportExportService.exportCashFlowToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Depreciation ====================

    @Test
    @DisplayName("Should export depreciation to PDF")
    void shouldExportDepreciationToPdf() {
        var report = depreciationReportService.generateReport(LocalDate.now().getYear());
        byte[] pdf = reportExportService.exportDepreciationToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export depreciation to Excel")
    void shouldExportDepreciationToExcel() throws Exception {
        var report = depreciationReportService.generateReport(LocalDate.now().getYear());
        byte[] excel = reportExportService.exportDepreciationToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Stock Balance ====================

    @Test
    @DisplayName("Should export stock balance to PDF")
    void shouldExportStockBalanceToPdf() {
        var report = inventoryReportService.generateStockBalanceReport(null, null);
        byte[] pdf = reportExportService.exportStockBalanceToPdf(report, LocalDate.now());

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export stock balance to Excel")
    void shouldExportStockBalanceToExcel() throws Exception {
        var report = inventoryReportService.generateStockBalanceReport(null, null);
        byte[] excel = reportExportService.exportStockBalanceToExcel(report, LocalDate.now());

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Stock Movement ====================

    @Test
    @DisplayName("Should export stock movement to PDF")
    void shouldExportStockMovementToPdf() {
        var report = inventoryReportService.generateStockMovementReport(
                LocalDate.now().minusMonths(1), LocalDate.now(), null, null);
        byte[] pdf = reportExportService.exportStockMovementToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export stock movement to Excel")
    void shouldExportStockMovementToExcel() throws Exception {
        var report = inventoryReportService.generateStockMovementReport(
                LocalDate.now().minusMonths(1), LocalDate.now(), null, null);
        byte[] excel = reportExportService.exportStockMovementToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Valuation ====================

    @Test
    @DisplayName("Should export valuation to PDF")
    void shouldExportValuationToPdf() {
        var report = inventoryReportService.generateValuationReport(null);
        byte[] pdf = reportExportService.exportValuationToPdf(report, LocalDate.now());

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export valuation to Excel")
    void shouldExportValuationToExcel() throws Exception {
        var report = inventoryReportService.generateValuationReport(null);
        byte[] excel = reportExportService.exportValuationToExcel(report, LocalDate.now());

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }

    // ==================== Product Profitability ====================

    @Test
    @DisplayName("Should export product profitability to PDF")
    void shouldExportProductProfitabilityToPdf() {
        var report = inventoryReportService.generateProfitabilityReport(
                LocalDate.now().minusMonths(1), LocalDate.now(), null, null);
        byte[] pdf = reportExportService.exportProductProfitabilityToPdf(report);

        assertThat(pdf).isNotNull();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export product profitability to Excel")
    void shouldExportProductProfitabilityToExcel() throws Exception {
        var report = inventoryReportService.generateProfitabilityReport(
                LocalDate.now().minusMonths(1), LocalDate.now(), null, null);
        byte[] excel = reportExportService.exportProductProfitabilityToExcel(report);

        assertThat(excel).isNotNull();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
        }
    }
}
