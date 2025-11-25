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

            templateExecutePage.fillTransactionDate("2024-06-30");
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

            templateExecutePage.fillTransactionDate("2024-06-30");
            templateExecutePage.fillAmount("10000000");
            templateExecutePage.fillDescription("Konsultasi Project XYZ");
            templateExecutePage.clickPreviewButton();

            templateExecutePage.assertBalanceStatusVisible();
            templateExecutePage.assertBalanced();
        }
    }

    @Nested
    @DisplayName("1.4.5 Template Execution")
    class TemplateExecutionTests {

        @Test
        @DisplayName("Should create journal entry from template")
        void shouldCreateJournalEntryFromTemplate() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2024-06-30");
            templateExecutePage.fillAmount("15000000");
            templateExecutePage.fillDescription("Konsultasi IT Implementation");
            templateExecutePage.clickPreviewButton();
            templateExecutePage.clickExecuteButton();

            templateExecutePage.assertJournalNumberVisible();
            String journalNumber = templateExecutePage.getJournalNumber();
            assertThat(journalNumber).startsWith("JE-");
        }

        @Test
        @DisplayName("Should display view journal button after execution")
        void shouldDisplayViewJournalButtonAfterExecution() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2024-06-30");
            templateExecutePage.fillAmount("20000000");
            templateExecutePage.fillDescription("Konsultasi Arsitektur Sistem");
            templateExecutePage.clickPreviewButton();
            templateExecutePage.clickExecuteButton();

            templateExecutePage.assertViewJournalButtonVisible();
        }
    }

    @Nested
    @DisplayName("1.4.6 Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should show error when amount is empty")
        void shouldShowErrorWhenAmountIsEmpty() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2024-06-30");
            templateExecutePage.fillDescription("Test without amount");
            templateExecutePage.clickPreviewButton();

            templateExecutePage.assertErrorMessageVisible();
        }

        @Test
        @DisplayName("Should show error when description is empty")
        void shouldShowErrorWhenDescriptionIsEmpty() {
            templateExecutePage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateExecutePage.fillTransactionDate("2024-06-30");
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

        @Test
        @DisplayName("Should not show edit button for system template")
        void shouldNotShowEditButtonForSystemTemplate() {
            templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateDetailPage.assertEditButtonNotVisible();
        }

        @Test
        @DisplayName("Should not show delete button for system template")
        void shouldNotShowDeleteButtonForSystemTemplate() {
            templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateDetailPage.assertDeleteButtonNotVisible();
        }

        @Test
        @DisplayName("Should show version in detail page")
        void shouldShowVersionInDetailPage() {
            templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            templateDetailPage.assertVersionVisible();
        }
    }
}
