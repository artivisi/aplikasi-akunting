package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TemplateDetailPage;
import com.artivisi.accountingfinance.functional.page.TemplateExecutePage;
import com.artivisi.accountingfinance.functional.page.TemplateFormPage;
import com.artivisi.accountingfinance.functional.page.TemplateListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Journal Templates (Section 1.4)")
class JournalTemplateTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TemplateListPage templateListPage;
    private TemplateDetailPage templateDetailPage;
    private TemplateExecutePage templateExecutePage;
    private TemplateFormPage templateFormPage;

    // Template ID from V003 seed data
    private static final String INCOME_CONSULTING_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000001";

    // Test template ID from V902 test migration (non-system, editable)
    private static final String TEST_EDITABLE_TEMPLATE_ID = "f0000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        templateListPage = new TemplateListPage(page, baseUrl());
        templateDetailPage = new TemplateDetailPage(page, baseUrl());
        templateExecutePage = new TemplateExecutePage(page, baseUrl());
        templateFormPage = new TemplateFormPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("1.4.1 Template List")
    class TemplateListTests {

        @Test
        @DisplayName("Should display template list page")
        void shouldDisplayTemplateListPage() {
            templateListPage.navigate();

            templateListPage.assertPageTitleVisible();
        }

        @Test
        @DisplayName("Should display seeded templates")
        void shouldDisplaySeededTemplates() {
            templateListPage.navigate();

            // The static list.html has 8 template cards displayed
            int count = templateListPage.getTemplateCount();
            assertThat(count).isGreaterThanOrEqualTo(8);
        }
    }

    @Nested
    @DisplayName("1.4.2 Template Detail")
    class TemplateDetailTests {

        @Test
        @DisplayName("Should display template detail page")
        void shouldDisplayTemplateDetailPage() {
            templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateDetailPage.assertPageTitleVisible();
            templateDetailPage.assertTemplateNameVisible();
        }

        @Test
        @DisplayName("Should display correct template name")
        void shouldDisplayCorrectTemplateName() {
            templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateDetailPage.assertTemplateNameText("Pendapatan Jasa Konsultasi");
        }

        @Test
        @DisplayName("Should display execute button")
        void shouldDisplayExecuteButton() {
            templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateDetailPage.assertExecuteButtonVisible();
        }
    }

    @Nested
    @DisplayName("1.4.3 Template Execution Page")
    class TemplateExecutionPageTests {

        @Test
        @DisplayName("Should display execution page")
        void shouldDisplayExecutionPage() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.assertPageTitleVisible();
            templateExecutePage.assertTemplateNameVisible();
        }

        @Test
        @DisplayName("Should display execution form fields")
        void shouldDisplayExecutionFormFields() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.assertTransactionDateVisible();
            templateExecutePage.assertAmountInputVisible();
            templateExecutePage.assertDescriptionInputVisible();
            templateExecutePage.assertPreviewButtonVisible();
        }
    }

    @Nested
    @DisplayName("1.4.4 Template Preview")
    class TemplatePreviewTests {

        @Test
        @DisplayName("Should show preview with correct entries")
        void shouldShowPreviewWithCorrectEntries() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("10000000");
            templateExecutePage.fillDescription("Konsultasi Project XYZ");
            templateExecutePage.clickPreviewButton();

            // Verify preview table and balance status are shown
            templateExecutePage.assertPreviewTableVisible();
            templateExecutePage.assertBalanceStatusVisible();
        }

        @Test
        @DisplayName("Should show balanced totals in preview")
        void shouldShowBalancedTotalsInPreview() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("10000000");
            templateExecutePage.fillDescription("Konsultasi Project XYZ");
            templateExecutePage.clickPreviewButton();

            templateExecutePage.assertBalanceStatusVisible();
            templateExecutePage.assertBalanced();
        }

        @Test
        @DisplayName("Should display account codes in preview table")
        void shouldDisplayAccountCodesInPreview() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("10000000");
            templateExecutePage.fillDescription("Konsultasi Project XYZ");
            templateExecutePage.clickPreviewButton();

            int rowCount = templateExecutePage.getPreviewRowCount();
            assertThat(rowCount).isGreaterThan(0);

            // Verify each row has account code displayed
            for (int i = 0; i < rowCount; i++) {
                templateExecutePage.assertAccountCodeVisible(i);
                templateExecutePage.assertAccountCodeNotEmpty(i);
            }
        }

        @Test
        @DisplayName("Should display account names in preview table")
        void shouldDisplayAccountNamesInPreview() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("10000000");
            templateExecutePage.fillDescription("Konsultasi Project XYZ");
            templateExecutePage.clickPreviewButton();

            int rowCount = templateExecutePage.getPreviewRowCount();
            assertThat(rowCount).isGreaterThan(0);

            // Verify each row has account name displayed
            for (int i = 0; i < rowCount; i++) {
                templateExecutePage.assertAccountNameVisible(i);
                templateExecutePage.assertAccountNameNotEmpty(i);
            }
        }
    }

    @Nested
    @DisplayName("1.4.5 Template Execution")
    class TemplateExecutionTests {

        @Test
        @DisplayName("Should create journal entry from template")
        void shouldCreateJournalEntryFromTemplate() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("15000000");
            templateExecutePage.fillDescription("Konsultasi IT Implementation");
            templateExecutePage.clickPreviewButton();
            templateExecutePage.clickExecuteButton();

            // Should redirect to transaction detail page with success message
            templateExecutePage.assertRedirectedToTransactionDetail();
            templateExecutePage.assertSuccessMessageVisible();
        }

        @Test
        @DisplayName("Should redirect to transaction detail after execution")
        void shouldRedirectToTransactionDetailAfterExecution() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("20000000");
            templateExecutePage.fillDescription("Konsultasi Arsitektur Sistem");
            templateExecutePage.clickPreviewButton();
            templateExecutePage.clickExecuteButton();

            templateExecutePage.assertRedirectedToTransactionDetail();
        }
    }

    @Nested
    @DisplayName("1.4.6 Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should show error when amount is empty")
        void shouldShowErrorWhenAmountIsEmpty() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillDescription("Test without amount");
            templateExecutePage.clickPreviewButton();

            templateExecutePage.assertErrorMessageVisible();
        }

        @Test
        @DisplayName("Should show error when description is empty")
        void shouldShowErrorWhenDescriptionIsEmpty() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2025-06-30");
            templateExecutePage.fillAmount("10000000");
            templateExecutePage.clickPreviewButton();

            templateExecutePage.assertErrorMessageVisible();
        }
    }

    @Nested
    @DisplayName("1.4.7 Template Form Page")
    class TemplateFormTests {

        @Test
        @DisplayName("Should display new template form page")
        void shouldDisplayNewTemplateFormPage() {
            templateFormPage.navigateToNew();

            templateFormPage.assertPageTitleText("Template Baru");
            templateFormPage.assertTemplateNameInputVisible();
            templateFormPage.assertCategorySelectVisible();
            templateFormPage.assertSaveButtonVisible();
        }

        @Test
        @DisplayName("Should have account options populated from database")
        void shouldHaveAccountOptionsPopulated() {
            templateFormPage.navigateToNew();

            int accountCount = templateFormPage.getAccountOptionsCount();
            assertThat(accountCount).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should navigate to new template form from list page")
        void shouldNavigateToNewTemplateFromList() {
            templateListPage.navigate();
            templateListPage.clickNewTemplateButton();

            templateFormPage.assertPageTitleText("Template Baru");
        }
    }

    @Nested
    @DisplayName("1.4.8 Template Create")
    class TemplateCreateTests {

        @Test
        @DisplayName("Should create new template and show in detail page")
        void shouldCreateNewTemplate() {
            templateFormPage.navigateToNew();

            String uniqueName = "Template Test " + System.currentTimeMillis();
            templateFormPage.fillTemplateName(uniqueName);
            templateFormPage.selectCategory("INCOME");
            templateFormPage.selectCashFlowCategory("OPERATING");
            templateFormPage.fillDescription("Template untuk testing");

            // Select accounts for lines (use first available accounts)
            String firstAccountId = templateFormPage.getFirstAccountId();
            String secondAccountId = templateFormPage.getSecondAccountId();
            templateFormPage.selectAccountForLine(0, firstAccountId);
            templateFormPage.selectAccountForLine(1, secondAccountId);

            templateFormPage.clickSave();

            // Should redirect to detail page
            templateDetailPage.assertTemplateNameText(uniqueName);
            templateDetailPage.assertVersionText("1");
        }

        @Test
        @DisplayName("Should show new template in list after creation")
        void shouldShowNewTemplateInList() {
            templateFormPage.navigateToNew();

            String uniqueName = "Template List Test " + System.currentTimeMillis();
            templateFormPage.fillTemplateName(uniqueName);
            templateFormPage.selectCategory("EXPENSE");
            templateFormPage.selectCashFlowCategory("OPERATING");

            // Select accounts for lines
            String firstAccountId = templateFormPage.getFirstAccountId();
            String secondAccountId = templateFormPage.getSecondAccountId();
            templateFormPage.selectAccountForLine(0, firstAccountId);
            templateFormPage.selectAccountForLine(1, secondAccountId);

            templateFormPage.clickSave();

            // Navigate to list and verify template appears
            templateListPage.navigate();
            templateListPage.assertTemplateVisible(uniqueName);
        }
    }

    @Nested
    @DisplayName("1.4.9 Template Edit and Versioning")
    class TemplateEditTests {

        @Test
        @DisplayName("Should display edit form for non-system template")
        void shouldDisplayEditFormForNonSystemTemplate() {
            // Navigate to test template detail page
            templateDetailPage.navigate(TEST_EDITABLE_TEMPLATE_ID);

            // Click edit button from detail page
            templateDetailPage.clickEditButton();

            templateFormPage.assertPageTitleText("Edit Template");
            assertThat(templateFormPage.getTemplateNameValue()).isEqualTo("Test Template - Editable");
        }

        @Test
        @DisplayName("Should increment version after edit")
        void shouldIncrementVersionAfterEdit() {
            // Navigate to test template detail page
            templateDetailPage.navigate(TEST_EDITABLE_TEMPLATE_ID);

            // Verify initial version is 1
            templateDetailPage.assertVersionText("1");

            // Edit the template
            templateDetailPage.clickEditButton();
            templateFormPage.fillDescription("Updated description");
            templateFormPage.clickSave();

            // Verify version is now 2
            templateDetailPage.assertVersionText("2");
        }
    }

    @Nested
    @DisplayName("1.4.10 Template Delete")
    class TemplateDeleteTests {

        @Test
        @DisplayName("Should delete non-system template")
        void shouldDeleteNonSystemTemplate() {
            // First create a non-system template
            templateFormPage.navigateToNew();

            String uniqueName = "Delete Test " + System.currentTimeMillis();
            templateFormPage.fillTemplateName(uniqueName);
            templateFormPage.selectCategory("RECEIPT");
            templateFormPage.selectCashFlowCategory("OPERATING");
            String firstAccountId = templateFormPage.getFirstAccountId();
            String secondAccountId = templateFormPage.getSecondAccountId();
            templateFormPage.selectAccountForLine(0, firstAccountId);
            templateFormPage.selectAccountForLine(1, secondAccountId);
            templateFormPage.clickSave();

            // Delete from detail page
            templateDetailPage.clickDeleteButton();

            // Should redirect to list and template should not be visible
            templateListPage.assertTemplateNotVisible(uniqueName);
        }
    }

    @Nested
    @DisplayName("1.4.11 System Template Protection")
    class SystemTemplateProtectionTests {

        // True system template: Post Gaji Bulanan (used by PayrollService)
        private static final String SYSTEM_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000014";

        @Test
        @DisplayName("Should not show edit button for system template")
        void shouldNotShowEditButtonForSystemTemplate() {
            templateDetailPage.navigate(SYSTEM_TEMPLATE_ID);

            templateDetailPage.assertEditButtonNotVisible();
        }

        @Test
        @DisplayName("Should not show delete button for system template")
        void shouldNotShowDeleteButtonForSystemTemplate() {
            templateDetailPage.navigate(SYSTEM_TEMPLATE_ID);

            templateDetailPage.assertDeleteButtonNotVisible();
        }

        @Test
        @DisplayName("Should show version in detail page")
        void shouldShowVersionInDetailPage() {
            templateDetailPage.navigate(SYSTEM_TEMPLATE_ID);

            templateDetailPage.assertVersionVisible();
        }
    }

    @Nested
    @DisplayName("1.4.12 Formula Help Panel")
    class FormulaHelpTests {

        @Test
        @DisplayName("Should display formula help panel when clicked")
        void shouldDisplayFormulaHelpPanel() {
            templateFormPage.navigateToNew();

            templateFormPage.clickFormulaHelpButton();

            templateFormPage.assertFormulaHelpPanelVisible();
        }

        @Test
        @DisplayName("Should show Coba Formula tab with result")
        void shouldShowCobaFormulaWithResult() {
            templateFormPage.navigateToNew();

            templateFormPage.clickFormulaHelpButton();
            templateFormPage.clickCobaFormulaTab();
            templateFormPage.fillTryFormula("amount * 0.11");
            templateFormPage.fillTryAmount("10000000");

            templateFormPage.assertTryResultVisible();
            String result = templateFormPage.getTryResult();
            assertThat(result).isEqualTo("1.100.000");
        }

        @Test
        @DisplayName("Should calculate DPP from gross (amount / 1.11)")
        void shouldCalculateDppFromGross() {
            templateFormPage.navigateToNew();

            templateFormPage.clickFormulaHelpButton();
            templateFormPage.clickCobaFormulaTab();
            templateFormPage.fillTryFormula("amount / 1.11");
            templateFormPage.fillTryAmount("11100000");

            String result = templateFormPage.getTryResult();
            assertThat(result).isEqualTo("10.000.000");
        }

        @Test
        @DisplayName("Should calculate conditional PPh 23 above threshold")
        void shouldCalculatePph23AboveThreshold() {
            templateFormPage.navigateToNew();

            templateFormPage.clickFormulaHelpButton();
            templateFormPage.clickCobaFormulaTab();
            templateFormPage.fillTryFormula("amount > 2000000 ? amount * 0.02 : 0");
            templateFormPage.fillTryAmount("5000000");

            String result = templateFormPage.getTryResult();
            assertThat(result).isEqualTo("100.000");
        }

        @Test
        @DisplayName("Should calculate conditional PPh 23 below threshold")
        void shouldCalculatePph23BelowThreshold() {
            templateFormPage.navigateToNew();

            templateFormPage.clickFormulaHelpButton();
            templateFormPage.clickCobaFormulaTab();
            templateFormPage.fillTryFormula("amount > 2000000 ? amount * 0.02 : 0");
            templateFormPage.fillTryAmount("1500000");

            String result = templateFormPage.getTryResult();
            assertThat(result).isEqualTo("0");
        }

        @Test
        @DisplayName("Should show quick example buttons")
        void shouldShowQuickExampleButtons() {
            templateFormPage.navigateToNew();

            templateFormPage.clickFormulaHelpButton();
            templateFormPage.clickCobaFormulaTab();
            templateFormPage.fillTryAmount("10000000");

            // Click quick example and verify result changes
            templateFormPage.clickQuickExample("amount * 0.11");

            String result = templateFormPage.getTryResult();
            assertThat(result).isEqualTo("1.100.000");
        }
    }

    @Nested
    @DisplayName("1.4.13 Formula Calculations")
    class FormulaCalculationTests {

        // Template IDs from V903 test migration
        private static final String PPN_SALE_TEMPLATE_ID = "f0000000-0000-0000-0000-000000000011";
        private static final String PPH23_TEMPLATE_ID = "f0000000-0000-0000-0000-000000000013";

        @Test
        @DisplayName("Should preview PPN template with calculated amounts")
        void shouldPreviewPpnTemplateWithCalculatedAmounts() {
            templateExecutePage.navigate(PPN_SALE_TEMPLATE_ID);

            // Input gross amount: 11,100,000
            templateExecutePage.fillAmount("11100000");
            templateExecutePage.fillDescription("Test PPN calculation");

            // Preview should show calculated values
            templateExecutePage.clickPreviewButton();

            // Expected: DPP = 10,000,000, PPN = 1,100,000
            templateExecutePage.assertPreviewTableVisible();
            templateExecutePage.assertTotalDebitText("11.100.000");
            templateExecutePage.assertTotalCreditText("11.100.000");
        }

        @Test
        @DisplayName("Should preview PPh 23 template with amount above threshold")
        void shouldPreviewPph23TemplateAboveThreshold() {
            templateExecutePage.navigate(PPH23_TEMPLATE_ID);

            // Input 5,000,000 (above 2,000,000 threshold)
            templateExecutePage.fillAmount("5000000");
            templateExecutePage.fillDescription("Test PPh 23 above threshold");

            templateExecutePage.clickPreviewButton();

            // Expected: PPh 23 = 100,000 (2% of 5,000,000)
            // Total Debit = 5,000,000, Total Credit = 4,900,000 + 100,000 = 5,000,000
            templateExecutePage.assertPreviewTableVisible();
            templateExecutePage.assertTotalDebitText("5.000.000");
            templateExecutePage.assertTotalCreditText("5.000.000");
        }

        @Test
        @DisplayName("Should preview PPh 23 template with amount below threshold")
        void shouldPreviewPph23TemplateBelowThreshold() {
            templateExecutePage.navigate(PPH23_TEMPLATE_ID);

            // Input 1,500,000 (below 2,000,000 threshold)
            templateExecutePage.fillAmount("1500000");
            templateExecutePage.fillDescription("Test PPh 23 below threshold");

            templateExecutePage.clickPreviewButton();

            // Expected: PPh 23 = 0 (below threshold)
            // Total Debit = 1,500,000, Total Credit = 1,500,000 + 0 = 1,500,000
            templateExecutePage.assertPreviewTableVisible();
            templateExecutePage.assertTotalDebitText("1.500.000");
            templateExecutePage.assertTotalCreditText("1.500.000");
        }
    }
}
