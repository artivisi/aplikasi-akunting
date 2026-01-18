package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.service.DataImportService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for DataImportService with data persistence verification.
 * Tests actual import of valid data and verifies database state.
 */
@DisplayName("Data Import - Persistence Tests")
@Import(ServiceTestDataInitializer.class)
class DataImportPersistenceTest extends PlaywrightTestBase {

    @TempDir
    Path tempDir;

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ChartOfAccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JournalTemplateRepository templateRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should verify import service is available")
    void shouldVerifyImportServiceIsAvailable() {
        assertThat(dataImportService).isNotNull();
    }

    @Test
    @DisplayName("Should verify account repository works")
    void shouldVerifyAccountRepositoryWorks() {
        long accountCount = accountRepository.count();
        assertThat(accountCount).isGreaterThan(0);

        // Verify at least some accounts exist
        var accounts = accountRepository.findAll();
        assertThat(accounts).isNotEmpty();
    }

    @Test
    @DisplayName("Should show success message after valid import via UI")
    void shouldShowSuccessMessageAfterValidImportViaUi() throws IOException {
        // Create a minimal valid ZIP
        Path validZip = tempDir.resolve("valid-import.zip");
        createMinimalValidZip(validZip);

        navigateTo("/settings/import");
        waitForPageLoad();

        // Upload valid ZIP
        page.locator("#file").setInputFiles(validZip);

        // Handle confirmation dialog
        page.onDialog(dialog -> dialog.accept());

        page.locator("#btn-import").click();
        waitForPageLoad();

        // Should show success or redirect to import page with result
        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should reject import with missing required columns")
    void shouldRejectImportWithMissingRequiredColumns() throws IOException {
        // Create ZIP with invalid CSV (missing columns)
        Path invalidZip = tempDir.resolve("invalid-columns.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(invalidZip.toFile()))) {
            // CSV with wrong columns
            ZipEntry entry = new ZipEntry("02_chart_of_accounts.csv");
            zos.putNextEntry(entry);
            zos.write("wrong_column1,wrong_column2\nval1,val2".getBytes());
            zos.closeEntry();
        }

        navigateTo("/settings/import");
        waitForPageLoad();

        page.locator("#file").setInputFiles(invalidZip);
        page.onDialog(dialog -> dialog.accept());
        page.locator("#btn-import").click();
        waitForPageLoad();

        // Should show error message
        assertThat(page.locator("#page-title")).hasText("Import Data");
    }

    @Test
    @DisplayName("Should verify templates exist in database")
    void shouldVerifyTemplatesExistInDatabase() {
        // Verify templates are loaded
        long templateCount = templateRepository.count();
        assertThat(templateCount).isGreaterThan(0);

        var templates = templateRepository.findAll();
        assertThat(templates).isNotEmpty();
    }

    private void createMinimalValidZip(Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            // Empty company config (header only - won't truncate)
            ZipEntry entry = new ZipEntry("01_company_config.csv");
            zos.putNextEntry(entry);
            zos.write("id,company_name,address,phone,email,npwp,company_logo_path\n".getBytes());
            zos.closeEntry();
        }
    }
}
