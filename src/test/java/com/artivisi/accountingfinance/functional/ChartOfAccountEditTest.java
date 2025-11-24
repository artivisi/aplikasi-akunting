package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.AccountFormPage;
import com.artivisi.accountingfinance.functional.page.ChartOfAccountsPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Chart of Accounts - Edit Account")
class ChartOfAccountEditTest extends PlaywrightTestBase {

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
    @DisplayName("Should navigate to edit form via edit button")
    void shouldNavigateToEditFormViaEditButton() {
        accountsPage.navigate();

        // Click edit on a leaf account (Kas - 1.1.01)
        accountsPage.clickExpandAccount("1");
        accountsPage.assertAccountRowVisible("1.1");
        accountsPage.clickExpandAccount("1.1");
        accountsPage.assertAccountRowVisible("1.1.01");
        accountsPage.clickEditAccount("1.1.01");

        formPage.assertPageTitleText("Edit Akun");
        formPage.assertAccountCodeInputVisible();
        formPage.assertAccountNameInputVisible();
        formPage.assertAccountTypeSelectVisible();
        formPage.assertSaveButtonText("Simpan Perubahan");
    }

    @Test
    @DisplayName("Should display existing account data in form")
    void shouldDisplayExistingAccountDataInForm() {
        accountsPage.navigate();

        // Click edit on Kas account
        accountsPage.clickExpandAccount("1");
        accountsPage.assertAccountRowVisible("1.1");
        accountsPage.clickExpandAccount("1.1");
        accountsPage.assertAccountRowVisible("1.1.01");
        accountsPage.clickEditAccount("1.1.01");

        formPage.assertAccountCodeValue("1.1.01");
        formPage.assertAccountNameValue("Kas");
        formPage.assertAccountTypeSelected("ASSET");
        formPage.assertNormalBalanceDebitSelected();
    }

    @Test
    @DisplayName("Should update account and show in list")
    void shouldUpdateAccountAndShowInList() {
        // First create a test account to edit
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.50");
        formPage.fillAccountName("Edit Test Original");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();
        accountsPage.assertSuccessMessageVisible();

        // Navigate to list and edit the account
        accountsPage.navigate();
        accountsPage.assertAccountRowVisible("9.9.50");
        accountsPage.clickEditAccount("9.9.50");

        // Update the account
        formPage.clearAndFillAccountName("Edit Test Updated");
        formPage.clickSave();

        // Verify update
        accountsPage.assertSuccessMessageVisible();
        accountsPage.assertSuccessMessageText("Akun berhasil diperbarui");
        accountsPage.assertAccountRowVisible("9.9.50");
        accountsPage.assertAccountNameVisible("9.9.50", "Edit Test Updated");
    }

    @Test
    @DisplayName("Should display success message after update")
    void shouldDisplaySuccessMessageAfterUpdate() {
        // Create test account
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.51");
        formPage.fillAccountName("Success Message Test");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();
        accountsPage.assertSuccessMessageVisible();

        // Edit the account
        accountsPage.navigate();
        accountsPage.clickEditAccount("9.9.51");
        formPage.clearAndFillAccountName("Success Message Updated");
        formPage.clickSave();

        accountsPage.assertSuccessMessageVisible();
        accountsPage.assertSuccessMessageText("Akun berhasil diperbarui");
    }

    @Test
    @DisplayName("Should display validation errors on invalid input")
    void shouldDisplayValidationErrorsOnInvalidInput() {
        // Create test account
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.52");
        formPage.fillAccountName("Validation Test");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();
        accountsPage.assertSuccessMessageVisible();

        // Edit and clear required field
        accountsPage.navigate();
        accountsPage.clickEditAccount("9.9.52");
        formPage.clearAccountName();
        formPage.clickSave();

        formPage.assertValidationErrorVisible();
        formPage.assertAccountNameErrorVisible("Nama akun harus diisi");
    }

    @Test
    @DisplayName("Should show error when changing account code to existing code")
    void shouldShowErrorWhenChangingToExistingCode() {
        // Create test account
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.53");
        formPage.fillAccountName("Duplicate Code Test");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();
        accountsPage.assertSuccessMessageVisible();

        // Edit and change to existing code
        accountsPage.navigate();
        accountsPage.clickEditAccount("9.9.53");
        formPage.clearAndFillAccountCode("1");  // Use existing seed data code
        formPage.clickSave();

        formPage.assertDuplicateCodeErrorVisible();
    }

    @Test
    @DisplayName("Should not allow account type change if account has children")
    void shouldNotAllowAccountTypeChangeIfHasChildren() {
        accountsPage.navigate();

        // Edit ASET account which has children (1.1 Aset Lancar, etc)
        accountsPage.clickEditAccount("1");

        // Account type select should be disabled for parent accounts
        formPage.assertAccountTypeSelectDisabled();
    }

    @Test
    @DisplayName("Should allow account type change if account has no children")
    void shouldAllowAccountTypeChangeIfNoChildren() {
        // Create a leaf account
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.54");
        formPage.fillAccountName("Leaf Account");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();
        accountsPage.assertSuccessMessageVisible();

        // Edit the leaf account
        accountsPage.navigate();
        accountsPage.clickEditAccount("9.9.54");

        // Account type select should be enabled
        formPage.assertAccountTypeSelectEnabled();
    }
}
