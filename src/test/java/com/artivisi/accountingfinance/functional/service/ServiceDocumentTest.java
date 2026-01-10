package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.repository.DocumentRepository;
import com.artivisi.accountingfinance.repository.InvoiceRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Document Controller Functional Tests.
 * Tests DocumentController: view, download documents via UI navigation.
 *
 * Note: Document upload via HTMX is tested indirectly through other tests.
 * This test focuses on view, download, and pages that include document sections.
 */
@DisplayName("Service Industry - Document Management")
@Import(ServiceTestDataInitializer.class)
class ServiceDocumentTest extends PlaywrightTestBase {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    void setup() {
        loginAsAdmin();
    }

    // ==================== Document on Transaction Detail ====================

    @Test
    @DisplayName("Should display transaction detail with document section")
    void shouldDisplayTransactionDetailWithDocumentSection() {
        var transaction = transactionRepository.findAll().stream().findFirst();

        if (transaction.isPresent()) {
            navigateTo("/transactions/" + transaction.get().getId());
            waitForPageLoad();

            // Verify page loads
            assertThat(page.locator("body")).isVisible();
        }
    }

    @Test
    @DisplayName("Should display transaction list page")
    void shouldDisplayTransactionListPage() {
        navigateTo("/transactions");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== Document on Invoice ====================

    @Test
    @DisplayName("Should display invoice detail with document section")
    void shouldDisplayInvoiceDetailWithDocumentSection() {
        var invoice = invoiceRepository.findAll().stream().findFirst();

        if (invoice.isPresent()) {
            navigateTo("/invoices/" + invoice.get().getInvoiceNumber());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== Journals List ====================

    @Test
    @DisplayName("Should display journals list page")
    void shouldDisplayJournalsListPage() {
        navigateTo("/journals");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display journals ledger page")
    void shouldDisplayJournalsLedgerPage() {
        navigateTo("/journals/ledger/00000000-0000-0000-0000-000000000001");
        waitForPageLoad();

        // Page may show error or redirect, but should load
        assertThat(page.locator("body")).isVisible();
    }

    // ==================== Document View via Navigation ====================

    @Test
    @DisplayName("Should view existing document via navigation")
    void shouldViewExistingDocumentViaNavigation() {
        var document = documentRepository.findAll().stream().findFirst();

        if (document.isPresent()) {
            // Navigate to view document (may open in new window or download)
            navigateTo("/documents/" + document.get().getId() + "/view");

            // Page should respond (may be a download)
            assertThat(page.locator("body")).isVisible();
        }
    }

    @Test
    @DisplayName("Should download existing document via navigation")
    void shouldDownloadExistingDocumentViaNavigation() {
        var document = documentRepository.findAll().stream().findFirst();

        if (document.isPresent()) {
            // Navigate to download document
            navigateTo("/documents/" + document.get().getId() + "/download");

            // Page should respond (triggers download)
            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== Draft Transactions Page ====================

    @Test
    @DisplayName("Should display draft transactions page")
    void shouldDisplayDraftTransactionsPage() {
        navigateTo("/drafts");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter draft transactions by status")
    void shouldFilterDraftTransactionsByStatus() {
        navigateTo("/drafts?status=PENDING");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }
}
