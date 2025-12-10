package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.ChartOfAccountsPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TemplateListPage;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.SalaryComponentRepository;
import com.artivisi.accountingfinance.repository.TaxDeadlineRepository;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional test for importing IT Services industry seed data.
 *
 * Tests:
 * 1. Create ZIP from industry-seed/it-service/seed-data
 * 2. Import via Settings > Import Data
 * 3. Verify COA screen shows imported accounts
 * 4. Verify Templates screen shows imported templates
 */
@DisplayName("IT Services Industry Seed Import Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IndustrySeedImportTest extends PlaywrightTestBase {

    @Autowired
    private ChartOfAccountRepository accountRepository;
    @Autowired
    private JournalTemplateRepository templateRepository;
    @Autowired
    private SalaryComponentRepository salaryComponentRepository;
    @Autowired
    private TaxDeadlineRepository taxDeadlineRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private LoginPage loginPage;
    private ChartOfAccountsPage coaPage;
    private TemplateListPage templateListPage;

    private static Path seedZipPath;

    // Expected counts from IT service seed data
    // COA: 75 accounts in seed file (replaces V004's 77)
    private static final int EXPECTED_COA_COUNT = 75;
    // Templates: 37 non-system imported + 4 system templates preserved from V004
    // (Post Gaji Bulanan, Penyusutan Aset Tetap, Pelepasan Aset Tetap, Jurnal Penutup Tahun)
    // Inventory templates are in V004a (test-only), not loaded for this test
    private static final int EXPECTED_TEMPLATE_COUNT = 41;
    // Salary components: 17 in seed file
    private static final int EXPECTED_SALARY_COMPONENT_COUNT = 17;
    // Tax deadlines: 8 in seed file
    private static final int EXPECTED_TAX_DEADLINE_COUNT = 8;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        coaPage = new ChartOfAccountsPage(page, baseUrl());
        templateListPage = new TemplateListPage(page, baseUrl());
        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Create ZIP from industry seed data")
    void step1_createSeedZip() throws IOException {
        Path seedDataDir = Paths.get("industry-seed/it-service/seed-data");
        assertThat(Files.exists(seedDataDir))
            .as("Seed data directory should exist: " + seedDataDir.toAbsolutePath())
            .isTrue();

        // Create ZIP in memory and save to temp file
        seedZipPath = Files.createTempFile("it-service-seed-", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(seedZipPath))) {
            Files.walk(seedDataDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String entryName = seedDataDir.relativize(file).toString();
                        // Handle Windows path separators
                        entryName = entryName.replace("\\", "/");
                        zos.putNextEntry(new ZipEntry(entryName));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to add file to ZIP: " + file, e);
                    }
                });
        }

        assertThat(Files.exists(seedZipPath)).isTrue();
        assertThat(Files.size(seedZipPath)).isGreaterThan(0);
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Import seed data via UI")
    void step2_importSeedData() {
        assertThat(seedZipPath).isNotNull();
        assertThat(Files.exists(seedZipPath)).isTrue();

        page.navigate(baseUrl() + "/settings/import");
        page.waitForLoadState();

        // Verify import page displays
        assertThat(page.locator("#import-title")).isVisible();

        // Upload the seed ZIP file
        page.locator("input[type='file']").setInputFiles(seedZipPath.toAbsolutePath());

        // Click import button (with confirmation)
        page.onDialog(dialog -> dialog.accept());

        // Import can take a while, increase timeout
        page.locator("#btn-import").click(
            new com.microsoft.playwright.Locator.ClickOptions().setTimeout(60000)
        );

        // Wait for page to load after import
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE,
            new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(60000));

        page.waitForTimeout(2000);

        // Wait for redirect or error message
        page.waitForTimeout(3000);

        // Check for success - we should be redirected or see success message
        String currentUrl = page.url();
        var successLocator = page.locator("#import-success-message");
        var errorLocator = page.locator("#import-error-message, .alert-error, .error");

        if (errorLocator.count() > 0 && errorLocator.first().isVisible()) {
            String errorText = errorLocator.first().textContent();
            throw new AssertionError("Import failed with error: " + errorText);
        }

        // Either we have success message or we were redirected
        boolean hasSuccessMessage = successLocator.count() > 0 && successLocator.isVisible();
        boolean wasRedirected = currentUrl.contains("/settings") && !currentUrl.contains("/import");

        org.assertj.core.api.Assertions.assertThat(hasSuccessMessage || wasRedirected)
            .as("Import should succeed - either show success message or redirect. Current URL: " + currentUrl)
            .isTrue();

        // Reset admin password after import
        userRepository.findByUsername("admin").ifPresent(admin -> {
            admin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(admin);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify COA imported - database count")
    void step3_verifyCOADatabaseCount() {
        long accountCount = accountRepository.count();
        assertThat(accountCount)
            .as("Should have imported %d accounts", EXPECTED_COA_COUNT)
            .isEqualTo(EXPECTED_COA_COUNT);
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify COA screen shows root accounts")
    void step4_verifyCOAScreenRootAccounts() {
        coaPage.navigate();

        // Verify page loads
        coaPage.assertPageTitleVisible();
        coaPage.assertAccountsTableVisible();

        // Verify all 5 root accounts are visible
        coaPage.assertAllRootAccountsVisible();
        coaPage.assertRootAccountsWithNames();
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify COA screen shows child accounts after expand")
    void step5_verifyCOAScreenChildAccounts() {
        coaPage.navigate();

        // Expand ASET (1)
        coaPage.clickExpandAccount("1");
        page.waitForTimeout(500);

        // Verify child accounts visible
        coaPage.assertAccountRowVisible("1.1");
        coaPage.assertAccountNameVisible("1.1", "Aset Lancar");

        coaPage.assertAccountRowVisible("1.2");
        coaPage.assertAccountNameVisible("1.2", "Aset Tetap");

        coaPage.assertAccountRowVisible("1.3");
        coaPage.assertAccountNameVisible("1.3", "Aset Tak Berwujud");

        // Expand Aset Lancar (1.1)
        coaPage.clickExpandAccount("1.1");
        page.waitForTimeout(500);

        // Verify leaf accounts
        coaPage.assertAccountRowVisible("1.1.01");
        coaPage.assertAccountNameVisible("1.1.01", "Kas");

        coaPage.assertAccountRowVisible("1.1.02");
        coaPage.assertAccountNameVisible("1.1.02", "Bank BCA");

        coaPage.assertAccountRowVisible("1.1.03");
        coaPage.assertAccountNameVisible("1.1.03", "Bank Mandiri");
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify specific IT service accounts exist")
    void step6_verifyITServiceAccounts() {
        // Verify IT service specific accounts exist in database
        assertThat(accountRepository.findByAccountCode("5.1.20"))
            .as("Should have 'Beban Cloud & Server' account")
            .isPresent();
        assertThat(accountRepository.findByAccountCode("5.1.20").get().getAccountName())
            .isEqualTo("Beban Cloud & Server");

        assertThat(accountRepository.findByAccountCode("5.1.21"))
            .as("Should have 'Beban Software & Lisensi' account")
            .isPresent();
        assertThat(accountRepository.findByAccountCode("5.1.21").get().getAccountName())
            .isEqualTo("Beban Software & Lisensi");

        assertThat(accountRepository.findByAccountCode("4.1.01"))
            .as("Should have 'Pendapatan Jasa Training' account")
            .isPresent();

        assertThat(accountRepository.findByAccountCode("4.1.02"))
            .as("Should have 'Pendapatan Jasa Konsultasi' account")
            .isPresent();

        assertThat(accountRepository.findByAccountCode("4.1.03"))
            .as("Should have 'Pendapatan Jasa Development' account")
            .isPresent();
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify templates imported - database count")
    void step7_verifyTemplatesDatabaseCount() {
        long templateCount = templateRepository.count();
        assertThat(templateCount)
            .as("Should have imported %d templates", EXPECTED_TEMPLATE_COUNT)
            .isEqualTo(EXPECTED_TEMPLATE_COUNT);
    }

    @Test
    @Order(8)
    @DisplayName("Step 8: Verify templates screen shows imported templates")
    void step8_verifyTemplatesScreen() {
        templateListPage.navigate();

        // Verify page loads
        templateListPage.assertPageTitleVisible();
        templateListPage.assertTemplateListVisible();

        // Verify template count is greater than 0
        templateListPage.assertTemplateCountGreaterThan(0);
    }

    @Test
    @Order(9)
    @DisplayName("Step 9: Verify specific PKP templates exist")
    void step9_verifyPKPTemplates() {
        templateListPage.navigate();

        // Verify PKP-related templates are visible
        templateListPage.assertTemplateVisible("Pendapatan Jasa + PPN");
        templateListPage.assertTemplateVisible("Pendapatan Jasa + PPh 23 Dipotong");
        templateListPage.assertTemplateVisible("Pendapatan Jasa + PPN + PPh 23");
        templateListPage.assertTemplateVisible("Pembelian dengan PPN");
        templateListPage.assertTemplateVisible("Setor PPN");
        templateListPage.assertTemplateVisible("Setor PPh 21");
        templateListPage.assertTemplateVisible("Setor PPh 23");
    }

    @Test
    @Order(10)
    @DisplayName("Step 10: Verify payroll templates exist")
    void step10_verifyPayrollTemplates() {
        templateListPage.navigate();

        templateListPage.assertTemplateVisible("Post Gaji Bulanan");
        templateListPage.assertTemplateVisible("Bayar Hutang Gaji");
        templateListPage.assertTemplateVisible("Bayar Hutang BPJS");
    }

    @Test
    @Order(11)
    @DisplayName("Step 11: Verify salary components imported")
    void step11_verifySalaryComponents() {
        long count = salaryComponentRepository.count();
        assertThat(count)
            .as("Should have imported %d salary components", EXPECTED_SALARY_COMPONENT_COUNT)
            .isEqualTo(EXPECTED_SALARY_COMPONENT_COUNT);

        // Verify specific components
        assertThat(salaryComponentRepository.findByCode("GAPOK"))
            .as("Should have 'Gaji Pokok' component")
            .isPresent();
        assertThat(salaryComponentRepository.findByCode("BPJS-KES-P"))
            .as("Should have 'BPJS Kesehatan (Perusahaan)' component")
            .isPresent();
        assertThat(salaryComponentRepository.findByCode("PPH21"))
            .as("Should have 'PPh 21' component")
            .isPresent();
    }

    @Test
    @Order(12)
    @DisplayName("Step 12: Verify tax deadlines imported")
    void step12_verifyTaxDeadlines() {
        long count = taxDeadlineRepository.count();
        assertThat(count)
            .as("Should have imported %d tax deadlines", EXPECTED_TAX_DEADLINE_COUNT)
            .isEqualTo(EXPECTED_TAX_DEADLINE_COUNT);
    }

    @AfterAll
    static void cleanup() {
        // Clean up temp file
        if (seedZipPath != null) {
            try {
                Files.deleteIfExists(seedZipPath);
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }
}
