package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.CompanyConfig;
import com.artivisi.accountingfinance.repository.CompanyConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DataExportService.
 * Verifies export of company config including tax profile fields.
 *
 * Note: Import tests are omitted here because importAllData() truncates tables
 * which would affect other tests. Import is tested implicitly via the functional
 * tests and data initializers that load seed data.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Data Export Service - Tax Profile Fields")
class DataExportImportTest {

    @Autowired
    private DataExportService dataExportService;

    @Autowired
    private CompanyConfigRepository companyConfigRepository;

    @Test
    @DisplayName("Should export company config with tax profile fields")
    @Transactional
    void exportCompanyConfigWithTaxProfileFields() throws IOException {
        // Given: Save company config with tax profile fields (will be rolled back)
        CompanyConfig config = companyConfigRepository.findFirst().orElse(new CompanyConfig());
        config.setCompanyName("PT Test Company");
        config.setNpwp("01.234.567.8-901.234");
        config.setEstablishedDate(LocalDate.of(2008, 5, 15));
        config.setIsPkp(true);
        config.setPkpSince(LocalDate.of(2010, 1, 1));
        companyConfigRepository.save(config);
        companyConfigRepository.flush();

        // When: Export all data
        byte[] exportedData = dataExportService.exportAllData();

        // Then: Verify the ZIP contains company config with tax profile fields
        String companyConfigCsv = extractFileFromZip(exportedData, "01_company_config.csv");

        assertThat(companyConfigCsv)
            .as("CSV should include tax profile fields with correct values")
            .contains("established_date,is_pkp,pkp_since")
            .contains("2008-05-15")
            .contains("true")
            .contains("2010-01-01");
    }

    @Test
    @DisplayName("Should handle null tax profile fields in export")
    @Transactional
    void handleNullTaxProfileFieldsInExport() throws IOException {
        // Given: Company config without tax profile fields
        CompanyConfig config = companyConfigRepository.findFirst().orElse(new CompanyConfig());
        config.setCompanyName("PT No Tax Profile");
        config.setNpwp("03.456.789.0-123.456");
        config.setEstablishedDate(null);
        config.setIsPkp(null);
        config.setPkpSince(null);
        companyConfigRepository.save(config);
        companyConfigRepository.flush();

        // When: Export
        byte[] exportedData = dataExportService.exportAllData();
        String companyConfigCsv = extractFileFromZip(exportedData, "01_company_config.csv");

        // Then: Should have empty values for tax profile fields
        assertThat(companyConfigCsv)
            .as("CSV should include tax profile fields and company name")
            .contains("established_date,is_pkp,pkp_since")
            .contains("PT No Tax Profile");
    }

    @Test
    @DisplayName("Export CSV should have correct column order for tax profile fields")
    @Transactional
    void exportCsvShouldHaveCorrectColumnOrder() throws IOException {
        // Given: Company config with tax profile fields
        CompanyConfig config = companyConfigRepository.findFirst().orElse(new CompanyConfig());
        config.setCompanyName("PT Column Order Test");
        config.setEstablishedDate(LocalDate.of(2015, 6, 20));
        config.setIsPkp(false);
        companyConfigRepository.save(config);
        companyConfigRepository.flush();

        // When: Export
        byte[] exportedData = dataExportService.exportAllData();
        String companyConfigCsv = extractFileFromZip(exportedData, "01_company_config.csv");

        // Then: Header should have tax profile fields at the end
        String[] lines = companyConfigCsv.split("\n");
        assertThat(lines).hasSizeGreaterThanOrEqualTo(2);

        String header = lines[0];
        assertThat(header)
            .as("Header should end with industry field")
            .endsWith("established_date,is_pkp,pkp_since,industry");

        // Data line should have the values
        String dataLine = lines[1];
        assertThat(dataLine)
            .as("Data should contain established date and isPkp=false")
            .contains("2015-06-20")
            .contains("false");
    }

    private String extractFileFromZip(byte[] zipData, String filename) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(filename)) {
                    return new String(zis.readAllBytes());
                }
            }
        }
        throw new IllegalArgumentException("File not found in ZIP: " + filename);
    }
}
