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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional test for importing Online Seller industry seed data.
 *
 * Tests:
 * 1. Create ZIP from industry-seed/online-seller/seed-data
 * 2. Import via Settings > Import Data
 * 3. Verify COA screen shows marketplace accounts
 * 4. Verify Templates screen shows marketplace templates
 */
@DisplayName("Online Seller Industry Seed Import Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OnlineSellerSeedImportTest extends PlaywrightTestBase {

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

    @PersistenceContext
    private EntityManager entityManager;

    private LoginPage loginPage;
    private ChartOfAccountsPage coaPage;
    private TemplateListPage templateListPage;

    private static Path seedZipPath;

    // Expected counts from online seller seed data
    // COA: 87 accounts in seed file
    private static final int EXPECTED_COA_COUNT = 87;
    // Templates: 37 non-system imported + 4 system templates preserved from V004
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
    @DisplayName("Step 1: Create ZIP from online seller seed data")
    void step1_createSeedZip() throws IOException {
        Path seedDataDir = Paths.get("industry-seed/online-seller/seed-data");
        assertThat(Files.exists(seedDataDir))
            .as("Seed data directory should exist: " + seedDataDir.toAbsolutePath())
            .isTrue();

        // Create ZIP in memory and save to temp file
        seedZipPath = Files.createTempFile("online-seller-seed-", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(seedZipPath))) {
            Files.walk(seedDataDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String entryName = seedDataDir.relativize(file).toString();
                        entryName = entryName.replace("\\", "/");
                        zos.putNextEntry(new java.util.zip.ZipEntry(entryName));
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
    @DisplayName("Step 2: Import seed data via Settings > Import")
    void step2_importSeedData() {
        assertThat(seedZipPath).isNotNull();
        assertThat(Files.exists(seedZipPath)).isTrue();

        page.navigate(baseUrl() + "/settings/import");
        page.waitForLoadState();

        // Verify import page displays
        assertThat(page.locator("#import-title")).isVisible();

        // Upload the seed ZIP file
        page.locator("input[type='file']").setInputFiles(seedZipPath.toAbsolutePath());

        // Click import button (with confirmation dialog handler)
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

        // Debug: capture page state
        String currentUrl = page.url();
        String pageContent = page.content();

        // Check for success
        var successLocator = page.locator("#import-success-message");
        var errorLocator = page.locator("#import-error-message");

        if (errorLocator.count() > 0 && errorLocator.isVisible()) {
            String errorText = errorLocator.textContent();
            if (errorText != null && !errorText.trim().isEmpty()) {
                throw new AssertionError("Import failed with error: " + errorText);
            }
        }

        // Check for validation errors
        var validationErrors = page.locator(".invalid-feedback, .field-error, .text-danger");
        if (validationErrors.count() > 0 && validationErrors.first().isVisible()) {
            String errorText = validationErrors.first().textContent();
            throw new AssertionError("Validation error: " + errorText);
        }

        boolean hasSuccessMessage = successLocator.count() > 0 && successLocator.isVisible();
        boolean wasRedirected = currentUrl.contains("/settings") && !currentUrl.contains("/import");

        // If neither success nor redirect, print debug info
        if (!hasSuccessMessage && !wasRedirected) {
            System.err.println("DEBUG: Current URL = " + currentUrl);
            System.err.println("DEBUG: Page contains 'error': " + pageContent.toLowerCase().contains("error"));
            System.err.println("DEBUG: Page contains 'success': " + pageContent.toLowerCase().contains("success"));
            // Print any alert messages
            var alerts = page.locator(".alert, [role='alert']");
            if (alerts.count() > 0) {
                System.err.println("DEBUG: Alert text: " + alerts.first().textContent());
            }
            // Print form errors
            var formFeedback = page.locator(".form-text, .help-block");
            if (formFeedback.count() > 0) {
                System.err.println("DEBUG: Form feedback: " + formFeedback.first().textContent());
            }
        }

        // Note: Import success is verified by server logs showing "Imported X records"
        // The UI may not always show success message due to timing, but import still succeeds
        // We verify actual data in subsequent steps

        // Reset admin password after import
        userRepository.findByUsername("admin").ifPresent(admin -> {
            admin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(admin);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify COA database count")
    void step3_verifyCOADatabaseCount() {
        // Clear JPA cache to ensure we see the imported data
        entityManager.clear();
        long count = accountRepository.count();
        assertThat(count)
            .as("Should have imported %d accounts", EXPECTED_COA_COUNT)
            .isEqualTo(EXPECTED_COA_COUNT);
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify COA screen shows marketplace accounts")
    void step4_verifyCOAScreenMarketplaceAccounts() {
        coaPage.navigate();

        // Verify root accounts exist
        coaPage.assertAccountRowVisible("1");
        coaPage.assertAccountRowVisible("4");
        coaPage.assertAccountRowVisible("5");

        // Expand to see marketplace saldo accounts
        coaPage.clickExpandAccount("1");
        page.waitForTimeout(500);
        coaPage.clickExpandAccount("1.1");
        page.waitForTimeout(500);

        // Verify marketplace saldo accounts
        coaPage.assertAccountNameVisible("1.1.04", "Saldo Tokopedia");
        coaPage.assertAccountNameVisible("1.1.05", "Saldo Shopee");
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify COA screen shows marketplace revenue accounts")
    void step5_verifyCOAScreenRevenueAccounts() {
        coaPage.navigate();

        // Expand revenue accounts
        coaPage.clickExpandAccount("4");
        page.waitForTimeout(500);
        coaPage.clickExpandAccount("4.1");
        page.waitForTimeout(500);

        // Verify marketplace revenue accounts
        coaPage.assertAccountNameVisible("4.1.01", "Penjualan Tokopedia");
        coaPage.assertAccountNameVisible("4.1.02", "Penjualan Shopee");
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify COA screen shows marketplace fee accounts")
    void step6_verifyCOAScreenFeeAccounts() {
        coaPage.navigate();

        // Expand expense accounts
        coaPage.clickExpandAccount("5");
        page.waitForTimeout(500);
        coaPage.clickExpandAccount("5.2");
        page.waitForTimeout(500);

        // Verify marketplace fee accounts
        coaPage.assertAccountNameVisible("5.2.01", "Biaya Admin Tokopedia");
        coaPage.assertAccountNameVisible("5.2.02", "Biaya Admin Shopee");
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify templates database count")
    void step7_verifyTemplatesDatabaseCount() {
        entityManager.clear();
        long count = templateRepository.count();
        assertThat(count)
            .as("Should have imported %d templates", EXPECTED_TEMPLATE_COUNT)
            .isEqualTo(EXPECTED_TEMPLATE_COUNT);
    }

    @Test
    @Order(8)
    @DisplayName("Step 8: Verify templates screen shows marketplace templates")
    void step8_verifyTemplatesScreenMarketplace() {
        templateListPage.navigate();

        // Verify marketplace sales templates
        templateListPage.assertTemplateVisible("Penjualan Tokopedia");
        templateListPage.assertTemplateVisible("Penjualan Shopee");
    }

    @Test
    @Order(9)
    @DisplayName("Step 9: Verify templates screen shows withdraw templates")
    void step9_verifyTemplatesScreenWithdraw() {
        templateListPage.navigate();

        // Verify withdraw templates
        templateListPage.assertTemplateVisible("Withdraw Saldo Tokopedia");
        templateListPage.assertTemplateVisible("Withdraw Saldo Shopee");
    }

    @Test
    @Order(10)
    @DisplayName("Step 10: Verify salary components count")
    void step10_verifySalaryComponents() {
        entityManager.clear();
        long count = salaryComponentRepository.count();
        assertThat(count)
            .as("Should have imported %d salary components", EXPECTED_SALARY_COMPONENT_COUNT)
            .isEqualTo(EXPECTED_SALARY_COMPONENT_COUNT);
    }

    @Test
    @Order(11)
    @DisplayName("Step 11: Verify tax deadlines count")
    void step11_verifyTaxDeadlines() {
        entityManager.clear();
        long count = taxDeadlineRepository.count();
        assertThat(count)
            .as("Should have imported %d tax deadlines", EXPECTED_TAX_DEADLINE_COUNT)
            .isEqualTo(EXPECTED_TAX_DEADLINE_COUNT);
    }

    @AfterAll
    static void cleanup() {
        if (seedZipPath != null) {
            try {
                Files.deleteIfExists(seedZipPath);
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }
}
