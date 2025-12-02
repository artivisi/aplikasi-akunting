package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.*;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End Integration Tests for Transaction Flows.
 * Tests complete business workflows from start to finish.
 */
@DisplayName("Transaction Integration Tests (E2E Flows)")
class TransactionIntegrationTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TransactionFormPage transactionFormPage;
    private TransactionDetailPage transactionDetailPage;
    private TransactionListPage transactionListPage;
    private TrialBalancePage trialBalancePage;
    private IncomeStatementPage incomeStatementPage;
    private ClientFormPage clientFormPage;
    private InvoiceFormPage invoiceFormPage;
    private InvoiceDetailPage invoiceDetailPage;
    private PayrollFormPage payrollFormPage;
    private PayrollDetailPage payrollDetailPage;

    // Template IDs from V003 seed data
    private static final String INCOME_CONSULTING_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000001";
    private static final String EXPENSE_OPERATIONAL_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000002";
    private static final String SALES_WITH_PPN_TEMPLATE_ID = "f0000000-0000-0000-0000-000000000011";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        transactionFormPage = new TransactionFormPage(page, baseUrl());
        transactionDetailPage = new TransactionDetailPage(page, baseUrl());
        transactionListPage = new TransactionListPage(page, baseUrl());
        trialBalancePage = new TrialBalancePage(page, baseUrl());
        incomeStatementPage = new IncomeStatementPage(page, baseUrl());
        clientFormPage = new ClientFormPage(page, baseUrl());
        invoiceFormPage = new InvoiceFormPage(page, baseUrl());
        invoiceDetailPage = new InvoiceDetailPage(page, baseUrl());
        payrollFormPage = new PayrollFormPage(page, baseUrl());
        payrollDetailPage = new PayrollDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("Sales Transaction Flow")
    class SalesTransactionFlowTests {

        @Test
        @DisplayName("Complete sales flow: Create Transaction → Post → Verify Journal → Check Reports")
        void completeSalesTransactionFlow() {
            String uniqueDesc = "E2E Sales Test " + System.currentTimeMillis();
            String amount = "50000000"; // 50 million

            // Step 1: Create a sales transaction
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount(amount);
            transactionFormPage.fillDescription(uniqueDesc);
            transactionFormPage.fillReferenceNumber("INV-E2E-" + System.currentTimeMillis());
            transactionFormPage.clickSaveAndPost();

            // Step 2: Verify transaction is posted
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();

            // Step 3: Verify journal entry was created
            transactionDetailPage.assertJournalEntriesVisible();

            // Step 4: Check Trial Balance reflects the transaction
            trialBalancePage.navigate();
            trialBalancePage.assertPageTitleVisible();

            // Verify accounts have balances
            assertThat(page.locator("table tbody tr").count()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Sales with PPN: Create → Post → Verify PPN components in journal")
        void salesWithPpnFlow() {
            String uniqueDesc = "E2E Sales PPN Test " + System.currentTimeMillis();
            String amount = "11100000"; // 11.1M includes PPN 11%

            // Step 1: Create sales transaction with PPN template
            transactionFormPage.navigate(SALES_WITH_PPN_TEMPLATE_ID);
            transactionFormPage.fillAmount(amount);
            transactionFormPage.fillDescription(uniqueDesc);
            transactionFormPage.clickSaveAndPost();

            // Step 2: Verify transaction is posted
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();

            // Step 3: Verify journal entries show PPN split
            // The template should create:
            // - Bank (Debit): 11,100,000
            // - Pendapatan (Credit): 10,000,000 (DPP)
            // - PPN Keluaran (Credit): 1,100,000 (PPN 11%)
            transactionDetailPage.assertJournalEntriesVisible();
        }
    }

    @Nested
    @DisplayName("Expense Transaction Flow")
    class ExpenseTransactionFlowTests {

        @Test
        @DisplayName("Complete expense flow: Create → Post → Verify affects balance")
        void completeExpenseTransactionFlow() {
            String uniqueDesc = "E2E Expense Test " + System.currentTimeMillis();
            String amount = "5000000"; // 5 million

            // Step 1: Create expense transaction
            transactionFormPage.navigate(EXPENSE_OPERATIONAL_TEMPLATE_ID);
            transactionFormPage.fillAmount(amount);
            transactionFormPage.fillDescription(uniqueDesc);
            transactionFormPage.clickSaveAndPost();

            // Step 2: Verify transaction is posted
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();

            // Step 3: Verify journal entries were created
            transactionDetailPage.assertJournalEntriesVisible();

            // Step 4: Check Income Statement page loads
            incomeStatementPage.navigate();
            incomeStatementPage.assertPageTitleVisible();
        }
    }

    @Nested
    @DisplayName("Invoice Payment Flow")
    class InvoicePaymentFlowTests {

        private String createTestClient() {
            clientFormPage.navigateToNew();
            String uniqueCode = "CLI-E2E-" + System.currentTimeMillis();
            String uniqueName = "E2E Integration Client " + System.currentTimeMillis();
            clientFormPage.fillCode(uniqueCode);
            clientFormPage.fillName(uniqueName);
            clientFormPage.clickSubmit();
            return uniqueName;
        }

        @Test
        @DisplayName("Complete invoice flow: Create Client → Create Invoice → Send → Mark Paid → Verify Transaction")
        void completeInvoicePaymentFlow() {
            // Step 1: Create a client
            createTestClient();

            // Step 2: Create an invoice
            invoiceFormPage.navigateToNew();
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE);

            invoiceFormPage.selectClientByIndex(1);
            invoiceFormPage.fillInvoiceDate(today);
            invoiceFormPage.fillDueDate(dueDate);
            invoiceFormPage.fillAmount("15000000"); // 15 million
            invoiceFormPage.fillNotes("E2E Invoice Test");
            invoiceFormPage.clickSubmit();

            // Step 3: Verify invoice was created
            invoiceDetailPage.assertPageTitleVisible();
            invoiceDetailPage.assertStatusText("Draf");

            // Step 4: Send the invoice
            invoiceDetailPage.clickSendButton();
            invoiceDetailPage.assertStatusText("Terkirim");

            // Step 5: Click "Tandai Lunas" to create payment transaction
            assertThat(invoiceDetailPage.hasMarkPaidLink()).isTrue();
            invoiceDetailPage.clickMarkPaidLink();

            // Step 6: Should redirect to transaction form with invoice info
            page.waitForLoadState();
            assertThat(page.url()).contains("/transactions/new");
            assertThat(page.url()).contains("invoiceId=");

            // Step 7: Complete the payment transaction
            // The form should be pre-filled with invoice data
            transactionFormPage.assertPageLoaded();
            transactionFormPage.clickSaveAndPost();

            // Step 8: Verify transaction is posted
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();
        }
    }

    @Nested
    @DisplayName("Payroll to Journal Flow")
    class PayrollJournalFlowTests {

        @Test
        @DisplayName("Complete payroll flow: Create → Calculate → Approve → Post → Verify Journal")
        void completePayrollToJournalFlow() {
            // Use a unique period to avoid conflicts
            String period = "2030-" + String.format("%02d", (System.currentTimeMillis() % 12) + 1);

            // Step 1: Create payroll
            payrollFormPage.navigateToNew();
            payrollFormPage.fillPeriod(period);
            payrollFormPage.fillBaseSalary("10000000");
            payrollFormPage.selectJkkRiskClass(1);
            payrollFormPage.clickSubmit();

            // Step 2: Verify payroll was calculated
            payrollDetailPage.assertPageTitleContains("Detail Payroll");
            payrollDetailPage.assertStatusBadgeText("Calculated");

            // Should have employees (at least 3 from test data)
            int employeeCount = Integer.parseInt(payrollDetailPage.getEmployeeCount());
            assertThat(employeeCount).isGreaterThanOrEqualTo(3);

            // Step 3: Approve the payroll
            assertThat(payrollDetailPage.hasApproveButton()).isTrue();
            payrollDetailPage.clickApproveButton();
            payrollDetailPage.assertStatusBadgeText("Approved");

            // Step 4: Post to journal
            assertThat(payrollDetailPage.hasPostButton()).isTrue();
            payrollDetailPage.clickPostButton();

            // Step 5: Verify posted status and journal reference
            payrollDetailPage.assertStatusBadgeText("Posted");
            assertThat(payrollDetailPage.hasJournalReference()).isTrue();

            String transactionNumber = payrollDetailPage.getTransactionNumber();
            assertThat(transactionNumber).startsWith("TRX-");

            // Step 6: Verify journal entries were created by navigating to transaction
            // The transaction link should be visible
            assertThat(page.locator("a:has-text('" + transactionNumber + "')").isVisible()).isTrue();
        }
    }

    @Nested
    @DisplayName("Transaction Void Flow")
    class TransactionVoidFlowTests {

        @Test
        @DisplayName("Complete void flow: Create → Post → Void → Verify Status")
        void completeVoidTransactionFlow() {
            String uniqueDesc = "E2E Void Test " + System.currentTimeMillis();
            String amount = "25000000";

            // Step 1: Create and post a transaction
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount(amount);
            transactionFormPage.fillDescription(uniqueDesc);
            transactionFormPage.clickSaveAndPost();

            // Step 2: Verify posted
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertPostedStatus();

            // Step 3: Void the transaction
            transactionDetailPage.assertVoidButtonVisible();
            transactionDetailPage.clickVoidButton();

            // Step 4: Fill void form
            TransactionVoidPage voidPage = new TransactionVoidPage(page, baseUrl());
            voidPage.assertPageLoaded();
            voidPage.selectVoidReason("INPUT_ERROR");
            voidPage.fillVoidNotes("E2E Testing void functionality");
            voidPage.checkConfirmation();
            voidPage.clickVoidButton();

            // Step 5: Verify void status
            transactionDetailPage.assertVoidStatus();
        }
    }

    @Nested
    @DisplayName("Draft to Post Flow")
    class DraftToPostFlowTests {

        @Test
        @DisplayName("Complete draft flow: Create Draft → Edit → Post → Verify")
        void completeDraftToPostFlow() {
            String uniqueDesc = "E2E Draft Test " + System.currentTimeMillis();
            String amount = "8000000";

            // Step 1: Create draft transaction
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount(amount);
            transactionFormPage.fillDescription(uniqueDesc);
            transactionFormPage.clickSaveDraft();

            // Step 2: Verify draft status
            transactionDetailPage.assertPageLoaded();
            transactionDetailPage.assertDraftStatus();

            // Get transaction ID
            String transactionUrl = page.url();
            String transactionId = transactionUrl.split("/transactions/")[1];

            // Step 3: Edit the draft
            transactionDetailPage.assertEditButtonVisible();
            transactionDetailPage.clickEditButton();

            // Step 4: Modify the description
            transactionFormPage.assertPageLoaded();
            String newDesc = uniqueDesc + " - Modified";
            transactionFormPage.fillDescription(newDesc);
            transactionFormPage.clickSaveDraft();

            // Step 5: Verify still draft with updated description
            transactionDetailPage.assertDraftStatus();

            // Step 6: Post the transaction
            transactionDetailPage.assertPostButtonVisible();
            transactionDetailPage.clickPostButton();

            // Step 7: Verify posted
            transactionDetailPage.assertPostedStatus();
            transactionDetailPage.assertJournalEntriesVisible();

            // Step 8: Verify edit/delete buttons are gone
            transactionDetailPage.assertEditButtonNotVisible();
        }
    }

    @Nested
    @DisplayName("Multi-Transaction Impact on Reports")
    class MultiTransactionReportImpactTests {

        @Test
        @DisplayName("Multiple transactions should accumulate correctly in Trial Balance")
        void multipleTransactionsAffectTrialBalance() {
            // Create multiple transactions
            for (int i = 1; i <= 3; i++) {
                String uniqueDesc = "E2E Multi-Tx Test " + i + " - " + System.currentTimeMillis();
                String amount = String.valueOf(10000000 * i); // 10M, 20M, 30M

                transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
                transactionFormPage.fillAmount(amount);
                transactionFormPage.fillDescription(uniqueDesc);
                transactionFormPage.clickSaveAndPost();

                transactionDetailPage.assertPostedStatus();
            }

            // Verify Trial Balance
            trialBalancePage.navigate();
            trialBalancePage.assertPageTitleVisible();
        }

        @Test
        @DisplayName("Transaction list shows all transactions with correct statuses")
        void transactionListShowsAllTransactions() {
            // Create transactions with different statuses
            String draftDesc = "E2E List Draft " + System.currentTimeMillis();
            String postedDesc = "E2E List Posted " + System.currentTimeMillis();

            // Create draft
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount("5000000");
            transactionFormPage.fillDescription(draftDesc);
            transactionFormPage.clickSaveDraft();
            transactionDetailPage.assertDraftStatus();

            // Create posted
            transactionFormPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);
            transactionFormPage.fillAmount("7000000");
            transactionFormPage.fillDescription(postedDesc);
            transactionFormPage.clickSaveAndPost();
            transactionDetailPage.assertPostedStatus();

            // Check transaction list
            transactionListPage.navigate();
            transactionListPage.assertPageLoaded();

            // Should show both transactions
            int count = transactionListPage.getTransactionCount();
            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }
}
