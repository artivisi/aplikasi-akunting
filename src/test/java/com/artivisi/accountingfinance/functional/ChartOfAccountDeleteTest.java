package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.AccountFormPage;
import com.artivisi.accountingfinance.functional.page.ChartOfAccountsPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Chart of Accounts - Delete Account (Soft Delete)")
class ChartOfAccountDeleteTest extends PlaywrightTestBase {

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
    @DisplayName("Should show delete button on leaf accounts only")
    void shouldShowDeleteButtonOnLeafAccountsOnly() {
        accountsPage.navigate();

        // Root account "1" (ASET) has children - should NOT have delete button
        accountsPage.assertDeleteButtonNotVisible("1");

        // Expand to see leaf accounts
        accountsPage.clickExpandAccount("1");
        accountsPage.assertAccountRowVisible("1.1");

        // "1.1" (Aset Lancar) has children - should NOT have delete button
        accountsPage.assertDeleteButtonNotVisible("1.1");

        accountsPage.clickExpandAccount("1.1");
        accountsPage.assertAccountRowVisible("1.1.01");

        // "1.1.01" (Kas) is a leaf account - should have delete button
        accountsPage.assertDeleteButtonVisible("1.1.01");
    }

    @Test
    @DisplayName("Should delete account and show success message")
    void shouldDeleteAccountAndShowSuccessMessage() {
        // First create a test account to delete
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.01");
        formPage.fillAccountName("Account To Delete");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertSuccessMessageVisible();
        accountsPage.assertAccountRowVisible("9.9.01");

        // Now delete the account
        accountsPage.clickDeleteAccount("9.9.01");

        // Verify success message
        accountsPage.assertSuccessMessageVisible();
        accountsPage.assertSuccessMessageText("Akun berhasil dihapus");

        // Verify account is no longer visible
        accountsPage.assertAccountRowNotVisible("9.9.01");
    }

    @Test
    @DisplayName("Should show confirmation dialog before delete")
    void shouldShowConfirmationDialogBeforeDelete() {
        // First create a test account
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.02");
        formPage.fillAccountName("Account For Confirm Test");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertAccountRowVisible("9.9.02");

        // Click delete but cancel the confirmation
        accountsPage.clickDeleteAccountAndCancel("9.9.02");

        // Account should still be visible (delete was cancelled)
        accountsPage.assertAccountRowVisible("9.9.02");
    }

    @Test
    @DisplayName("Should not show delete button on accounts with children")
    void shouldNotShowDeleteButtonOnAccountsWithChildren() {
        accountsPage.navigate();

        // All root accounts have children, none should have delete buttons
        accountsPage.assertDeleteButtonNotVisible("1");
        accountsPage.assertDeleteButtonNotVisible("2");
        accountsPage.assertDeleteButtonNotVisible("3");
        accountsPage.assertDeleteButtonNotVisible("4");
        accountsPage.assertDeleteButtonNotVisible("5");

        // Expand ASET to check intermediate accounts
        accountsPage.clickExpandAccount("1");
        accountsPage.assertAccountRowVisible("1.1");
        accountsPage.assertAccountRowVisible("1.2");

        // Aset Lancar (1.1) has children - no delete button
        accountsPage.assertDeleteButtonNotVisible("1.1");

        // Aset Tetap (1.2) has children - no delete button
        accountsPage.assertDeleteButtonNotVisible("1.2");
    }

    @Test
    @DisplayName("Deleted account should not appear after page refresh")
    void deletedAccountShouldNotAppearAfterRefresh() {
        // First create a test account
        formPage.navigateToNew();
        formPage.fillAccountCode("9.9.03");
        formPage.fillAccountName("Account For Refresh Test");
        formPage.selectAccountType("EXPENSE");
        formPage.selectNormalBalanceDebit();
        formPage.clickSave();

        accountsPage.assertAccountRowVisible("9.9.03");

        // Delete the account
        accountsPage.clickDeleteAccount("9.9.03");
        accountsPage.assertSuccessMessageText("Akun berhasil dihapus");

        // Refresh the page
        accountsPage.navigate();

        // Account should still not be visible (soft delete persisted)
        accountsPage.assertAccountRowNotVisible("9.9.03");
    }
}
