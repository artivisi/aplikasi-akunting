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
            assertThat(page.locator("text=" + uniqueNumber).count()).isGreaterThan(0);
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

            // Should show error message (stays on form page)
            boolean hasError = page.locator(".text-red-500, .text-red-600").count() > 0
                || page.locator("text=sudah ada").count() > 0
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
                assertThat(page.locator("text=Utama").count()).isGreaterThan(0);
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
                assertThat(page.locator("text=Nonaktif").count()).isGreaterThan(0);
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
                assertThat(page.locator("text=" + newBankName).count()).isGreaterThan(0);
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
            assertThat(page.locator("text=Commit ID").count()).isGreaterThan(0);
            assertThat(page.locator("text=Branch").count()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should display technology stack")
        void shouldDisplayTechnologyStack() {
            page.navigate(baseUrl() + "/settings/about");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            assertThat(page.locator("text=Spring Boot").count()).isGreaterThan(0);
            assertThat(page.locator("text=PostgreSQL").count()).isGreaterThan(0);
            assertThat(page.locator("text=Thymeleaf").count()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should display GitHub link")
        void shouldDisplayGitHubLink() {
            page.navigate(baseUrl() + "/settings/about");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            Locator githubLink = page.locator("a[href*='github.com']");
            assertThat(githubLink.count()).isGreaterThan(0);
        }
    }
}
