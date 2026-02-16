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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DataExportService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("DataExportService Integration Tests")
class DataExportServiceTest {

    @Autowired
    private DataExportService dataExportService;

    @Nested
    @DisplayName("Export Statistics Operations")
    class ExportStatisticsTests {

        @Test
        @DisplayName("Should get export statistics")
        void shouldGetExportStatistics() {
            DataExportService.ExportStatistics stats = dataExportService.getExportStatistics();

            assertThat(stats).isNotNull();
            assertThat(stats.accountCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.journalEntryCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.transactionCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.clientCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.projectCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.invoiceCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.employeeCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.payrollRunCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.documentCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.auditLogCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.templateCount()).isGreaterThanOrEqualTo(0);
            assertThat(stats.userCount()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Should calculate total records")
        void shouldCalculateTotalRecords() {
            DataExportService.ExportStatistics stats = dataExportService.getExportStatistics();

            long total = stats.totalRecords();
            long expected = stats.accountCount() + stats.journalEntryCount() + stats.transactionCount() +
                    stats.clientCount() + stats.projectCount() + stats.invoiceCount() +
                    stats.employeeCount() + stats.payrollRunCount() + stats.documentCount() +
                    stats.auditLogCount() + stats.templateCount() + stats.userCount();

            assertThat(total).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should have non-zero account count from test data")
        void shouldHaveNonZeroAccountCountFromTestData() {
            DataExportService.ExportStatistics stats = dataExportService.getExportStatistics();
            // Test data should have some accounts
            assertThat(stats.accountCount()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should have non-zero template count from test data")
        void shouldHaveNonZeroTemplateCountFromTestData() {
            DataExportService.ExportStatistics stats = dataExportService.getExportStatistics();
            // Test data should have some templates
            assertThat(stats.templateCount()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("Export All Data Operations")
    class ExportAllDataTests {

        @Test
        @DisplayName("Should export all data as ZIP")
        void shouldExportAllDataAsZip() throws IOException {
            byte[] zipData = dataExportService.exportAllData();

            assertThat(zipData).isNotNull();
            assertThat(zipData).hasSizeGreaterThan(0);
        }

        @Test
        @DisplayName("Should contain manifest file")
        void shouldContainManifestFile() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("MANIFEST.md");
        }

        @Test
        @DisplayName("Should contain company config CSV")
        void shouldContainCompanyConfigCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("01_company_config.csv");
        }

        @Test
        @DisplayName("Should contain chart of accounts CSV")
        void shouldContainChartOfAccountsCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("02_chart_of_accounts.csv");
        }

        @Test
        @DisplayName("Should contain salary components CSV")
        void shouldContainSalaryComponentsCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("03_salary_components.csv");
        }

        @Test
        @DisplayName("Should contain journal templates CSV")
        void shouldContainJournalTemplatesCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("04_journal_templates.csv");
        }

        @Test
        @DisplayName("Should contain clients CSV")
        void shouldContainClientsCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("07_clients.csv");
        }

        @Test
        @DisplayName("Should contain projects CSV")
        void shouldContainProjectsCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("08_projects.csv");
        }

        @Test
        @DisplayName("Should contain employees CSV")
        void shouldContainEmployeesCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("15_employees.csv");
        }

        @Test
        @DisplayName("Should contain transactions CSV")
        void shouldContainTransactionsCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("18_transactions.csv");
        }

        @Test
        @DisplayName("Should contain journal entries CSV")
        void shouldContainJournalEntriesCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("20_journal_entries.csv");
        }

        @Test
        @DisplayName("Should contain users CSV")
        void shouldContainUsersCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("28_users.csv");
        }

        @Test
        @DisplayName("Should contain audit logs CSV")
        void shouldContainAuditLogsCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("32_audit_logs.csv");
        }

        @Test
        @DisplayName("Should contain document index CSV")
        void shouldContainDocumentIndexCsv() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            assertThat(fileNames).contains("documents/index.csv");
        }

        @Test
        @DisplayName("Should contain all expected CSV files")
        void shouldContainAllExpectedCsvFiles() throws IOException {
            byte[] zipData = dataExportService.exportAllData();
            Set<String> fileNames = getZipFileNames(zipData);

            // All numbered CSV files should be present
            assertThat(fileNames).contains(
                    "01_company_config.csv",
                    "02_chart_of_accounts.csv",
                    "03_salary_components.csv",
                    "04_journal_templates.csv",
                    "05_journal_template_lines.csv",
                    "06_journal_template_tags.csv",
                    "07_clients.csv",
                    "08_projects.csv",
                    "09_project_milestones.csv",
                    "10_project_payment_terms.csv",
                    "11_fiscal_periods.csv",
                    "12_tax_deadlines.csv",
                    "13_company_bank_accounts.csv",
                    "14_merchant_mappings.csv",
                    "15_employees.csv",
                    "16_employee_salary_components.csv",
                    "17_invoices.csv",
                    "18_transactions.csv",
                    "19_transaction_account_mappings.csv",
                    "19a_transaction_variables.csv",
                    "20_journal_entries.csv"
            );
        }

        @Test
        @DisplayName("Should produce valid ZIP format")
        void shouldProduceValidZipFormat() throws IOException {
            byte[] zipData = dataExportService.exportAllData();

            // Should not throw exception when reading as ZIP
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
                ZipEntry entry;
                int entryCount = 0;
                while ((entry = zis.getNextEntry()) != null) {
                    assertThat(entry.getName()).isNotEmpty();
                    entryCount++;
                    zis.closeEntry();
                }
                assertThat(entryCount).isGreaterThan(0);
            }
        }
    }

    private Set<String> getZipFileNames(byte[] zipData) throws IOException {
        Set<String> names = new HashSet<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                names.add(entry.getName());
                zis.closeEntry();
            }
        }
        return names;
    }
}
