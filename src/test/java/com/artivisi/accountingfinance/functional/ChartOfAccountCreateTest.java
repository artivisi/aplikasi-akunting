package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.AccountFormPage;
import com.artivisi.accountingfinance.functional.page.ChartOfAccountsPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Chart of Accounts - Create Account")
class ChartOfAccountCreateTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private ChartOfAccountsPage accountsPage;
    private AccountFormPage formPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        accountsPage = new ChartOfAccountsPage(page, baseUrl());
        formPage = new AccountFormPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should navigate to create form via Tambah Akun button")
    void shouldNavigateToCreateFormViaAddButton() {
        accountsPage.navigate();
        accountsPage.clickAddAccount();

        formPage.assertPageTitleText("Tambah Akun");
        formPage.assertAccountCodeInputVisible();
        formPage.assertAccountNameInputVisible();
        formPage.assertAccountTypeSelectVisible();
        formPage.assertSaveButtonVisible();
    }

    @Test
    @DisplayName("Should display empty form with all fields")
    void shouldDisplayEmptyFormWithAllFields() {
        formPage.navigateToNew();

        formPage.assertPageTitleText("Tambah Akun");
        formPage.assertAccountCodeInputVisible();
        formPage.assertAccountCodeInputEmpty();
        formPage.assertAccountNameInputVisible();
        formPage.assertAccountNameInputEmpty();
        formPage.assertAccountTypeSelectVisible();
        formPage.assertNormalBalanceRadiosVisible();
        formPage.assertIsHeaderCheckboxVisible();
        formPage.assertPermanentCheckboxVisible();
        formPage.assertDescriptionTextareaVisible();
        formPage.assertParentSelectVisible();
        formPage.assertSaveButtonVisible();
    }

    @Test
    @DisplayName("Should create new account and show in list")
    void shouldCreateNewAccountAndShowInList() {
        formPage.navigateToNew();

        formPage.fillAccountCode("9.9.99");
        formPage.fillAccountName("Test Account");
        formPage.selectAccountType("ASSET");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertSuccessMessageVisible();
        accountsPage.assertSuccessMessageText("Akun berhasil ditambahkan");
        accountsPage.assertAccountRowVisible("9.9.99");
        accountsPage.assertAccountNameVisible("9.9.99", "Test Account");
    }

    @Test
    @DisplayName("Should display success message after creation")
    void shouldDisplaySuccessMessageAfterCreation() {
        formPage.navigateToNew();

        formPage.fillAccountCode("9.9.98");
        formPage.fillAccountName("Success Message Test");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertSuccessMessageVisible();
        accountsPage.assertSuccessMessageText("Akun berhasil ditambahkan");
    }

    @Test
    @DisplayName("Should show validation error when account code is empty")
    void shouldShowValidationErrorWhenAccountCodeEmpty() {
        formPage.navigateToNew();

        formPage.fillAccountName("Test Account");
        formPage.selectAccountType("ASSET");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        formPage.assertValidationErrorVisible();
        formPage.assertAccountCodeErrorVisible("Kode akun harus diisi");
    }

    @Test
    @DisplayName("Should show validation error when account name is empty")
    void shouldShowValidationErrorWhenAccountNameEmpty() {
        formPage.navigateToNew();

        formPage.fillAccountCode("9.9.97");
        formPage.selectAccountType("ASSET");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        formPage.assertValidationErrorVisible();
        formPage.assertAccountNameErrorVisible("Nama akun harus diisi");
    }

    @Test
    @DisplayName("Should show validation error when account type is not selected")
    void shouldShowValidationErrorWhenAccountTypeNotSelected() {
        formPage.navigateToNew();

        formPage.fillAccountCode("9.9.96");
        formPage.fillAccountName("Test Account");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        formPage.assertValidationErrorVisible();
        formPage.assertAccountTypeErrorVisible("Tipe akun harus dipilih");
    }

    @Test
    @DisplayName("Should show validation error when normal balance is not selected")
    void shouldShowValidationErrorWhenNormalBalanceNotSelected() {
        formPage.navigateToNew();

        formPage.fillAccountCode("9.9.95");
        formPage.fillAccountName("Test Account");
        formPage.selectAccountType("ASSET");
        formPage.clickSave();

        formPage.assertValidationErrorVisible();
        formPage.assertNormalBalanceErrorVisible("Saldo normal harus dipilih");
    }

    @Test
    @DisplayName("Should show validation error when account code already exists")
    void shouldShowValidationErrorWhenAccountCodeExists() {
        formPage.navigateToNew();

        // Use existing account code from seed data
        formPage.fillAccountCode("1");
        formPage.fillAccountName("Duplicate Code Test");
        formPage.selectAccountType("ASSET");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        formPage.assertDuplicateCodeErrorVisible();
    }

    @Test
    @DisplayName("Should create child account under parent")
    void shouldCreateChildAccountUnderParent() {
        formPage.navigateToNew();

        formPage.fillAccountCode("1.1.99");
        formPage.fillAccountName("Test Child Account");
        formPage.selectParentAccount("1.1 - Aset Lancar");
        formPage.selectAccountType("ASSET");  // Required for validation, will be overridden by parent
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertSuccessMessageVisible();

        // Re-navigate to ensure page is fully loaded with Alpine.js initialized
        accountsPage.navigate();

        // Expand to see the child account
        accountsPage.clickExpandAccount("1");
        accountsPage.assertAccountRowVisible("1.1");  // Wait for child to appear before clicking
        accountsPage.clickExpandAccount("1.1");
        accountsPage.assertAccountRowVisible("1.1.99");
        accountsPage.assertAccountNameVisible("1.1.99", "Test Child Account");
    }

    @Test
    @DisplayName("Should inherit account type from parent when creating child")
    void shouldInheritAccountTypeFromParent() {
        formPage.navigateToNew();

        formPage.fillAccountCode("1.1.88");
        formPage.fillAccountName("Inherited Type Test");
        formPage.selectParentAccount("1.1 - Aset Lancar");
        // Intentionally select wrong type to prove inheritance works
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertSuccessMessageVisible();

        // Re-navigate to ensure page is fully loaded with Alpine.js initialized
        accountsPage.navigate();

        // Expand and verify account type is inherited from parent (ASSET), not the selected EXPENSE
        accountsPage.clickExpandAccount("1");
        accountsPage.assertAccountRowVisible("1.1");  // Wait for child to appear before clicking
        accountsPage.clickExpandAccount("1.1");
        accountsPage.assertAccountRowVisible("1.1.88");
        accountsPage.assertAccountTypeVisible("1.1.88", "Aset");
    }
}
