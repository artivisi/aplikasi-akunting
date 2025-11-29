package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TransactionDetailPage;
import com.artivisi.accountingfinance.functional.page.TransactionFormPage;
import com.artivisi.accountingfinance.functional.page.TransactionListPage;
import com.artivisi.accountingfinance.functional.page.TransactionVoidPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transactions (Section 1.5)")
class TransactionTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TransactionListPage transactionListPage;
    private TransactionFormPage transactionFormPage;
    private TransactionDetailPage transactionDetailPage;
    private TransactionVoidPage transactionVoidPage;

    // Test data IDs from V904 migration
    private static final String DRAFT_TRANSACTION_ID = "a0000000-0000-0000-0000-000000000001";
    private static final String POSTED_TRANSACTION_ID = "a0000000-0000-0000-0000-000000000002";
    private static final String VOIDED_TRANSACTION_ID = "a0000000-0000-0000-0000-000000000003";

    // Template ID from V003 seed data
    private static final String INCOME_CONSULTING_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        transactionListPage = new TransactionListPage(page, baseUrl());
        transactionFormPage = new TransactionFormPage(page, baseUrl());
        transactionDetailPage = new TransactionDetailPage(page, baseUrl());
        transactionVoidPage = new TransactionVoidPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("1.5.1 Transaction List")
    class TransactionListTests {

        @Test
        @DisplayName("Should display transaction list page")
        void shouldDisplayTransactionListPage() {
            transactionListPage.navigate();

            transactionListPage.assertPageLoaded();
            transactionListPage.assertPageTitleText("Transaksi");
        }

        @Test
        @DisplayName("Should display new transaction button")
        void shouldDisplayNewTransactionButton() {
            transactionListPage.navigate();

            transactionListPage.assertNewTransactionButtonVisible();
        }

        @Test
        @DisplayName("Should display filter options")
        void shouldDisplayFilterOptions() {
            transactionListPage.navigate();

            transactionListPage.assertFilterOptionsVisible();
        }

        @Test
        @DisplayName("Should display test transactions from seed data")
        void shouldDisplayTestTransactions() {
            transactionListPage.navigate();

            // Should have test transactions from V904
            int count = transactionListPage.getTransactionCount();
            assertThat(count).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("Should navigate to transaction detail when clicking row")
        void shouldNavigateToDetailWhenClicking() {
            transactionListPage.navigate();
            transactionListPage.clickTransaction("TRX-TEST-0001");

            transactionDetailPage.assertPageLoaded();
        }
    }

    @Nested
    @DisplayName("1.5.2 Transaction Form")
    class TransactionFormTests {

        @Test
        @DisplayName("Should display template selection when no template is selected")
        void shouldDisplayTemplateSelection() {
            transactionFormPage.navigateToNew();

            transactionFormPage.assertPageLoaded();
            transactionFormPage.assertTemplateSelectionVisible();
        }

        @Test
        @DisplayName("Should display transaction form when template is selected")
        void shouldDisplayTransactionForm() {
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            transactionFormPage.assertPageLoaded();
            transactionFormPage.assertFormVisible();
        }

        @Test
        @DisplayName("Should display journal preview section")
        void shouldDisplayJournalPreview() {
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            transactionFormPage.assertJournalPreviewVisible();
        }

        @Test
        @DisplayName("Should display save buttons")
        void shouldDisplaySaveButtons() {
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            transactionFormPage.assertSaveDraftButtonVisible();
            transactionFormPage.assertSavePostButtonVisible();
        }
    }

    @Nested
    @DisplayName("1.5.3 Transaction Detail - Draft")
    class TransactionDetailDraftTests {

        @Test
        @DisplayName("Should display draft transaction detail")
        void shouldDisplayDraftTransactionDetail() {
            transactionDetailPage.navigate(DRAFT_TRANSACTION_ID);

            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertDraftStatus();
        }

        @Test
        @DisplayName("Should display edit button for draft")
        void shouldDisplayEditButtonForDraft() {
            transactionDetailPage.navigate(DRAFT_TRANSACTION_ID);

            transactionDetailPage.assertEditButtonVisible();
        }

        @Test
        @DisplayName("Should display post button for draft")
        void shouldDisplayPostButtonForDraft() {
            transactionDetailPage.navigate(DRAFT_TRANSACTION_ID);

            transactionDetailPage.assertPostButtonVisible();
        }

        @Test
        @DisplayName("Should display delete button for draft")
        void shouldDisplayDeleteButtonForDraft() {
            transactionDetailPage.navigate(DRAFT_TRANSACTION_ID);

            transactionDetailPage.assertDeleteButtonVisible();
        }

        @Test
        @DisplayName("Should NOT display void button for draft")
        void shouldNotDisplayVoidButtonForDraft() {
            transactionDetailPage.navigate(DRAFT_TRANSACTION_ID);

            transactionDetailPage.assertVoidButtonNotVisible();
        }
    }

    @Nested
    @DisplayName("1.5.4 Transaction Detail - Posted")
    class TransactionDetailPostedTests {

        @Test
        @DisplayName("Should display posted transaction detail")
        void shouldDisplayPostedTransactionDetail() {
            transactionDetailPage.navigate(POSTED_TRANSACTION_ID);

            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();
        }

        @Test
        @DisplayName("Should NOT display edit button for posted")
        void shouldNotDisplayEditButtonForPosted() {
            transactionDetailPage.navigate(POSTED_TRANSACTION_ID);

            transactionDetailPage.assertEditButtonNotVisible();
        }

        @Test
        @DisplayName("Should NOT display post button for posted")
        void shouldNotDisplayPostButtonForPosted() {
            transactionDetailPage.navigate(POSTED_TRANSACTION_ID);

            transactionDetailPage.assertPostButtonNotVisible();
        }

        @Test
        @DisplayName("Should display void button for posted")
        void shouldDisplayVoidButtonForPosted() {
            transactionDetailPage.navigate(POSTED_TRANSACTION_ID);

            transactionDetailPage.assertVoidButtonVisible();
        }

        @Test
        @DisplayName("Should display journal entries for posted")
        void shouldDisplayJournalEntriesForPosted() {
            transactionDetailPage.navigate(POSTED_TRANSACTION_ID);

            transactionDetailPage.assertJournalEntriesVisible();
        }
    }

    @Nested
    @DisplayName("1.5.5 Transaction Detail - Voided")
    class TransactionDetailVoidedTests {

        @Test
        @DisplayName("Should display voided transaction detail")
        void shouldDisplayVoidedTransactionDetail() {
            transactionDetailPage.navigate(VOIDED_TRANSACTION_ID);

            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertVoidStatus();
        }

        @Test
        @DisplayName("Should NOT display edit button for voided")
        void shouldNotDisplayEditButtonForVoided() {
            transactionDetailPage.navigate(VOIDED_TRANSACTION_ID);

            transactionDetailPage.assertEditButtonNotVisible();
        }

        @Test
        @DisplayName("Should NOT display void button for voided")
        void shouldNotDisplayVoidButtonForVoided() {
            transactionDetailPage.navigate(VOIDED_TRANSACTION_ID);

            transactionDetailPage.assertVoidButtonNotVisible();
        }
    }

    @Nested
    @DisplayName("1.5.6 Void Page")
    class VoidPageTests {

        @Test
        @DisplayName("Should display void page for posted transaction")
        void shouldDisplayVoidPage() {
            transactionVoidPage.navigate(POSTED_TRANSACTION_ID);

            transactionVoidPage.assertPageLoaded();
            transactionVoidPage.assertPageTitleText("Void Transaksi");
        }

        @Test
        @DisplayName("Should display warning banner")
        void shouldDisplayWarningBanner() {
            transactionVoidPage.navigate(POSTED_TRANSACTION_ID);

            transactionVoidPage.assertWarningBannerVisible();
        }

        @Test
        @DisplayName("Should display void form")
        void shouldDisplayVoidForm() {
            transactionVoidPage.navigate(POSTED_TRANSACTION_ID);

            transactionVoidPage.assertVoidFormVisible();
        }

        @Test
        @DisplayName("Should display journal entries to be cancelled")
        void shouldDisplayJournalEntriesToBeCancelled() {
            transactionVoidPage.navigate(POSTED_TRANSACTION_ID);

            transactionVoidPage.assertJournalEntryToBeCancelledVisible();
        }
    }

    @Nested
    @DisplayName("1.5.7 Create Transaction Flow")
    class CreateTransactionFlowTests {

        @Test
        @DisplayName("Should create draft transaction from template")
        void shouldCreateDraftTransactionFromTemplate() {
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            transactionFormPage.fillAmount("25000000");
            transactionFormPage.fillDescription("Test New Transaction " + System.currentTimeMillis());
            transactionFormPage.fillReferenceNumber("INV-NEW-001");
            transactionFormPage.clickSaveDraft();

            // Should redirect to detail page
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertDraftStatus();
        }

        @Test
        @DisplayName("Should create and post transaction from template")
        void shouldCreateAndPostTransactionFromTemplate() {
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

            transactionFormPage.fillAmount("30000000");
            transactionFormPage.fillDescription("Test Posted Transaction " + System.currentTimeMillis());
            transactionFormPage.clickSaveAndPost();

            // Should redirect to detail page with posted status
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();
        }
    }

    @Nested
    @DisplayName("1.5.8 Post Transaction Flow")
    class PostTransactionFlowTests {

        @Test
        @DisplayName("Should post draft transaction from detail page")
        void shouldPostDraftTransaction() {
            // First create a new draft to post
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount("35000000");
            transactionFormPage.fillDescription("Test Post Transaction " + System.currentTimeMillis());
            transactionFormPage.clickSaveDraft();

            // Now post it
            transactionDetailPage.clickPostButton();

            transactionDetailPage.assertPostedStatus();
        }
    }

    @Nested
    @DisplayName("1.5.9 Void Transaction Flow")
    class VoidTransactionFlowTests {

        @Test
        @DisplayName("Should void posted transaction")
        void shouldVoidPostedTransaction() {
            // First create and post a new transaction
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount("40000000");
            transactionFormPage.fillDescription("Test Void Transaction " + System.currentTimeMillis());
            transactionFormPage.clickSaveAndPost();

            // Go to void page
            transactionDetailPage.clickVoidButton();

            // Fill void form
            transactionVoidPage.selectVoidReason("INPUT_ERROR");
            transactionVoidPage.fillVoidNotes("Testing void functionality");
            transactionVoidPage.checkConfirmation();
            transactionVoidPage.clickVoidButton();

            // Should redirect to detail page with void status
            transactionDetailPage.assertVoidStatus();
        }
    }

    @Nested
    @DisplayName("1.5.10 Edit Transaction Flow")
    class EditTransactionFlowTests {

        @Test
        @DisplayName("Should edit draft transaction")
        void shouldEditDraftTransaction() {
            // Navigate to draft transaction
            transactionDetailPage.navigate(DRAFT_TRANSACTION_ID);

            // Click edit
            transactionDetailPage.clickEditButton();

            // Verify form is loaded with data
            transactionFormPage.assertPageLoaded();
            transactionFormPage.assertPageTitleText("Edit Transaksi");
        }
    }

    @Nested
    @DisplayName("1.5.11 Delete Transaction Flow")
    class DeleteTransactionFlowTests {

        @Test
        @DisplayName("Should delete draft transaction")
        void shouldDeleteDraftTransaction() {
            // First create a new draft to delete
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            String uniqueDesc = "Test Delete Transaction " + System.currentTimeMillis();
            transactionFormPage.fillAmount("45000000");
            transactionFormPage.fillDescription(uniqueDesc);
            transactionFormPage.clickSaveDraft();

            // Now delete it
            transactionDetailPage.clickDeleteButton();

            // Should redirect to list
            transactionListPage.assertPageLoaded();
        }
    }

    @Nested
    @DisplayName("1.5.12 Formula Preview Calculation")
    class FormulaPreviewTests {

        /**
         * Test template "Penjualan Jasa dengan PPN" from V903__formula_test_templates.sql
         * Formulas:
         * - Bank (Debit): amount (full amount)
         * - Pendapatan (Credit): amount / 1.11 (DPP)
         * - PPN Keluaran (Credit): amount - (amount / 1.11) (PPN 11%)
         */
        private static final String TEST_TEMPLATE_ID = "f0000000-0000-0000-0000-000000000011";

        @Test
        @DisplayName("Should show correct calculated amounts in preview for complex formulas")
        void shouldShowCorrectCalculatedAmountsInPreview() {
            // Use test template with PPN formulas from V903__formula_test_templates.sql
            transactionFormPage.navigate(TEST_TEMPLATE_ID);
            
            // Input amount: 11,100,000 (includes PPN 11%)
            transactionFormPage.fillAmount("11100000");
            
            // Wait for preview to load via HTMX
            page.waitForTimeout(1000);
            
            // Verify preview shows calculated amounts, not just the input amount
            // Expected calculations for 11,100,000:
            // - Bank (Debit) = 11,100,000 (full amount)
            // - Pendapatan (Credit) = 11,100,000 / 1.11 = 10,000,000 (DPP)
            // - PPN Keluaran (Credit) = 11,100,000 - 10,000,000 = 1,100,000 (PPN 11%)
            
            // Get all debit and credit amounts from preview
            var previewLines = page.locator("#preview-content .grid.grid-cols-12").all();
            assertThat(previewLines).hasSizeGreaterThanOrEqualTo(3);
            
            // Verify that amounts are different (not all showing 11,100,000)
            String firstDebitAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(1) .col-span-3:nth-child(3)").innerText();
            String firstCreditAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(2) .col-span-3:nth-child(4)").innerText();
            String secondCreditAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(3) .col-span-3:nth-child(4)").innerText();
            
            // First line (Bank Debit) should show 11,100,000 (full amount)
            assertThat(firstDebitAmount).contains("11.100.000");
            
            // Second line (Pendapatan Credit) should show 10,000,000 (DPP - not the same as input)
            assertThat(firstCreditAmount).contains("10.000.000");
            assertThat(firstCreditAmount).doesNotContain("11.100.000");
            
            // Third line (PPN Keluaran Credit) should show 1,100,000 (PPN - not the same as input)
            assertThat(secondCreditAmount).contains("1.100.000");
            assertThat(secondCreditAmount).doesNotContain("11.100.000");
        }

        @Test
        @DisplayName("Should update preview when amount changes")
        void shouldUpdatePreviewWhenAmountChanges() {
            transactionFormPage.navigate(TEST_TEMPLATE_ID);
            
            // Input first amount: 5,550,000
            transactionFormPage.fillAmount("5550000");
            page.waitForTimeout(1000);
            
            // Get first calculated values
            String firstBankAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(1) .col-span-3:nth-child(3)").innerText();
            String firstRevenueAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(2) .col-span-3:nth-child(4)").innerText();
            
            // Should show calculated amounts for 5.55M
            assertThat(firstBankAmount).contains("5.550.000");
            assertThat(firstRevenueAmount).contains("5.000.000"); // 5.55M / 1.11
            
            // Change amount to 22,200,000
            transactionFormPage.fillAmount("22200000");
            page.waitForTimeout(1000);
            
            // Get second calculated values
            String secondBankAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(1) .col-span-3:nth-child(3)").innerText();
            String secondRevenueAmount = page.locator("#preview-content .grid.grid-cols-12:nth-child(2) .col-span-3:nth-child(4)").innerText();
            
            // Should show new calculated amounts for 22.2M
            assertThat(secondBankAmount).contains("22.200.000");
            assertThat(secondRevenueAmount).contains("20.000.000"); // 22.2M / 1.11
            
            // Values should be different (preview updated)
            assertThat(firstBankAmount).isNotEqualTo(secondBankAmount);
            assertThat(firstRevenueAmount).isNotEqualTo(secondRevenueAmount);
        }
    }
}
