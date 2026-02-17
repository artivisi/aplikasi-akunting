package com.artivisi.accountingfinance.functional.manufacturing;

import com.artivisi.accountingfinance.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Test configuration for Coffee Shop analysis report test.
 * Imports coffee shop seed data + financial transactions for analysis.
 *
 * Separate from CoffeeTestDataInitializer because importing 18_transactions.csv
 * triggers TRUNCATE TABLE transactions CASCADE, which cascades to production_orders
 * (FK id_transaction). Manufacturing tests need production_orders intact.
 */
@TestConfiguration
@Profile("functional")
@RequiredArgsConstructor
@Slf4j
public class CoffeeAnalysisTestDataInitializer {

    private final DataImportService dataImportService;

    @PostConstruct
    public void importCoffeeAnalysisTestData() {
        try {
            log.info("Importing Coffee Shop industry seed data (analysis)...");
            byte[] seedZip = createZipFromDirectory("industry-seed/coffee-shop/seed-data");
            DataImportService.ImportResult seedResult = dataImportService.importAllData(seedZip);
            log.info("Coffee Shop seed imported: {} records in {}ms",
                seedResult.totalRecords(), seedResult.durationMs());

            log.info("Importing coffee analysis test data...");
            byte[] testDataZip = createZipFromTestData("src/test/resources/testdata/coffee");
            DataImportService.ImportResult testResult = dataImportService.importAllData(testDataZip);
            log.info("Coffee analysis test data imported: {} records in {}ms",
                testResult.totalRecords(), testResult.durationMs());

        } catch (Exception e) {
            log.error("Failed to import coffee analysis test data", e);
            throw new RuntimeException("Coffee analysis test data initialization failed", e);
        }
    }

    private byte[] createZipFromTestData(String testDataDir) throws IOException {
        Path testDir = Paths.get(testDataDir).toAbsolutePath();

        if (!Files.exists(testDir)) {
            throw new IOException("Test data directory not found: " + testDir);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            addTestFileToZip(zos, testDir, "company-config.csv", "01_company_config.csv");
            addTestFileToZip(zos, testDir, "fiscal-periods.csv", "11_fiscal_periods.csv");
            addTestFileToZip(zos, testDir, "employees.csv", "15_employees.csv");
            addTestFileToZip(zos, testDir, "financial-transactions.csv", "18_transactions.csv");
            addTestFileToZip(zos, testDir, "financial-journal-entries.csv", "20_journal_entries.csv");
        }

        return baos.toByteArray();
    }

    private void addTestFileToZip(ZipOutputStream zos, Path testDir, String sourceFile, String zipEntry) throws IOException {
        Path filePath = testDir.resolve(sourceFile);
        if (Files.exists(filePath)) {
            ZipEntry entry = new ZipEntry(zipEntry);
            zos.putNextEntry(entry);
            Files.copy(filePath, zos);
            zos.closeEntry();
        }
    }

    private byte[] createZipFromDirectory(String dirPath) throws IOException {
        Path seedDir = Paths.get(dirPath).toAbsolutePath();

        if (!Files.exists(seedDir)) {
            throw new IOException("Seed data directory not found: " + seedDir);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walk(seedDir)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        Path relativePath = seedDir.relativize(path);
                        ZipEntry entry = new ZipEntry(relativePath.toString().replace('\\', '/'));
                        zos.putNextEntry(entry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to zip file: " + path, e);
                    }
                });
        }

        return baos.toByteArray();
    }
}
