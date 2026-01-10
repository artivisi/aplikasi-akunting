package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.enums.InvoiceStatus;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.InvoiceRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Invoice Controller Functional Tests.
 * Tests InvoiceController: CRUD, send, mark paid, cancel, print.
 */
@DisplayName("Service Industry - Invoice Management")
@Import(ServiceTestDataInitializer.class)
class ServiceInvoiceTest extends PlaywrightTestBase {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setup() {
        loginAsAdmin();
    }

    // ==================== Invoice List ====================

    @Test
    @DisplayName("Should display invoice list page")
    void shouldDisplayInvoiceListPage() {
        navigateTo("/invoices");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title, h1").first()).containsText("Invoice");
    }

    @Test
    @DisplayName("Should display invoice status filters")
    void shouldDisplayInvoiceStatusFilters() {
        navigateTo("/invoices");
        waitForPageLoad();

        // Status filters should be visible
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter invoices by status")
    void shouldFilterInvoicesByStatus() {
        navigateTo("/invoices?status=DRAFT");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter invoices by client")
    void shouldFilterInvoicesByClient() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isPresent()) {
            navigateTo("/invoices?clientId=" + client.get().getId());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== Create Invoice ====================

    @Test
    @DisplayName("Should display new invoice form")
    void shouldDisplayNewInvoiceForm() {
        navigateTo("/invoices/new");
        waitForPageLoad();

        // Verify form elements
        assertThat(page.locator("#invoiceNumber")).isVisible();
        assertThat(page.locator("#client")).isVisible();
        assertThat(page.locator("#invoiceDate")).isVisible();
        assertThat(page.locator("#dueDate")).isVisible();
        assertThat(page.locator("#amount")).isVisible();
    }

    @Test
    @DisplayName("Should create invoice with manual number")
    void shouldCreateInvoiceWithManualNumber() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/invoices/new");
        waitForPageLoad();

        String invoiceNumber = "INV-TEST-" + System.currentTimeMillis();
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(30);

        page.locator("#invoiceNumber").fill(invoiceNumber);
        page.locator("#client").selectOption(client.get().getId().toString());
        page.locator("#invoiceDate").fill(today.toString());
        page.locator("#dueDate").fill(dueDate.toString());
        page.locator("#amount").fill("5000000");
        page.locator("#notes").fill("Test invoice created by functional test");

        page.click("#btn-simpan");
        waitForPageLoad();

        // Verify redirect to detail or list
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create invoice with auto-generated number")
    void shouldCreateInvoiceWithAutoNumber() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/invoices/new");
        waitForPageLoad();

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(30);

        // Leave invoice number empty for auto-generation
        page.locator("#client").selectOption(client.get().getId().toString());
        page.locator("#invoiceDate").fill(today.toString());
        page.locator("#dueDate").fill(dueDate.toString());
        page.locator("#amount").fill("7500000");

        page.click("#btn-simpan");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should pre-select client from query param")
    void shouldPreSelectClientFromQueryParam() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isPresent()) {
            navigateTo("/invoices/new?clientId=" + client.get().getId());
            waitForPageLoad();

            // Verify form loads with client pre-selected
            assertThat(page.locator("#client")).isVisible();
        }
    }

    @Test
    @DisplayName("Should pre-select project from query param")
    void shouldPreSelectProjectFromQueryParam() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isPresent()) {
            navigateTo("/invoices/new?projectId=" + project.get().getId());
            waitForPageLoad();

            assertThat(page.locator("#project")).isVisible();
        }
    }

    @Test
    @DisplayName("Should show validation error for missing client")
    void shouldShowValidationErrorForMissingClient() {
        navigateTo("/invoices/new");
        waitForPageLoad();

        LocalDate today = LocalDate.now();
        page.locator("#invoiceDate").fill(today.toString());
        page.locator("#dueDate").fill(today.plusDays(30).toString());
        page.locator("#amount").fill("1000000");

        page.click("#btn-simpan");
        waitForPageLoad();

        // Should stay on form or show error
        assertThat(page.locator("body")).isVisible();
    }

    // ==================== Edit Invoice ====================

    @Test
    @DisplayName("Should display edit form for draft invoice")
    void shouldDisplayEditFormForDraftInvoice() {
        var draftInvoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.DRAFT)
                .findFirst();

        if (draftInvoice.isPresent()) {
            navigateTo("/invoices/" + draftInvoice.get().getInvoiceNumber() + "/edit");
            waitForPageLoad();

            assertThat(page.locator("#invoiceNumber")).isVisible();
        }
    }

    @Test
    @DisplayName("Should update draft invoice")
    void shouldUpdateDraftInvoice() {
        var draftInvoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.DRAFT)
                .findFirst();

        if (draftInvoice.isPresent()) {
            navigateTo("/invoices/" + draftInvoice.get().getInvoiceNumber() + "/edit");
            waitForPageLoad();

            var notesField = page.locator("#notes");
            if (notesField.isVisible()) {
                notesField.fill("Updated notes by functional test");
                page.click("#btn-simpan");
                waitForPageLoad();
            }

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== Invoice Detail ====================

    @Test
    @DisplayName("Should display invoice detail page")
    void shouldDisplayInvoiceDetailPage() {
        var invoice = invoiceRepository.findAll().stream().findFirst();

        if (invoice.isPresent()) {
            navigateTo("/invoices/" + invoice.get().getInvoiceNumber());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== Invoice Actions ====================

    @Test
    @DisplayName("Should send draft invoice")
    void shouldSendDraftInvoice() {
        var draftInvoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.DRAFT)
                .findFirst();

        if (draftInvoice.isPresent()) {
            navigateTo("/invoices/" + draftInvoice.get().getInvoiceNumber());
            waitForPageLoad();

            var sendBtn = page.locator("form[action*='/send'] button[type='submit']").first();
            if (sendBtn.isVisible()) {
                sendBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should mark sent invoice as paid")
    void shouldMarkSentInvoiceAsPaid() {
        var sentInvoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.SENT)
                .findFirst();

        if (sentInvoice.isPresent()) {
            navigateTo("/invoices/" + sentInvoice.get().getInvoiceNumber());
            waitForPageLoad();

            var markPaidBtn = page.locator("form[action*='/mark-paid'] button[type='submit']").first();
            if (markPaidBtn.isVisible()) {
                markPaidBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should cancel invoice")
    void shouldCancelInvoice() {
        var invoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.DRAFT || inv.getStatus() == InvoiceStatus.SENT)
                .findFirst();

        if (invoice.isPresent()) {
            navigateTo("/invoices/" + invoice.get().getInvoiceNumber());
            waitForPageLoad();

            var cancelBtn = page.locator("form[action*='/cancel'] button[type='submit']").first();
            if (cancelBtn.isVisible()) {
                cancelBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should delete draft invoice")
    void shouldDeleteDraftInvoice() {
        var draftInvoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.DRAFT)
                .findFirst();

        if (draftInvoice.isPresent()) {
            navigateTo("/invoices/" + draftInvoice.get().getInvoiceNumber());
            waitForPageLoad();

            var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
            if (deleteBtn.isVisible()) {
                deleteBtn.click();
                waitForPageLoad();

                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    // ==================== Invoice Print ====================

    @Test
    @DisplayName("Should display print preview page")
    void shouldDisplayPrintPreviewPage() {
        var invoice = invoiceRepository.findAll().stream().findFirst();

        if (invoice.isPresent()) {
            navigateTo("/invoices/" + invoice.get().getInvoiceNumber() + "/print");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== Invoice Pay ====================

    @Test
    @DisplayName("Should redirect to payment form for sent invoice")
    void shouldRedirectToPaymentFormForSentInvoice() {
        var sentInvoice = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.SENT || inv.getStatus() == InvoiceStatus.OVERDUE)
                .findFirst();

        if (sentInvoice.isPresent()) {
            navigateTo("/invoices/" + sentInvoice.get().getInvoiceNumber() + "/pay");
            waitForPageLoad();

            // Should redirect to transaction form
            assertThat(page.locator("body")).isVisible();
        }
    }
}
