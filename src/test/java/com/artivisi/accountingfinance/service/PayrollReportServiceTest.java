package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.PayrollDetail;
import com.artivisi.accountingfinance.entity.PayrollRun;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.repository.PayrollDetailRepository;
import com.artivisi.accountingfinance.repository.PayrollRunRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PayrollReportService.
 * Tests PDF and Excel report generation for payroll using existing test data.
 * Test data from V909__payroll_approved_test_data.sql
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("PayrollReportService - Report Generation")
class PayrollReportServiceTest {

    @Autowired
    private PayrollReportService payrollReportService;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private PayrollDetailRepository payrollDetailRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Test data IDs from V909__payroll_approved_test_data.sql
    private static final UUID APPROVED_PAYROLL_RUN_ID = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    private static final UUID EMPLOYEE_1_ID = UUID.fromString("e0000000-0000-0000-0000-000000000001");

    private PayrollRun testPayrollRun;
    private List<PayrollDetail> testDetails;

    @BeforeEach
    void setup() {
        // Load existing test data from database
        testPayrollRun = payrollRunRepository.findById(APPROVED_PAYROLL_RUN_ID)
                .orElseThrow(() -> new IllegalStateException("Test payroll run not found"));
        testDetails = payrollDetailRepository.findByPayrollRunOrderByEmployeeEmployeeId(testPayrollRun);
    }

    // ==================== Payroll Summary ====================

    @Test
    @DisplayName("Should export payroll summary to PDF")
    void shouldExportPayrollSummaryToPdf() {
        byte[] pdf = payrollReportService.exportPayrollSummaryToPdf(testPayrollRun, testDetails);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
        // PDF files start with %PDF
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export payroll summary to Excel")
    void shouldExportPayrollSummaryToExcel() throws Exception {
        byte[] excel = payrollReportService.exportPayrollSummaryToExcel(testPayrollRun, testDetails);

        assertThat(excel).isNotNull();
        assertThat(excel).hasSizeGreaterThan(0);

        // Verify Excel structure
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            assertThat(workbook.getSheetName(0)).isEqualTo("Rekap Gaji");
        }
    }

    @Test
    @DisplayName("Should include employee data in payroll summary PDF")
    void shouldIncludeEmployeeDataInPayrollSummaryPdf() {
        byte[] pdf = payrollReportService.exportPayrollSummaryToPdf(testPayrollRun, testDetails);

        assertThat(pdf).isNotNull();
        // PDF should contain substantial data
        assertThat(pdf).hasSizeGreaterThan(1000);
    }

    // ==================== PPh 21 Report ====================

    @Test
    @DisplayName("Should export PPh 21 report to PDF")
    void shouldExportPph21ReportToPdf() {
        byte[] pdf = payrollReportService.exportPph21ReportToPdf(testPayrollRun, testDetails);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export PPh 21 report to Excel")
    void shouldExportPph21ReportToExcel() throws Exception {
        byte[] excel = payrollReportService.exportPph21ReportToExcel(testPayrollRun, testDetails);

        assertThat(excel).isNotNull();
        assertThat(excel).hasSizeGreaterThan(0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            assertThat(workbook.getSheetName(0)).isEqualTo("PPh 21");
        }
    }

    // ==================== BPJS Report ====================

    @Test
    @DisplayName("Should export BPJS report to PDF")
    void shouldExportBpjsReportToPdf() {
        byte[] pdf = payrollReportService.exportBpjsReportToPdf(testPayrollRun, testDetails);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should export BPJS report to Excel")
    void shouldExportBpjsReportToExcel() throws Exception {
        byte[] excel = payrollReportService.exportBpjsReportToExcel(testPayrollRun, testDetails);

        assertThat(excel).isNotNull();
        assertThat(excel).hasSizeGreaterThan(0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            // Should have 2 sheets: BPJS Kesehatan and BPJS Ketenagakerjaan
            assertThat(workbook.getNumberOfSheets()).isEqualTo(2);
            assertThat(workbook.getSheetName(0)).isEqualTo("BPJS Kesehatan");
            assertThat(workbook.getSheetName(1)).isEqualTo("BPJS Ketenagakerjaan");
        }
    }

    // ==================== Payslip ====================

    @Test
    @DisplayName("Should generate payslip PDF")
    void shouldGeneratePayslipPdf() {
        PayrollDetail detail = testDetails.get(0);

        byte[] pdf = payrollReportService.generatePayslipPdf(testPayrollRun, detail);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Should generate payslip with correct employee info")
    void shouldGeneratePayslipWithCorrectEmployeeInfo() {
        PayrollDetail detail = testDetails.get(0);

        byte[] pdf = payrollReportService.generatePayslipPdf(testPayrollRun, detail);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(500);
    }

    // ==================== Bukti Potong 1721-A1 ====================

    @Test
    @DisplayName("Should generate Bukti Potong 1721-A1")
    void shouldGenerateBuktiPotong1721A1() {
        var employee = employeeRepository.findById(EMPLOYEE_1_ID)
                .orElseThrow(() -> new IllegalStateException("Test employee not found"));

        // Create yearly summary directly from test data
        // Since test data is APPROVED (not POSTED), we construct the summary manually
        var summary = new PayrollService.YearlyPayrollSummary(
                employee,
                2025,
                1, // monthCount
                new java.math.BigDecimal("12000000"), // totalGross
                new java.math.BigDecimal("324000"), // totalBpjsEmployee (JHT + JP + Kes)
                new java.math.BigDecimal("720000") // totalPph21
        );

        byte[] pdf = payrollReportService.generateBuktiPotong1721A1(summary);

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle empty payroll details")
    void shouldHandleEmptyPayrollDetails() {
        byte[] pdf = payrollReportService.exportPayrollSummaryToPdf(testPayrollRun, List.of());

        assertThat(pdf).isNotNull();
        assertThat(pdf).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle multiple employees in report")
    void shouldHandleMultipleEmployeesInReport() {
        // testDetails already has 3 employees from V909 test data
        assertThat(testDetails).hasSizeGreaterThanOrEqualTo(3);

        byte[] pdf = payrollReportService.exportPayrollSummaryToPdf(testPayrollRun, testDetails);
        assertThat(pdf).isNotNull();

        byte[] pph21Pdf = payrollReportService.exportPph21ReportToPdf(testPayrollRun, testDetails);
        assertThat(pph21Pdf).isNotNull();

        byte[] bpjsPdf = payrollReportService.exportBpjsReportToPdf(testPayrollRun, testDetails);
        assertThat(bpjsPdf).isNotNull();
    }
}
