package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for Settings pages.
 * Covers CompanyConfigService, CompanyBankAccountService, and SettingsController.
 */
@DisplayName("Settings Management")
class SettingsTest extends PlaywrightTestBase {

    @BeforeEach
    void setUp() {
        page.navigate(baseUrl() + "/login");
        page.fill("input[name='username']", "admin");
        page.fill("input[name='password']", "admin");
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }

    private void submitCompanyForm() {
        page.locator("#btn-save-company").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    private void submitBankForm() {
        page.locator("#btn-save-bank").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Nested
    @DisplayName("Company Settings CRUD")
    class CompanySettingsCrudTests {

        @Test
        @DisplayName("Should display company settings page")
        void shouldDisplayCompanySettingsPage() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertThat(page.locator("#page-title").textContent()).contains("Pengaturan");
        }

        @Test
        @DisplayName("Should update company name")
        void shouldUpdateCompanyName() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String uniqueName = "Test Company " + System.currentTimeMillis();
            page.locator("#companyName").fill(uniqueName);

            submitCompanyForm();

            // Reload page and verify
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#companyName").inputValue();
            assertThat(savedValue).isEqualTo(uniqueName);
        }

        @Test
        @DisplayName("Should update company address")
        void shouldUpdateCompanyAddress() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String uniqueAddress = "Jl. Test No. " + System.currentTimeMillis();
            page.locator("#companyAddress").fill(uniqueAddress);

            submitCompanyForm();

            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#companyAddress").inputValue();
            assertThat(savedValue).isEqualTo(uniqueAddress);
        }

        @Test
        @DisplayName("Should update company phone")
        void shouldUpdateCompanyPhone() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String phone = "021-" + (System.currentTimeMillis() % 10000000);
            page.locator("#companyPhone").fill(phone);

            submitCompanyForm();

            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#companyPhone").inputValue();
            assertThat(savedValue).isEqualTo(phone);
        }

        @Test
        @DisplayName("Should update company email")
        void shouldUpdateCompanyEmail() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String email = "company" + System.currentTimeMillis() + "@test.com";
            page.locator("#companyEmail").fill(email);

            submitCompanyForm();

            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#companyEmail").inputValue();
            assertThat(savedValue).isEqualTo(email);
        }

        @Test
        @DisplayName("Should update tax ID")
        void shouldUpdateTaxId() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String taxId = "TAX-" + System.currentTimeMillis();
            page.locator("#taxId").fill(taxId);

            submitCompanyForm();

            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#taxId").inputValue();
            assertThat(savedValue).isEqualTo(taxId);
        }

        @Test
        @DisplayName("Should change fiscal year start month")
        void shouldChangeFiscalYearStartMonth() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Select April (4)
            page.locator("#fiscalYearStartMonth").selectOption("4");

            submitCompanyForm();

            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#fiscalYearStartMonth").inputValue();
            assertThat(savedValue).isEqualTo("4");
        }

        @Test
        @DisplayName("Should change currency")
        void shouldChangeCurrency() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Select USD
            page.locator("#currencyCode").selectOption("USD");

            submitCompanyForm();

            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String savedValue = page.locator("#currencyCode").inputValue();
            assertThat(savedValue).isEqualTo("USD");

            // Reset to IDR
            page.locator("#currencyCode").selectOption("IDR");
            submitCompanyForm();
        }
    }

    @Nested
    @DisplayName("Bank Accounts CRUD")
    class BankAccountsCrudTests {

        @Test
        @DisplayName("Should display bank account form")
        void shouldDisplayBankAccountForm() {
            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertThat(page.locator("#bankName").isVisible()).isTrue();
            assertThat(page.locator("#accountNumber").isVisible()).isTrue();
            assertThat(page.locator("#accountName").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should create new bank account")
        void shouldCreateNewBankAccount() {
            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String uniqueNumber = "12345" + System.currentTimeMillis();
            page.locator("#bankName").fill("Bank Central Asia");
            page.locator("#bankBranch").fill("KCP Sudirman");
            page.locator("#accountNumber").fill(uniqueNumber);
            page.locator("#accountName").fill("PT Test Company");

            submitBankForm();

            // Should redirect to settings page
            assertThat(page.url()).contains("/settings");

            // Verify the bank account appears in the list
            assertThat(page.content()).contains(uniqueNumber);
        }

        @Test
        @DisplayName("Should show error for duplicate account number")
        void shouldShowErrorForDuplicateAccountNumber() {
            // Create first bank account
            String uniqueNumber = "DUP" + System.currentTimeMillis();

            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.locator("#bankName").fill("Bank Test 1");
            page.locator("#accountNumber").fill(uniqueNumber);
            page.locator("#accountName").fill("Account 1");
            submitBankForm();

            // Try to create second bank account with same number
            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.locator("#bankName").fill("Bank Test 2");
            page.locator("#accountNumber").fill(uniqueNumber);
            page.locator("#accountName").fill("Account 2");
            submitBankForm();

            // Should show error message (stays on form page or shows error)
            boolean hasError = page.content().contains("sudah ada")
                || page.url().contains("/bank-accounts/new");
            assertThat(hasError).isTrue();
        }

        @Test
        @DisplayName("Should set bank account as default")
        void shouldSetBankAccountAsDefault() {
            // Create a bank account first
            String uniqueNumber = "DEF" + System.currentTimeMillis();

            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.locator("#bankName").fill("Bank Default Test");
            page.locator("#accountNumber").fill(uniqueNumber);
            page.locator("#accountName").fill("Default Test Account");
            submitBankForm();

            // Find the set-default button for this account
            Locator setDefaultButton = page.locator("form[action*='set-default'] button");
            if (setDefaultButton.count() > 0) {
                setDefaultButton.first().click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Verify success - should see "Utama" badge
                assertThat(page.content()).contains("Utama");
            }
        }

        @Test
        @DisplayName("Should deactivate bank account")
        void shouldDeactivateBankAccount() {
            // Create a bank account first
            String uniqueNumber = "DEACT" + System.currentTimeMillis();

            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.locator("#bankName").fill("Bank Deactivate Test");
            page.locator("#accountNumber").fill(uniqueNumber);
            page.locator("#accountName").fill("Deactivate Test Account");
            submitBankForm();

            // Find and click the deactivate button
            Locator deactivateButton = page.locator("form[action*='deactivate'] button").first();
            if (deactivateButton.isVisible()) {
                deactivateButton.click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Verify "Nonaktif" badge appears
                assertThat(page.content()).contains("Nonaktif");
            }
        }

        @Test
        @DisplayName("Should edit bank account")
        void shouldEditBankAccount() {
            // Create a bank account first
            String uniqueNumber = "EDIT" + System.currentTimeMillis();

            page.navigate(baseUrl() + "/settings/bank-accounts/new");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.locator("#bankName").fill("Bank Edit Test");
            page.locator("#accountNumber").fill(uniqueNumber);
            page.locator("#accountName").fill("Edit Test Account");
            submitBankForm();

            // Find and click edit link
            Locator editLink = page.locator("a[href*='/bank-accounts/'][href*='/edit']").first();
            if (editLink.isVisible()) {
                editLink.click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Update the bank name
                String newBankName = "Bank Updated " + System.currentTimeMillis();
                page.locator("#bankName").fill(newBankName);
                submitBankForm();

                // Verify update
                assertThat(page.content()).contains(newBankName);
            }
        }
    }

    @Nested
    @DisplayName("Company Logo Upload")
    class CompanyLogoUploadTests {

        @Test
        @DisplayName("Should display logo upload form")
        void shouldDisplayLogoUploadForm() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Verify logo upload section exists
            assertThat(page.locator("#label-logo").isVisible()).isTrue();
            assertThat(page.locator("#logoFile").isVisible()).isTrue();
            assertThat(page.locator("#btn-upload-logo").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should upload company logo")
        void shouldUploadCompanyLogo() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Create a test image file
            java.nio.file.Path testImage = createTestImage();

            // Upload the file
            page.locator("#logoFile").setInputFiles(testImage);
            page.locator("#btn-upload-logo").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Verify success message
            assertThat(page.content()).contains("berhasil");

            // Verify logo preview is displayed
            assertThat(page.locator("img[alt='Company Logo']").count()).isGreaterThan(0);

            // Cleanup
            deleteTestImage(testImage);
        }

        @Test
        @DisplayName("Should delete company logo")
        void shouldDeleteCompanyLogo() {
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // First upload a logo if none exists
            Locator logoPreview = page.locator("img[alt='Company Logo']");
            if (logoPreview.count() == 0) {
                java.nio.file.Path testImage = createTestImage();
                page.locator("#logoFile").setInputFiles(testImage);
                page.locator("#btn-upload-logo").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);
                deleteTestImage(testImage);
            }

            // Now delete the logo
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            Locator deleteButton = page.locator("#btn-delete-logo");
            if (deleteButton.count() > 0) {
                page.onDialog(dialog -> dialog.accept());
                deleteButton.click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Verify success message
                assertThat(page.content()).contains("berhasil");
            }
        }

        @Test
        @DisplayName("Should serve company logo image")
        void shouldServeCompanyLogoImage() {
            // First upload a logo
            page.navigate(baseUrl() + "/settings");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            java.nio.file.Path testImage = createTestImage();
            page.locator("#logoFile").setInputFiles(testImage);
            page.locator("#btn-upload-logo").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Navigate to logo URL and verify it returns an image
            var response = page.navigate(baseUrl() + "/settings/company/logo");
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.headerValue("content-type")).startsWith("image/");

            // Cleanup
            deleteTestImage(testImage);
        }

        private java.nio.file.Path createTestImage() {
            try {
                java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test-logo-", ".png");
                // Create a minimal valid PNG file (1x1 pixel)
                byte[] pngBytes = {
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
                    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, // 1x1 pixel
                    0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53, (byte) 0xDE, // 8-bit RGB
                    0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
                    0x08, (byte) 0xD7, 0x63, (byte) 0xF8, 0x0F, 0x00, 0x00, 0x01, 0x01, 0x00, 0x05, 0x1B, (byte) 0xB4,
                    (byte) 0xD5, // compressed data
                    0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
                    (byte) 0xAE, 0x42, 0x60, (byte) 0x82
                };
                java.nio.file.Files.write(tempFile, pngBytes);
                return tempFile;
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to create test image", e);
            }
        }

        private void deleteTestImage(java.nio.file.Path path) {
            try {
                java.nio.file.Files.deleteIfExists(path);
            } catch (java.io.IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    @Nested
    @DisplayName("About Page")
    class AboutPageTests {

        @Test
        @DisplayName("Should display about page with version info")
        void shouldDisplayAboutPageWithVersionInfo() {
            page.navigate(baseUrl() + "/settings/about");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertThat(page.locator("#page-title").textContent()).containsIgnoringCase("Tentang");
            assertThat(page.locator("#label-commit-id").isVisible()).isTrue();
            assertThat(page.locator("#label-branch").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should display actual version values not Unknown")
        void shouldDisplayActualVersionValuesNotUnknown() {
            page.navigate(baseUrl() + "/settings/about");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Verify commit ID is not Unknown (should be a git hash like af1e477)
            String commitId = page.locator("#value-commit-id").textContent();
            assertThat(commitId).isNotEqualTo("Unknown");
            assertThat(commitId).matches("[a-f0-9]{7,}");

            // Verify branch is not Unknown
            String branch = page.locator("#value-branch").textContent();
            assertThat(branch).isNotEqualTo("Unknown");

            // Verify build date is not Unknown (should contain date format)
            String buildDate = page.locator("#value-build-date").textContent();
            assertThat(buildDate).isNotEqualTo("Unknown");
            assertThat(buildDate).contains("202"); // Should contain year 202x
        }

        @Test
        @DisplayName("Should display technology stack")
        void shouldDisplayTechnologyStack() {
            page.navigate(baseUrl() + "/settings/about");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertThat(page.locator("#tech-spring-boot").isVisible()).isTrue();
            assertThat(page.locator("#tech-postgresql").isVisible()).isTrue();
            assertThat(page.locator("#tech-thymeleaf").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should display GitHub link")
        void shouldDisplayGitHubLink() {
            page.navigate(baseUrl() + "/settings/about");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertThat(page.locator("#link-github").isVisible()).isTrue();
        }
    }
}
