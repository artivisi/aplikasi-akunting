package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.CompanyBankAccount;
import com.artivisi.accountingfinance.entity.CompanyConfig;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.CompanyBankAccountRepository;
import com.artivisi.accountingfinance.repository.CompanyConfigRepository;
import com.artivisi.accountingfinance.service.CompanyBankAccountService;
import com.artivisi.accountingfinance.service.CompanyConfigService;
import com.artivisi.accountingfinance.service.SecurityAuditService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Functional tests for Settings with data persistence verification.
 * Tests company settings, bank accounts, and audit log functionality.
 */
@DisplayName("Settings - Persistence Tests")
@Import(ServiceTestDataInitializer.class)
class SettingsPersistenceTest extends PlaywrightTestBase {

    @Autowired
    private CompanyConfigService companyConfigService;

    @Autowired
    private CompanyConfigRepository companyConfigRepository;

    @Autowired
    private CompanyBankAccountService bankAccountService;

    @Autowired
    private CompanyBankAccountRepository bankAccountRepository;

    @Autowired
    private SecurityAuditService securityAuditService;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== COMPANY CONFIG SERVICE TESTS ====================

    @Test
    @DisplayName("Should get company config")
    void shouldGetCompanyConfig() {
        CompanyConfig config = companyConfigService.getConfig();

        assertThat(config).isNotNull();
        assertThat(config.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should update company config and verify persistence")
    void shouldUpdateCompanyConfigAndVerifyPersistence() {
        CompanyConfig config = companyConfigService.getConfig();
        String originalName = config.getCompanyName();
        String newName = "Test Company " + System.currentTimeMillis();

        // Update config
        config.setCompanyName(newName);
        companyConfigService.update(config.getId(), config);

        // Verify persistence
        CompanyConfig updated = companyConfigService.getConfig();
        assertThat(updated.getCompanyName()).isEqualTo(newName);

        // Restore original name
        config.setCompanyName(originalName);
        companyConfigService.update(config.getId(), config);
    }

    @Test
    @DisplayName("Should verify company config has required fields")
    void shouldVerifyCompanyConfigHasRequiredFields() {
        CompanyConfig config = companyConfigService.getConfig();

        assertThat(config).isNotNull();
        assertThat(config.getId()).isNotNull();
        // Company name should be set
        assertThat(config.getCompanyName()).isNotNull();
    }

    // ==================== BANK ACCOUNT SERVICE TESTS ====================

    @Test
    @DisplayName("Should create bank account and verify persistence")
    void shouldCreateBankAccountAndVerifyPersistence() {
        String uniqueNumber = "987654" + System.currentTimeMillis() % 10000;

        CompanyBankAccount account = new CompanyBankAccount();
        account.setBankName("Test Bank Persistence");
        account.setAccountNumber(uniqueNumber);
        account.setAccountName("PT Test Company Persistence");
        account.setBankBranch("001");
        account.setActive(true);

        CompanyBankAccount saved = bankAccountService.create(account);

        // Verify persistence
        assertThat(saved.getId()).isNotNull();

        var fromDb = bankAccountRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getBankName()).isEqualTo("Test Bank Persistence");
        assertThat(fromDb.get().getAccountNumber()).isEqualTo(uniqueNumber);

        // Cleanup
        bankAccountService.delete(saved.getId());
    }

    @Test
    @DisplayName("Should update bank account and verify persistence")
    void shouldUpdateBankAccountAndVerifyPersistence() {
        // Create test account
        String uniqueNumber = "123456" + System.currentTimeMillis() % 10000;

        CompanyBankAccount account = new CompanyBankAccount();
        account.setBankName("Original Bank Name");
        account.setAccountNumber(uniqueNumber);
        account.setAccountName("Original Account Name");
        account.setActive(true);

        CompanyBankAccount saved = bankAccountService.create(account);

        // Update account
        CompanyBankAccount updateData = new CompanyBankAccount();
        updateData.setBankName("Updated Bank Name");
        updateData.setAccountNumber(uniqueNumber);
        updateData.setAccountName("Updated Account Name");
        updateData.setActive(true);

        CompanyBankAccount updated = bankAccountService.update(saved.getId(), updateData);

        // Verify persistence
        assertThat(updated.getBankName()).isEqualTo("Updated Bank Name");

        var fromDb = bankAccountRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getBankName()).isEqualTo("Updated Bank Name");

        // Cleanup
        bankAccountService.delete(saved.getId());
    }

    @Test
    @DisplayName("Should delete bank account and verify removal")
    void shouldDeleteBankAccountAndVerifyRemoval() {
        // Create test account
        String uniqueNumber = "555666" + System.currentTimeMillis() % 10000;

        CompanyBankAccount account = new CompanyBankAccount();
        account.setBankName("Bank To Delete");
        account.setAccountNumber(uniqueNumber);
        account.setAccountName("Delete Test Account");
        account.setActive(true);

        CompanyBankAccount saved = bankAccountService.create(account);
        java.util.UUID savedId = saved.getId();

        // Delete account
        bankAccountService.delete(savedId);

        // Verify removal
        var fromDb = bankAccountRepository.findById(savedId);
        assertThat(fromDb).isEmpty();
    }

    @Test
    @DisplayName("Should set bank account as default and verify")
    void shouldSetBankAccountAsDefaultAndVerify() {
        // Create test account
        String uniqueNumber = "777888" + System.currentTimeMillis() % 10000;

        CompanyBankAccount account = new CompanyBankAccount();
        account.setBankName("Default Test Bank");
        account.setAccountNumber(uniqueNumber);
        account.setAccountName("Default Test Account");
        account.setActive(true);
        account.setIsDefault(false);

        CompanyBankAccount saved = bankAccountService.create(account);

        // Set as default
        bankAccountService.setAsDefault(saved.getId());

        // Verify
        var fromDb = bankAccountRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().isDefaultAccount()).isTrue();

        // Cleanup
        bankAccountService.delete(saved.getId());
    }

    @Test
    @DisplayName("Should deactivate bank account and verify")
    void shouldDeactivateBankAccountAndVerify() {
        // Create active account
        String uniqueNumber = "999111" + System.currentTimeMillis() % 10000;

        CompanyBankAccount account = new CompanyBankAccount();
        account.setBankName("Deactivate Test Bank");
        account.setAccountNumber(uniqueNumber);
        account.setAccountName("Deactivate Test Account");
        account.setActive(true);

        CompanyBankAccount saved = bankAccountService.create(account);

        // Deactivate
        bankAccountService.deactivate(saved.getId());

        // Verify
        var fromDb = bankAccountRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().isActive()).isFalse();

        // Cleanup
        bankAccountService.delete(saved.getId());
    }

    @Test
    @DisplayName("Should activate bank account and verify")
    void shouldActivateBankAccountAndVerify() {
        // Create inactive account
        String uniqueNumber = "222333" + System.currentTimeMillis() % 10000;

        CompanyBankAccount account = new CompanyBankAccount();
        account.setBankName("Activate Test Bank");
        account.setAccountNumber(uniqueNumber);
        account.setAccountName("Activate Test Account");
        account.setActive(false);

        CompanyBankAccount saved = bankAccountService.create(account);

        // Activate
        bankAccountService.activate(saved.getId());

        // Verify
        var fromDb = bankAccountRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().isActive()).isTrue();

        // Cleanup
        bankAccountService.delete(saved.getId());
    }

    @Test
    @DisplayName("Should reject duplicate account number")
    void shouldRejectDuplicateAccountNumber() {
        String uniqueNumber = "444555" + System.currentTimeMillis() % 10000;

        // Create first account
        CompanyBankAccount account1 = new CompanyBankAccount();
        account1.setBankName("First Bank");
        account1.setAccountNumber(uniqueNumber);
        account1.setAccountName("First Account");
        account1.setActive(true);

        CompanyBankAccount saved = bankAccountService.create(account1);

        // Try to create second with same number
        CompanyBankAccount account2 = new CompanyBankAccount();
        account2.setBankName("Second Bank");
        account2.setAccountNumber(uniqueNumber);
        account2.setAccountName("Second Account");
        account2.setActive(true);

        assertThatThrownBy(() -> bankAccountService.create(account2))
            .isInstanceOf(IllegalArgumentException.class);

        // Cleanup
        bankAccountService.delete(saved.getId());
    }

    @Test
    @DisplayName("Should find all bank accounts")
    void shouldFindAllBankAccounts() {
        var accounts = bankAccountService.findAll();
        assertThat(accounts).isNotNull();
    }

    // ==================== AUDIT LOG TESTS ====================

    @Test
    @DisplayName("Should search audit logs")
    void shouldSearchAuditLogs() {
        var results = securityAuditService.search(
            null, null, null, null, PageRequest.of(0, 10));

        assertThat(results).isNotNull();
    }

    @Test
    @DisplayName("Should search audit logs by event type")
    void shouldSearchAuditLogsByEventType() {
        var results = securityAuditService.search(
            com.artivisi.accountingfinance.enums.AuditEventType.LOGIN_SUCCESS,
            null, null, null, PageRequest.of(0, 10));

        assertThat(results).isNotNull();
    }

    @Test
    @DisplayName("Should search audit logs by username")
    void shouldSearchAuditLogsByUsername() {
        var results = securityAuditService.search(
            null, "admin", null, null, PageRequest.of(0, 10));

        assertThat(results).isNotNull();
    }

    // ==================== UI TESTS ====================

    @Test
    @DisplayName("Should update company settings via UI and verify in database")
    void shouldUpdateCompanySettingsViaUiAndVerifyInDatabase() {
        navigateTo("/settings");
        waitForPageLoad();

        // Get current config
        CompanyConfig originalConfig = companyConfigService.getConfig();
        String originalName = originalConfig.getCompanyName();

        // Update company name via UI
        var companyNameInput = page.locator("#companyName");
        assertThat(companyNameInput).isVisible();

        String newName = "UI Updated Company " + System.currentTimeMillis() % 1000;
        companyNameInput.fill(newName);

        page.locator("#btn-save-company").click();
        waitForPageLoad();

        // Verify in database
        CompanyConfig updated = companyConfigService.getConfig();
        assertThat(updated.getCompanyName()).isEqualTo(newName);

        // Restore original
        companyNameInput = page.locator("#companyName");
        companyNameInput.fill(originalName);
        page.locator("#btn-save-company").click();
        waitForPageLoad();
    }

    @Test
    @DisplayName("Should create bank account via UI and verify in database")
    void shouldCreateBankAccountViaUiAndVerifyInDatabase() {
        navigateTo("/settings/bank-accounts/new");
        waitForPageLoad();

        String uniqueNumber = "UI" + System.currentTimeMillis() % 100000000;

        // Fill form
        page.locator("#bankName").fill("UI Created Bank");
        page.locator("#accountNumber").fill(uniqueNumber);
        page.locator("#accountName").fill("UI Created Account");

        page.locator("#btn-save-bank").click();
        waitForPageLoad();

        // Verify in database
        var fromDb = bankAccountRepository.findByAccountNumber(uniqueNumber);
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getBankName()).isEqualTo("UI Created Bank");

        // Cleanup
        bankAccountService.delete(fromDb.get().getId());
    }

    @Test
    @DisplayName("Should display audit logs page with data")
    void shouldDisplayAuditLogsPageWithData() {
        navigateTo("/settings/audit-logs");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
        assertThat(page.locator("#audit-log-table")).isVisible();
    }

    @Test
    @DisplayName("Should filter audit logs via UI")
    void shouldFilterAuditLogsViaUi() {
        navigateTo("/settings/audit-logs?eventType=LOGIN_SUCCESS");
        waitForPageLoad();

        // Verify filter is applied
        assertThat(page.locator("#event-type-filter")).hasValue("LOGIN_SUCCESS");
    }
}
