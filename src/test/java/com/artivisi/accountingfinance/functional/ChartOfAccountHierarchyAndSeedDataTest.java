package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.AccountFormPage;
import com.artivisi.accountingfinance.functional.page.ChartOfAccountsPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Chart of Accounts - Hierarchy, Seed Data & Soft Delete")
class ChartOfAccountHierarchyAndSeedDataTest extends PlaywrightTestBase {

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

    @Nested
    @DisplayName("6. Hierarchical Structure")
    class HierarchicalStructureTests {

        @Test
        @DisplayName("Level calculation correct - root accounts have level 1")
        void rootAccountsShouldHaveLevel1() {
            // Root accounts (1, 2, 3, 4, 5) are at level 1
            // When we edit them, they show as root accounts with no parent
            accountsPage.navigate();
            accountsPage.clickEditAccount("1");

            // ASET is a root account - it should not have a parent selected
            formPage.assertPageTitleText("Edit Akun");
            formPage.assertAccountCodeValue("1");
            formPage.assertAccountNameValue("ASET");
        }

        @Test
        @DisplayName("Level calculation correct - child accounts at level 2")
        void childAccountsShouldHaveLevel2() {
            accountsPage.navigate();

            // Expand ASET to see level 2 accounts
            accountsPage.clickExpandAccount("1");
            accountsPage.assertAccountRowVisible("1.1");
            accountsPage.assertAccountRowVisible("1.2");

            // Level 2 accounts have parent = level 1 account
            accountsPage.clickEditAccount("1.1");
            formPage.assertPageTitleText("Edit Akun");
            formPage.assertAccountCodeValue("1.1");
            formPage.assertAccountNameValue("Aset Lancar");
        }

        @Test
        @DisplayName("Level calculation correct - grandchild accounts at level 3")
        void grandchildAccountsShouldHaveLevel3() {
            accountsPage.navigate();

            // Expand to level 3
            accountsPage.clickExpandAccount("1");
            accountsPage.assertAccountRowVisible("1.1");
            accountsPage.clickExpandAccount("1.1");
            accountsPage.assertAccountRowVisible("1.1.01");

            // Verify level 3 account exists and has correct parent chain
            accountsPage.clickEditAccount("1.1.01");
            formPage.assertPageTitleText("Edit Akun");
            formPage.assertAccountCodeValue("1.1.01");
            formPage.assertAccountNameValue("Kas");
        }

        @Test
        @DisplayName("Creating child account inherits correct level")
        void creatingChildAccountInheritsCorrectLevel() {
            formPage.navigateToNew();

            // Create child under Aset Lancar (level 2)
            formPage.fillAccountCode("1.1.77");
            formPage.fillAccountName("Test Level 3 Account");
            formPage.selectParentAccount("1.1 - Aset Lancar");
            formPage.selectAccountType("ASSET");
            formPage.selectNormalBalanceDebit();
            formPage.clickSave();

            accountsPage.assertSuccessMessageVisible();

            // Navigate and expand to verify the account exists at correct level
            accountsPage.navigate();
            accountsPage.clickExpandAccount("1");
            accountsPage.assertAccountRowVisible("1.1");
            accountsPage.clickExpandAccount("1.1");
            accountsPage.assertAccountRowVisible("1.1.77");
            accountsPage.assertAccountNameVisible("1.1.77", "Test Level 3 Account");
        }

        @Test
        @DisplayName("Account code follows parent pattern - ASET hierarchy")
        void accountCodeFollowsParentPatternAset() {
            accountsPage.navigate();

            // ASET (1) -> Aset Lancar (1.1) -> Kas (1.1.01)
            accountsPage.assertAccountRowVisible("1");
            accountsPage.clickExpandAccount("1");
            accountsPage.assertAccountRowVisible("1.1");
            accountsPage.assertAccountRowVisible("1.2");
            accountsPage.clickExpandAccount("1.1");
            accountsPage.assertAccountRowVisible("1.1.01");
            accountsPage.assertAccountRowVisible("1.1.02");
            accountsPage.assertAccountRowVisible("1.1.03");
        }

        @Test
        @DisplayName("Account code follows parent pattern - LIABILITAS hierarchy")
        void accountCodeFollowsParentPatternLiabilitas() {
            accountsPage.navigate();

            // LIABILITAS (2) -> Liabilitas Jangka Pendek (2.1) -> Hutang Usaha (2.1.01)
            accountsPage.assertAccountRowVisible("2");
            accountsPage.clickExpandAccount("2");
            accountsPage.assertAccountRowVisible("2.1");
            accountsPage.clickExpandAccount("2.1");
            accountsPage.assertAccountRowVisible("2.1.01");
            accountsPage.assertAccountRowVisible("2.1.02");
        }
    }

    @Nested
    @DisplayName("7. Seed Data Verification")
    class SeedDataVerificationTests {

        @Test
        @DisplayName("All root seed accounts have correct types in UI")
        void allRootSeedAccountsHaveCorrectTypesInUI() {
            accountsPage.navigate();

            // Verify type badges display correctly
            accountsPage.assertAccountTypeVisible("1", "Aset");
            accountsPage.assertAccountTypeVisible("2", "Liabilitas");
            accountsPage.assertAccountTypeVisible("3", "Ekuitas");
            accountsPage.assertAccountTypeVisible("4", "Pendapatan");
            accountsPage.assertAccountTypeVisible("5", "Beban");
        }

        @Test
        @DisplayName("ASET account has correct type in edit form")
        void asetAccountHasCorrectTypeInEditForm() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("1");

            formPage.assertAccountTypeSelected("ASSET");
        }

        @Test
        @DisplayName("LIABILITAS account has correct type in edit form")
        void liabilitasAccountHasCorrectTypeInEditForm() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("2");

            formPage.assertAccountTypeSelected("LIABILITY");
        }

        @Test
        @DisplayName("EKUITAS account has correct type in edit form")
        void ekuitasAccountHasCorrectTypeInEditForm() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("3");

            formPage.assertAccountTypeSelected("EQUITY");
        }

        @Test
        @DisplayName("PENDAPATAN account has correct type in edit form")
        void pendapatanAccountHasCorrectTypeInEditForm() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("4");

            formPage.assertAccountTypeSelected("REVENUE");
        }

        @Test
        @DisplayName("BEBAN account has correct type in edit form")
        void bebanAccountHasCorrectTypeInEditForm() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("5");

            formPage.assertAccountTypeSelected("EXPENSE");
        }

        @Test
        @DisplayName("ASET account has correct normal balance (DEBIT)")
        void asetAccountHasCorrectNormalBalance() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("1");

            formPage.assertNormalBalanceDebitSelected();
        }

        @Test
        @DisplayName("LIABILITAS account has correct normal balance (CREDIT)")
        void liabilitasAccountHasCorrectNormalBalance() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("2");

            formPage.assertNormalBalanceCreditSelected();
        }

        @Test
        @DisplayName("EKUITAS account has correct normal balance (CREDIT)")
        void ekuitasAccountHasCorrectNormalBalance() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("3");

            formPage.assertNormalBalanceCreditSelected();
        }

        @Test
        @DisplayName("PENDAPATAN account has correct normal balance (CREDIT)")
        void pendapatanAccountHasCorrectNormalBalance() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("4");

            formPage.assertNormalBalanceCreditSelected();
        }

        @Test
        @DisplayName("BEBAN account has correct normal balance (DEBIT)")
        void bebanAccountHasCorrectNormalBalance() {
            accountsPage.navigate();
            accountsPage.clickEditAccount("5");

            formPage.assertNormalBalanceDebitSelected();
        }

        @Test
        @DisplayName("IT Services - Pendapatan Jasa accounts exist")
        void itServicesPendapatanJasaAccountsExist() {
            accountsPage.navigate();

            // Expand to Pendapatan -> Pendapatan Usaha
            accountsPage.clickExpandAccount("4");
            accountsPage.assertAccountRowVisible("4.1");
            accountsPage.assertAccountNameVisible("4.1", "Pendapatan Usaha");

            accountsPage.clickExpandAccount("4.1");

            // IT Services specific revenue accounts
            accountsPage.assertAccountRowVisible("4.1.01");
            accountsPage.assertAccountNameVisible("4.1.01", "Pendapatan Jasa Konsultasi");
            accountsPage.assertAccountRowVisible("4.1.02");
            accountsPage.assertAccountNameVisible("4.1.02", "Pendapatan Jasa Development");
            accountsPage.assertAccountRowVisible("4.1.03");
            accountsPage.assertAccountNameVisible("4.1.03", "Pendapatan Jasa Training");
        }

        @Test
        @DisplayName("IT Services - Beban Operasional accounts exist")
        void itServicesBebanOperasionalAccountsExist() {
            accountsPage.navigate();

            // Expand to Beban -> Beban Operasional
            accountsPage.clickExpandAccount("5");
            accountsPage.assertAccountRowVisible("5.1");
            accountsPage.assertAccountNameVisible("5.1", "Beban Operasional");

            accountsPage.clickExpandAccount("5.1");

            // IT Services specific expense accounts
            accountsPage.assertAccountRowVisible("5.1.01");
            accountsPage.assertAccountNameVisible("5.1.01", "Beban Gaji");
            accountsPage.assertAccountRowVisible("5.1.02");
            accountsPage.assertAccountNameVisible("5.1.02", "Beban Server & Cloud");
            accountsPage.assertAccountRowVisible("5.1.03");
            accountsPage.assertAccountNameVisible("5.1.03", "Beban Software & Lisensi");
            accountsPage.assertAccountRowVisible("5.1.04");
            accountsPage.assertAccountNameVisible("5.1.04", "Beban Internet & Telekomunikasi");
        }

        @Test
        @DisplayName("IT Services - Piutang Usaha account exists under Aset Lancar")
        void itServicesPiutangUsahaAccountExists() {
            accountsPage.navigate();

            // Expand to Aset -> Aset Lancar
            accountsPage.clickExpandAccount("1");
            accountsPage.clickExpandAccount("1.1");

            // Piutang Usaha for IT Services invoicing
            accountsPage.assertAccountRowVisible("1.1.04");
            accountsPage.assertAccountNameVisible("1.1.04", "Piutang Usaha");
        }

        @Test
        @DisplayName("IT Services - Peralatan Komputer account exists under Aset Tetap")
        void itServicesPeralatanKomputerAccountExists() {
            accountsPage.navigate();

            // Expand to Aset -> Aset Tetap
            accountsPage.clickExpandAccount("1");
            accountsPage.clickExpandAccount("1.2");

            // IT Services fixed assets
            accountsPage.assertAccountRowVisible("1.2.01");
            accountsPage.assertAccountNameVisible("1.2.01", "Peralatan Komputer");
            accountsPage.assertAccountRowVisible("1.2.02");
            accountsPage.assertAccountNameVisible("1.2.02", "Akum. Penyusutan Peralatan");
        }
    }

    @Nested
    @DisplayName("8. Soft Delete - Deleted accounts exclusion")
    class SoftDeleteTests {

        @Test
        @DisplayName("Deleted account not available in parent dropdown")
        void deletedAccountNotAvailableInParentDropdown() {
            // First, create a test account that we can delete
            formPage.navigateToNew();
            formPage.fillAccountCode("9.9.88");
            formPage.fillAccountName("Account To Delete For Dropdown Test");
            formPage.selectAccountType("EXPENSE");
            formPage.selectNormalBalanceDebit();
            formPage.clickSave();

            accountsPage.assertSuccessMessageVisible();

            // Verify the account exists in the list
            accountsPage.navigate();
            accountsPage.assertAccountRowVisible("9.9.88");

            // Verify account appears in parent dropdown
            formPage.navigateToNew();
            formPage.assertParentAccountOptionExists("9.9.88 - Account To Delete For Dropdown Test");

            // Go back and delete the account
            accountsPage.navigate();
            accountsPage.clickDeleteAccount("9.9.88");

            accountsPage.assertSuccessMessageVisible();

            // Verify account no longer in list
            accountsPage.assertAccountRowNotVisible("9.9.88");

            // Navigate to new form and verify deleted account is NOT in parent dropdown
            formPage.navigateToNew();
            formPage.assertParentAccountOptionNotExists("9.9.88");
        }

        @Test
        @DisplayName("Deleted account not visible in accounts list")
        void deletedAccountNotVisibleInAccountsList() {
            // Create a test account
            formPage.navigateToNew();
            formPage.fillAccountCode("9.9.87");
            formPage.fillAccountName("Account To Delete For List Test");
            formPage.selectAccountType("EXPENSE");
            formPage.selectNormalBalanceDebit();
            formPage.clickSave();

            accountsPage.assertSuccessMessageVisible();

            // Verify account exists
            accountsPage.navigate();
            accountsPage.assertAccountRowVisible("9.9.87");

            // Delete the account
            accountsPage.clickDeleteAccount("9.9.87");
            accountsPage.assertSuccessMessageVisible();

            // Verify account is no longer visible (soft deleted)
            accountsPage.assertAccountRowNotVisible("9.9.87");

            // Refresh and verify still not visible
            accountsPage.navigate();
            accountsPage.assertAccountRowNotVisible("9.9.87");
        }
    }
}
