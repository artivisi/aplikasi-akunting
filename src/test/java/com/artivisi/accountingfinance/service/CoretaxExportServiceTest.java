package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.repository.TaxTransactionDetailRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for CoretaxExportService.
 * Tests export of tax data to Excel format for Coretax system.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Coretax Export Service Integration Tests")
class CoretaxExportServiceTest {

    @Autowired
    private CoretaxExportService coretaxExportService;

    @Autowired
    private TaxTransactionDetailRepository taxTransactionDetailRepository;

    // Test data uses CURRENT_DATE - INTERVAL 'N day', so use a wide date range
    private static final LocalDate START_DATE = LocalDate.now().minusMonths(1);
    private static final LocalDate END_DATE = LocalDate.now().plusMonths(1);

    @Test
    @DisplayName("Should export e-Faktur Keluaran to Excel")
    void shouldExportEFakturKeluaran() throws IOException {
        byte[] excelData = coretaxExportService.exportEFakturKeluaran(START_DATE, END_DATE);

        assertThat(excelData).isNotNull();
        assertThat(excelData).hasSizeGreaterThan(0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            // Verify DATA sheet exists
            Sheet dataSheet = workbook.getSheet("DATA");
            assertThat(dataSheet).isNotNull();

            // Verify header row
            Row headerRow = dataSheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("TrxCode");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("TrxNumber");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("TrxDate");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("SellerTaxId");
            assertThat(headerRow.getCell(11).getStringCellValue()).isEqualTo("TaxBaseSellingPrice");
            assertThat(headerRow.getCell(13).getStringCellValue()).isEqualTo("VAT");

            // Verify REF sheet exists
            Sheet refSheet = workbook.getSheet("REF");
            assertThat(refSheet).isNotNull();
        }
    }

    @Test
    @DisplayName("Should export e-Faktur Keluaran with correct data rows")
    void shouldExportEFakturKeluaranWithDataRows() throws IOException {
        // First check if we have test data
        var fakturKeluaran = taxTransactionDetailRepository.findEFakturKeluaranByDateRange(START_DATE, END_DATE);
        if (fakturKeluaran.isEmpty()) {
            return; // Skip if no test data
        }

        byte[] excelData = coretaxExportService.exportEFakturKeluaran(START_DATE, END_DATE);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            Sheet dataSheet = workbook.getSheet("DATA");

            // Row 0 is header, row 1+ is data
            int dataRowCount = dataSheet.getLastRowNum(); // Last row index (0-based)
            assertThat(dataRowCount).isGreaterThanOrEqualTo(1);

            // Verify first data row has transaction code
            Row firstDataRow = dataSheet.getRow(1);
            String trxCode = firstDataRow.getCell(0).getStringCellValue();
            assertThat(trxCode).isIn("01", "02", "03", "04", "07", "08");
        }
    }

    @Test
    @DisplayName("Should export e-Faktur Masukan to Excel")
    void shouldExportEFakturMasukan() throws IOException {
        byte[] excelData = coretaxExportService.exportEFakturMasukan(START_DATE, END_DATE);

        assertThat(excelData).isNotNull();
        assertThat(excelData).hasSizeGreaterThan(0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            // Verify DATA sheet exists
            Sheet dataSheet = workbook.getSheet("DATA");
            assertThat(dataSheet).isNotNull();

            // Verify header row
            Row headerRow = dataSheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("TrxCode");

            // Verify REF sheet exists
            Sheet refSheet = workbook.getSheet("REF");
            assertThat(refSheet).isNotNull();
        }
    }

    @Test
    @DisplayName("Should export e-Bupot Unifikasi to Excel")
    void shouldExportBupotUnifikasi() throws IOException {
        byte[] excelData = coretaxExportService.exportBupotUnifikasi(START_DATE, END_DATE);

        assertThat(excelData).isNotNull();
        assertThat(excelData).hasSizeGreaterThan(0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            // Verify DATA sheet exists
            Sheet dataSheet = workbook.getSheet("DATA");
            assertThat(dataSheet).isNotNull();

            // Verify header row for Bupot
            Row headerRow = dataSheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("BupotNumber");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("BupotDate");
            assertThat(headerRow.getCell(8).getStringCellValue()).isEqualTo("TaxObjectCode");
            assertThat(headerRow.getCell(9).getStringCellValue()).isEqualTo("GrossAmount");
            assertThat(headerRow.getCell(10).getStringCellValue()).isEqualTo("TaxRate");
            assertThat(headerRow.getCell(11).getStringCellValue()).isEqualTo("TaxAmount");

            // Verify REF sheet exists
            Sheet refSheet = workbook.getSheet("REF");
            assertThat(refSheet).isNotNull();
        }
    }

    @Test
    @DisplayName("Should export e-Bupot Unifikasi with correct data rows")
    void shouldExportBupotUnifikasiWithDataRows() throws IOException {
        // First check if we have test data
        var bupotData = taxTransactionDetailRepository.findEBupotUnifikasiByDateRange(START_DATE, END_DATE);
        if (bupotData.isEmpty()) {
            return; // Skip if no test data
        }

        byte[] excelData = coretaxExportService.exportBupotUnifikasi(START_DATE, END_DATE);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            Sheet dataSheet = workbook.getSheet("DATA");

            // Row 0 is header, row 1+ is data
            int dataRowCount = dataSheet.getLastRowNum();
            assertThat(dataRowCount).isGreaterThanOrEqualTo(1);

            // Verify first data row has bupot number
            Row firstDataRow = dataSheet.getRow(1);
            String bupotNumber = firstDataRow.getCell(0).getStringCellValue();
            assertThat(bupotNumber).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should get export statistics")
    void shouldGetExportStatistics() {
        CoretaxExportService.ExportStatistics stats = coretaxExportService.getExportStatistics(START_DATE, END_DATE);

        assertThat(stats).isNotNull();
        assertThat(stats.fakturKeluaranCount()).isGreaterThanOrEqualTo(0);
        assertThat(stats.fakturMasukanCount()).isGreaterThanOrEqualTo(0);
        assertThat(stats.bupotUnifikasiCount()).isGreaterThanOrEqualTo(0);
        assertThat(stats.totalPPNKeluaran()).isNotNull();
        assertThat(stats.totalPPNMasukan()).isNotNull();
        assertThat(stats.totalPPh()).isNotNull();
    }

    @Test
    @DisplayName("Should return correct statistics counts")
    void shouldReturnCorrectStatisticsCounts() {
        // Get counts from repository
        int expectedFakturKeluaran = taxTransactionDetailRepository.findEFakturKeluaranByDateRange(START_DATE, END_DATE).size();
        int expectedFakturMasukan = taxTransactionDetailRepository.findEFakturMasukanByDateRange(START_DATE, END_DATE).size();
        int expectedBupot = taxTransactionDetailRepository.findEBupotUnifikasiByDateRange(START_DATE, END_DATE).size();

        CoretaxExportService.ExportStatistics stats = coretaxExportService.getExportStatistics(START_DATE, END_DATE);

        assertThat(stats.fakturKeluaranCount()).isEqualTo(expectedFakturKeluaran);
        assertThat(stats.fakturMasukanCount()).isEqualTo(expectedFakturMasukan);
        assertThat(stats.bupotUnifikasiCount()).isEqualTo(expectedBupot);
    }

    @Test
    @DisplayName("Should handle empty date range gracefully")
    void shouldHandleEmptyDateRange() throws IOException {
        // Use a date range in the far past where no data exists
        LocalDate pastStart = LocalDate.of(2000, 1, 1);
        LocalDate pastEnd = LocalDate.of(2000, 1, 31);

        // Should not throw exception
        byte[] fakturKeluaran = coretaxExportService.exportEFakturKeluaran(pastStart, pastEnd);
        byte[] fakturMasukan = coretaxExportService.exportEFakturMasukan(pastStart, pastEnd);
        byte[] bupot = coretaxExportService.exportBupotUnifikasi(pastStart, pastEnd);

        // Should return valid Excel files with headers but no data
        assertThat(fakturKeluaran).isNotNull();
        assertThat(fakturMasukan).isNotNull();
        assertThat(bupot).isNotNull();

        // Verify workbook structure is valid
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(fakturKeluaran))) {
            assertThat(workbook.getSheet("DATA")).isNotNull();
            assertThat(workbook.getSheet("REF")).isNotNull();
        }
    }

    @Test
    @DisplayName("Should include reference sheet with transaction codes")
    void shouldIncludeReferenceSheetWithTransactionCodes() throws IOException {
        byte[] excelData = coretaxExportService.exportEFakturKeluaran(START_DATE, END_DATE);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            Sheet refSheet = workbook.getSheet("REF");
            assertThat(refSheet).isNotNull();

            // Verify reference codes are present
            Row headerRow = refSheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("Kode");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("Keterangan");

            // Check first reference entry
            Row firstRefRow = refSheet.getRow(1);
            assertThat(firstRefRow.getCell(0).getStringCellValue()).isEqualTo("01");
        }
    }

    @Test
    @DisplayName("Should include Bupot reference sheet with tax object codes")
    void shouldIncludeBupotReferenceSheetWithTaxObjectCodes() throws IOException {
        byte[] excelData = coretaxExportService.exportBupotUnifikasi(START_DATE, END_DATE);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            Sheet refSheet = workbook.getSheet("REF");
            assertThat(refSheet).isNotNull();

            // Verify reference codes are present
            Row headerRow = refSheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("Kode Objek Pajak");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("Keterangan");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("Tarif Default (%)");

            // Check first reference entry (PPh 23 Jasa Teknik)
            Row firstRefRow = refSheet.getRow(1);
            assertThat(firstRefRow.getCell(0).getStringCellValue()).isEqualTo("24-104-01");
        }
    }
}
