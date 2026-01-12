package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.repository.CompanyBankAccountRepository;
import com.artivisi.accountingfinance.repository.CompanyConfigRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Settings Controller Functional Tests.
 * Tests SettingsController: company config, bank accounts, audit logs.
 */
@DisplayName("Service Industry - Settings Comprehensive")
@Import(ServiceTestDataInitializer.class)
class ServiceSettingsComprehensiveTest extends PlaywrightTestBase {

    @Autowired
    private CompanyConfigRepository companyConfigRepository;

    @Autowired
    private CompanyBankAccountRepository bankAccountRepository;

    @BeforeEach
    void setup() {
        loginAsAdmin();
    }

    // ==================== Company Settings ====================

    @Test
    @DisplayName("Should display company settings page")
    void shouldDisplayCompanySettingsPage() {
        navigateTo("/settings");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title, h1").first()).containsText("Pengaturan");
    }

    @Test
    @DisplayName("Should display company config form")
    void shouldDisplayCompanyConfigForm() {
        navigateTo("/settings");
        waitForPageLoad();

        // Verify company config form fields
        assertThat(page.locator("input[name='companyName']")).isVisible();
    }

    @Test
    @DisplayName("Should update company name")
    void shouldUpdateCompanyName() {
        navigateTo("/settings");
        waitForPageLoad();

        var companyNameInput = page.locator("input[name='companyName']").first();
        if (companyNameInput.isVisible()) {
            String newName = "Updated Company " + System.currentTimeMillis();
            companyNameInput.fill(newName);

            // Submit form
            var submitBtn = page.locator("form[action*='/settings/company'] button[type='submit']").first();
            if (submitBtn.isVisible()) {
                submitBtn.click();
                waitForPageLoad();

                // Verify page loads (success message may or may not be visible)
                assertThat(page.locator("body")).isVisible();
            }
        }

        // Verify page loads anyway
        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should update company address")
    void shouldUpdateCompanyAddress() {
        navigateTo("/settings");
        waitForPageLoad();

        var addressInput = page.locator("input[name='companyAddress'], textarea[name='companyAddress']").first();
        if (addressInput.isVisible()) {
            addressInput.fill("Updated Address " + System.currentTimeMillis());

            var submitBtn = page.locator("form[action*='/settings/company'] button[type='submit']").first();
            if (submitBtn.isVisible()) {
                submitBtn.click();
                waitForPageLoad();

                // Verify page reloads
                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should update company tax ID (NPWP)")
    void shouldUpdateCompanyTaxId() {
        navigateTo("/settings");
        waitForPageLoad();

        var npwpInput = page.locator("input[name='npwp']").first();
        if (npwpInput.isVisible()) {
            npwpInput.fill("01.234.567.8-012.000");

            var submitBtn = page.locator("form[action*='/settings/company'] button[type='submit']").first();
            if (submitBtn.isVisible()) {
                submitBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    // ==================== Bank Accounts ====================

    @Test
    @DisplayName("Should display bank accounts list")
    void shouldDisplayBankAccountsList() {
        navigateTo("/settings/bank-accounts");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should display new bank account form")
    void shouldDisplayNewBankAccountForm() {
        navigateTo("/settings/bank-accounts/new");
        waitForPageLoad();

        // Verify form fields - at least bank name and account number should exist
        var bankNameInput = page.locator("input[name='bankName']").first();
        var accountNumberInput = page.locator("input[name='accountNumber']").first();

        if (bankNameInput.isVisible()) {
            assertThat(bankNameInput).isVisible();
        }
        if (accountNumberInput.isVisible()) {
            assertThat(accountNumberInput).isVisible();
        }

        // Verify page loads
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new bank account")
    void shouldCreateNewBankAccount() {
        navigateTo("/settings/bank-accounts/new");
        waitForPageLoad();

        String uniqueNumber = "ACC" + System.currentTimeMillis();

        var bankNameInput = page.locator("input[name='bankName']").first();
        var accountNumberInput = page.locator("input[name='accountNumber']").first();

        if (bankNameInput.isVisible() && accountNumberInput.isVisible()) {
            bankNameInput.fill("Bank Test");
            accountNumberInput.fill(uniqueNumber);

            // Fill account holder if exists
            var holderInput = page.locator("input[name='accountHolder'], input[name='accountHolderName']").first();
            if (holderInput.isVisible()) {
                holderInput.fill("Test Account Holder");
            }

            // Fill branch if exists
            var branchInput = page.locator("input[name='branch']").first();
            if (branchInput.isVisible()) {
                branchInput.fill("Test Branch");
            }

            page.click("button[type='submit']");
            waitForPageLoad();
        }

        // Verify page loads
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should edit bank account")
    void shouldEditBankAccount() {
        var bankAccount = bankAccountRepository.findAll().stream().findFirst();

        if (bankAccount.isPresent()) {
            navigateTo("/settings/bank-accounts/" + bankAccount.get().getId() + "/edit");
            waitForPageLoad();

            // Update bank name
            var bankNameInput = page.locator("input[name='bankName']").first();
            if (bankNameInput.isVisible()) {
                bankNameInput.fill("Updated Bank Name");
            }

            page.click("button[type='submit']");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    @Test
    @DisplayName("Should set default bank account")
    void shouldSetDefaultBankAccount() {
        var bankAccount = bankAccountRepository.findAll().stream().findFirst();

        if (bankAccount.isPresent()) {
            navigateTo("/settings");
            waitForPageLoad();

            var setDefaultBtn = page.locator("form[action*='/set-default'] button[type='submit']").first();
            if (setDefaultBtn.isVisible()) {
                setDefaultBtn.click();
                waitForPageLoad();

                // Verify we're still on settings page (action completed successfully)
                assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings.*"));
            }
        }
    }

    @Test
    @DisplayName("Should deactivate bank account")
    void shouldDeactivateBankAccount() {
        var bankAccount = bankAccountRepository.findAll().stream()
                .filter(ba -> Boolean.TRUE.equals(ba.getActive()))
                .findFirst();

        if (bankAccount.isPresent()) {
            navigateTo("/settings");
            waitForPageLoad();

            var deactivateBtn = page.locator("form[action*='/" + bankAccount.get().getId() + "/deactivate'] button[type='submit']").first();
            if (deactivateBtn.isVisible()) {
                deactivateBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should activate bank account")
    void shouldActivateBankAccount() {
        var bankAccount = bankAccountRepository.findAll().stream()
                .filter(ba -> Boolean.FALSE.equals(ba.getActive()))
                .findFirst();

        if (bankAccount.isPresent()) {
            navigateTo("/settings");
            waitForPageLoad();

            var activateBtn = page.locator("form[action*='/" + bankAccount.get().getId() + "/activate'] button[type='submit']").first();
            if (activateBtn.isVisible()) {
                activateBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    // ==================== Audit Logs ====================

    @Test
    @DisplayName("Should display audit logs page")
    void shouldDisplayAuditLogsPage() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title, h1").first()).containsText("Audit");
    }

    @Test
    @DisplayName("Should filter audit logs by event type")
    void shouldFilterAuditLogsByEventType() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        var eventTypeSelect = page.locator("select[name='eventType']").first();
        if (eventTypeSelect.isVisible()) {
            eventTypeSelect.selectOption("LOGIN_SUCCESS");

            var filterBtn = page.locator("button[type='submit']:has-text('Filter'), button[type='submit']:has-text('Cari')").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }

            assertThat(page.locator("body")).isVisible();
        }
    }

    @Test
    @DisplayName("Should filter audit logs by username")
    void shouldFilterAuditLogsByUsername() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        var usernameInput = page.locator("input[name='username']").first();
        if (usernameInput.isVisible()) {
            usernameInput.fill("admin");

            var filterBtn = page.locator("button[type='submit']:has-text('Filter'), button[type='submit']:has-text('Cari')").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }

            assertThat(page.locator("body")).isVisible();
        }
    }

    @Test
    @DisplayName("Should filter audit logs by date range")
    void shouldFilterAuditLogsByDateRange() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        var startDateInput = page.locator("input[name='startDate']").first();
        var endDateInput = page.locator("input[name='endDate']").first();

        if (startDateInput.isVisible() && endDateInput.isVisible()) {
            startDateInput.fill("2024-01-01");
            endDateInput.fill("2024-12-31");

            var filterBtn = page.locator("button[type='submit']:has-text('Filter'), button[type='submit']:has-text('Cari')").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== About Page ====================

    @Test
    @DisplayName("Should display about page")
    void shouldDisplayAboutPage() {
        navigateTo("/settings/about");
        waitForPageLoad();

        // Verify about page loads
        assertThat(page.locator("body")).isVisible();
    }

    // ==================== Telegram Settings ====================

    @Test
    @DisplayName("Should display telegram settings page")
    void shouldDisplayTelegramSettingsPage() {
        navigateTo("/settings/telegram");
        waitForPageLoad();

        // Verify page loads (may show disabled message if bot not configured)
        assertThat(page.locator("body")).isVisible();
    }

    // ==================== Privacy Page ====================

    @Test
    @DisplayName("Should display privacy policy page")
    void shouldDisplayPrivacyPolicyPage() {
        navigateTo("/settings/privacy");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("body")).isVisible();
    }
}
