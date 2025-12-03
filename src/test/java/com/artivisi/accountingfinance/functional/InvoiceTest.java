package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.ClientFormPage;
import com.artivisi.accountingfinance.functional.page.InvoiceDetailPage;
import com.artivisi.accountingfinance.functional.page.InvoiceFormPage;
import com.artivisi.accountingfinance.functional.page.InvoiceListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Invoice Management (Section 1.9)")
class InvoiceTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private InvoiceListPage listPage;
    private InvoiceFormPage formPage;
    private InvoiceDetailPage detailPage;
    private ClientFormPage clientFormPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new InvoiceListPage(page, baseUrl());
        formPage = new InvoiceFormPage(page, baseUrl());
        detailPage = new InvoiceDetailPage(page, baseUrl());
        clientFormPage = new ClientFormPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("1.9.10 Invoice List")
    class InvoiceListTests {

        @Test
        @DisplayName("Should display invoice list page")
        void shouldDisplayInvoiceListPage() {
            listPage.navigate();

            listPage.assertPageTitleVisible();
            listPage.assertPageTitleText("Daftar Invoice");
        }

        @Test
        @DisplayName("Should display invoice table")
        void shouldDisplayInvoiceTable() {
            listPage.navigate();

            listPage.assertTableVisible();
        }
    }

    @Nested
    @DisplayName("1.9.11 Invoice Form")
    class InvoiceFormTests {

        @Test
        @DisplayName("Should display new invoice form")
        void shouldDisplayNewInvoiceForm() {
            formPage.navigateToNew();

            formPage.assertPageTitleText("Invoice Baru");
        }

        @Test
        @DisplayName("Should navigate to form from list page")
        void shouldNavigateToFormFromListPage() {
            listPage.navigate();
            listPage.clickNewInvoiceButton();

            formPage.assertPageTitleText("Invoice Baru");
        }
    }

    @Nested
    @DisplayName("1.9.12 Invoice CRUD")
    class InvoiceCrudTests {

        private void createTestClient() {
            clientFormPage.navigateToNew();
            String uniqueCode = "CLI-INV-" + System.currentTimeMillis();
            String uniqueName = "Invoice Test Client " + System.currentTimeMillis();
            clientFormPage.fillCode(uniqueCode);
            clientFormPage.fillName(uniqueName);
            clientFormPage.clickSubmit();
        }

        @Test
        @DisplayName("Should create new invoice")
        void shouldCreateNewInvoice() {
            // Create client first
            createTestClient();

            // Navigate to new invoice form
            formPage.navigateToNew();

            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE);

            // Select first available client (index 1, as 0 is "-- Pilih Klien --")
            formPage.selectClientByIndex(1);
            formPage.fillInvoiceDate(today);
            formPage.fillDueDate(dueDate);
            formPage.fillAmount("10000000");
            formPage.fillNotes("Test invoice");
            formPage.clickSubmit();

            // Should redirect to detail page with auto-generated invoice number
            detailPage.assertPageTitleVisible();
            assertThat(detailPage.hasSendButton()).isTrue();
        }
    }

    @Nested
    @DisplayName("1.9.13 Invoice Status")
    class InvoiceStatusTests {

        private void createTestClient() {
            clientFormPage.navigateToNew();
            String uniqueCode = "CLI-STS-" + System.currentTimeMillis();
            String uniqueName = "Status Test Client " + System.currentTimeMillis();
            clientFormPage.fillCode(uniqueCode);
            clientFormPage.fillName(uniqueName);
            clientFormPage.clickSubmit();
        }

        private void createTestInvoice() {
            formPage.navigateToNew();
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE);

            formPage.selectClientByIndex(1);
            formPage.fillInvoiceDate(today);
            formPage.fillDueDate(dueDate);
            formPage.fillAmount("5000000");
            formPage.clickSubmit();
        }

        @Test
        @DisplayName("Should send draft invoice")
        void shouldSendDraftInvoice() {
            createTestClient();
            createTestInvoice();

            // Should be draft by default
            detailPage.assertStatusText("Draf");
            assertThat(detailPage.hasSendButton()).isTrue();

            // Send the invoice
            detailPage.clickSendButton();

            // Should show sent status
            detailPage.assertStatusText("Terkirim");
            assertThat(detailPage.hasMarkPaidLink()).isTrue();
        }

        @Test
        @DisplayName("Should redirect to transaction form when clicking Tandai Lunas")
        void shouldRedirectToTransactionFormWhenMarkingPaid() {
            createTestClient();
            createTestInvoice();

            // Send first
            detailPage.clickSendButton();
            detailPage.assertStatusText("Terkirim");

            // Click "Tandai Lunas" - should redirect to transaction form
            detailPage.clickMarkPaidLink();

            // Should be on transaction form with template selected
            page.waitForLoadState();
            assertThat(page.url()).contains("/transactions/new");
            assertThat(page.url()).contains("invoiceId=");
            assertThat(page.url()).contains("templateId=");

            // Should see invoice payment info banner
            assertThat(page.locator("text=Pembayaran Invoice").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should cancel draft invoice")
        void shouldCancelDraftInvoice() {
            createTestClient();
            createTestInvoice();

            // Cancel
            detailPage.clickCancelButton();

            // Should show cancelled status
            detailPage.assertStatusText("Dibatalkan");
        }
    }

    @Nested
    @DisplayName("1.9.14 Invoice Edit")
    class InvoiceEditTests {

        private void createTestClient() {
            clientFormPage.navigateToNew();
            String uniqueCode = "CLI-EDT-" + System.currentTimeMillis();
            String uniqueName = "Edit Test Client " + System.currentTimeMillis();
            clientFormPage.fillCode(uniqueCode);
            clientFormPage.fillName(uniqueName);
            clientFormPage.clickSubmit();
        }

        private void createTestInvoice() {
            formPage.navigateToNew();
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE);

            formPage.selectClientByIndex(1);
            formPage.fillInvoiceDate(today);
            formPage.fillDueDate(dueDate);
            formPage.fillAmount("5000000");
            formPage.fillNotes("Original notes");
            formPage.clickSubmit();
        }

        @Test
        @DisplayName("Should edit draft invoice")
        void shouldEditDraftInvoice() {
            createTestClient();
            createTestInvoice();

            // Click edit button
            page.locator("a:has-text('Edit')").click();
            page.waitForLoadState();

            // Verify we're on edit form
            assertThat(page.url()).contains("/edit");

            // Verify form fields are populated from database
            String invoiceDateValue = page.locator("#invoiceDate").inputValue();
            assertThat(invoiceDateValue).as("Invoice date should be populated").isNotEmpty();

            String dueDateValue = page.locator("#dueDate").inputValue();
            assertThat(dueDateValue).as("Due date should be populated").isNotEmpty();

            String amountValue = page.locator("#amount").inputValue();
            assertThat(amountValue).as("Amount should be populated").isNotEmpty();

            // Clear and update amount
            page.locator("#amount").clear();
            page.locator("#amount").fill("7500000");

            // Clear and update notes
            page.locator("#notes").clear();
            page.locator("#notes").fill("Updated notes");

            // Submit form
            page.locator("#btn-simpan").click();
            page.waitForLoadState();

            // Should redirect to detail page with updated values
            page.waitForURL("**/invoices/**", new com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(10000));
            assertThat(page.url()).doesNotContain("/edit");
            // Amount is formatted as "7,500,000" (comma as thousands separator)
            assertThat(page.locator("body").textContent()).contains("7,500,000");
        }

        @Test
        @DisplayName("Should not allow edit on non-draft invoice")
        void shouldNotAllowEditOnNonDraftInvoice() {
            createTestClient();
            createTestInvoice();

            // Send first
            detailPage.clickSendButton();
            detailPage.assertStatusText("Terkirim");

            // Navigate directly to edit URL - should redirect back
            String currentUrl = page.url();
            page.navigate(currentUrl.replace("/invoices/", "/invoices/") + "/edit");

            // Should redirect back to detail (no edit allowed)
            assertThat(page.url()).doesNotContain("/edit");
        }
    }

    @Nested
    @DisplayName("1.9.15 Invoice Delete")
    class InvoiceDeleteTests {

        private void createTestClient() {
            clientFormPage.navigateToNew();
            String uniqueCode = "CLI-DEL-" + System.currentTimeMillis();
            String uniqueName = "Delete Test Client " + System.currentTimeMillis();
            clientFormPage.fillCode(uniqueCode);
            clientFormPage.fillName(uniqueName);
            clientFormPage.clickSubmit();
        }

        private void createTestInvoice() {
            formPage.navigateToNew();
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE);

            formPage.selectClientByIndex(1);
            formPage.fillInvoiceDate(today);
            formPage.fillDueDate(dueDate);
            formPage.fillAmount("3000000");
            formPage.clickSubmit();
        }

        @Test
        @DisplayName("Should delete draft invoice")
        void shouldDeleteDraftInvoice() {
            createTestClient();
            createTestInvoice();

            // Wait for detail page
            page.waitForLoadState();

            // Accept confirmation dialog and click delete
            page.onceDialog(dialog -> dialog.accept());
            page.locator("form[action*='/delete'] button").click();

            // Should redirect to list
            page.waitForURL("**/invoices", new com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(10000));
            assertThat(page.url()).endsWith("/invoices");
        }
    }

    @Nested
    @DisplayName("1.9.16 Invoice Print")
    class InvoicePrintTests {

        private void createTestClient() {
            clientFormPage.navigateToNew();
            String uniqueCode = "CLI-PRT-" + System.currentTimeMillis();
            String uniqueName = "Print Test Client " + System.currentTimeMillis();
            clientFormPage.fillCode(uniqueCode);
            clientFormPage.fillName(uniqueName);
            clientFormPage.clickSubmit();
        }

        private void createTestInvoice() {
            formPage.navigateToNew();
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String dueDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_DATE);

            formPage.selectClientByIndex(1);
            formPage.fillInvoiceDate(today);
            formPage.fillDueDate(dueDate);
            formPage.fillAmount("8000000");
            formPage.clickSubmit();
        }

        @Test
        @DisplayName("Should display invoice print view")
        void shouldDisplayInvoicePrintView() {
            createTestClient();
            createTestInvoice();

            // Get invoice number from URL
            String url = page.url();
            String invoiceNumber = url.substring(url.lastIndexOf("/") + 1);

            // Navigate to print view
            page.navigate(baseUrl() + "/invoices/" + invoiceNumber + "/print");

            // Should display printable invoice
            assertThat(page.locator("body").textContent()).contains("INVOICE");
            // Amount is formatted with comma as thousands separator
            assertThat(page.locator("body").textContent()).contains("8,000,000");
        }
    }

    @Nested
    @DisplayName("1.9.17 Invoice Filters")
    class InvoiceFilterTests {

        @Test
        @DisplayName("Should display status filter")
        void shouldDisplayStatusFilter() {
            listPage.navigate();

            assertThat(page.locator("select[name='status']").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should display client filter")
        void shouldDisplayClientFilter() {
            listPage.navigate();

            assertThat(page.locator("select[name='clientId']").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should display summary counts")
        void shouldDisplaySummaryCounts() {
            listPage.navigate();

            // Summary badges should be visible
            assertThat(page.locator("text=Draf").first().isVisible()).isTrue();
            assertThat(page.locator("text=Terkirim").first().isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should filter by status")
        void shouldFilterByStatus() {
            listPage.navigate();

            // Select DRAFT status
            page.locator("select[name='status']").selectOption("DRAFT");
            page.locator("button[type='submit']:has-text('Filter'), button:has-text('Terapkan')").first().click();

            // Page should reload with status filter
            assertThat(page.url()).contains("status=DRAFT");
        }
    }
}
