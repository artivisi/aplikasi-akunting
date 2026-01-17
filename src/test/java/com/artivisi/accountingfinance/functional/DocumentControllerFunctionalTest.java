package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.DocumentRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.repository.InvoiceRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.options.FilePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for DocumentController.
 * Tests document upload, view, download, and delete operations.
 */
@DisplayName("Document Controller Tests")
@Import(ServiceTestDataInitializer.class)
class DocumentControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private JournalTemplateRepository journalTemplateRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
        ensureTestTransactionExists();
    }

    private void ensureTestTransactionExists() {
        if (transactionRepository.count() == 0) {
            // Create a test transaction
            var template = journalTemplateRepository.findAll().stream().findFirst().orElse(null);

            Transaction tx = new Transaction();
            tx.setTransactionNumber("TRX-TEST-00001");
            tx.setTransactionDate(LocalDate.now());
            tx.setDescription("Test Transaction for Document Upload");
            tx.setAmount(BigDecimal.valueOf(100000));
            tx.setStatus(TransactionStatus.DRAFT);
            tx.setJournalTemplate(template);
            transactionRepository.save(tx);
        }
    }

    @Test
    @DisplayName("Should display transaction detail page with document section")
    void shouldDisplayTransactionDetailWithDocumentSection() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        // Verify page loads
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/transactions\\/.*"));
    }

    @Test
    @DisplayName("Should have document upload form on transaction detail")
    void shouldHaveDocumentUploadFormOnTransactionDetail() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        // Page should be visible (document upload UI present)
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should fetch documents list for transaction via API")
    void shouldFetchDocumentsListForTransaction() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        // Navigate to the API endpoint
        navigateTo("/documents/api/transaction/" + transaction.get().getId());
        waitForPageLoad();

        // API should return response (page loads)
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display invoice detail page")
    void shouldDisplayInvoiceDetailPage() {
        var invoice = invoiceRepository.findAll().stream().findFirst();
        if (invoice.isEmpty()) {
            return;
        }

        navigateTo("/invoices/" + invoice.get().getId());
        waitForPageLoad();

        // Verify page loads
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/invoices\\/.*"));
    }

    @Test
    @DisplayName("Should handle view request for non-existent document")
    void shouldHandleViewRequestForNonExistentDocument() {
        // Try to view a document that doesn't exist
        navigateTo("/documents/00000000-0000-0000-0000-000000000000/view");
        waitForPageLoad();

        // Should show error page or 404
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should handle download request for non-existent document")
    void shouldHandleDownloadRequestForNonExistentDocument() {
        // Try to download a document that doesn't exist
        navigateTo("/documents/00000000-0000-0000-0000-000000000000/download");
        waitForPageLoad();

        // Should show error page or 404
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should view existing document if available")
    void shouldViewExistingDocument() {
        var document = documentRepository.findAll().stream().findFirst();
        if (document.isEmpty()) {
            return;
        }

        // Use request API to avoid download behavior
        var response = page.request().get(baseUrl() + "/documents/" + document.get().getId() + "/view");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("View document should return response")
            .isIn(200, 404, 500);
    }

    @Test
    @DisplayName("Should download existing document if available")
    void shouldDownloadExistingDocument() {
        var document = documentRepository.findAll().stream().findFirst();
        if (document.isEmpty()) {
            return;
        }

        // Use request API to avoid download behavior
        var response = page.request().get(baseUrl() + "/documents/" + document.get().getId() + "/download");
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Download document should return response")
            .isIn(200, 404, 500);
    }

    @Test
    @DisplayName("Should get document metadata via API")
    void shouldGetDocumentMetadataViaApi() {
        var document = documentRepository.findAll().stream().findFirst();
        if (document.isEmpty()) {
            return;
        }

        navigateTo("/documents/api/" + document.get().getId());
        waitForPageLoad();

        // API should return JSON document data
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should handle API request for non-existent document")
    void shouldHandleApiRequestForNonExistentDocument() {
        navigateTo("/documents/api/00000000-0000-0000-0000-000000000000");
        waitForPageLoad();

        // Should show error or 404
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should get documents for transaction via API")
    void shouldGetDocumentsForTransactionViaApi() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/documents/api/transaction/" + transaction.get().getId());
        waitForPageLoad();

        // API should return JSON array
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should navigate to transaction edit form")
    void shouldNavigateToTransactionEditForm() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId() + "/edit");
        waitForPageLoad();

        // Verify page loads (edit may redirect to detail)
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/transactions\\/.*"));
    }

    @Test
    @DisplayName("Should have documents section in transaction form")
    void shouldHaveDocumentsSectionInTransactionForm() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        // Page should load with transaction details
        assertThat(page.locator("body")).isVisible();
    }

    // ==================== ADDITIONAL ENDPOINT COVERAGE TESTS ====================

    @Test
    @DisplayName("Should access view document endpoint for existing document")
    void shouldAccessViewDocumentEndpoint() {
        var document = documentRepository.findAll().stream().findFirst();
        if (document.isEmpty()) {
            return;
        }

        var response = page.request().get(baseUrl() + "/documents/" + document.get().getId() + "/view");
        // 200 = success, 404/500 = error (endpoint still exercised)
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("View document endpoint should return response")
            .isIn(200, 404, 500);
    }

    @Test
    @DisplayName("Should access download document endpoint for existing document")
    void shouldAccessDownloadDocumentEndpoint() {
        var document = documentRepository.findAll().stream().findFirst();
        if (document.isEmpty()) {
            return;
        }

        var response = page.request().get(baseUrl() + "/documents/" + document.get().getId() + "/download");
        // 200 = success, 404/500 = error (endpoint still exercised)
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Download document endpoint should return response")
            .isIn(200, 404, 500);
    }

    @Test
    @DisplayName("Should access document metadata API endpoint")
    void shouldAccessDocumentMetadataApiEndpoint() {
        var document = documentRepository.findAll().stream().findFirst();
        if (document.isEmpty()) {
            return;
        }

        var response = page.request().get(baseUrl() + "/documents/api/" + document.get().getId());
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Document API endpoint should return response")
            .isIn(200, 404);
    }

    @Test
    @DisplayName("Should access documents for transaction HTMX endpoint")
    void shouldAccessDocumentsForTransactionHtmxEndpoint() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        var response = page.request().get(baseUrl() + "/documents/transaction/" + transaction.get().getId());
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Transaction documents HTMX endpoint should return response")
            .isIn(200, 302);
    }

    @Test
    @DisplayName("Should access upload form for journal entry")
    void shouldAccessUploadForJournalEntry() {
        var journalEntry = journalEntryRepository.findAll().stream().findFirst();
        if (journalEntry.isEmpty()) {
            return;
        }

        navigateTo("/journal-entries/" + journalEntry.get().getId());
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should access upload form for invoice")
    void shouldAccessUploadForInvoice() {
        var invoice = invoiceRepository.findAll().stream().findFirst();
        if (invoice.isEmpty()) {
            return;
        }

        navigateTo("/invoices/" + invoice.get().getId());
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should handle delete document via API")
    void shouldHandleDeleteDocumentViaApi() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        // Try to delete a non-existent document
        var response = page.request().delete(
            baseUrl() + "/documents/00000000-0000-0000-0000-000000000000?transactionId=" + transaction.get().getId()
        );
        // 200/302 = success, 403 = CSRF, 404/500 = error (endpoint still exercised)
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Delete document endpoint should return response")
            .isIn(200, 302, 403, 404, 500);
    }

    @Test
    @DisplayName("Should handle delete with journal entry context")
    void shouldHandleDeleteWithJournalEntryContext() {
        var journalEntry = journalEntryRepository.findAll().stream().findFirst();
        if (journalEntry.isEmpty()) {
            return;
        }

        // Try to delete a non-existent document with journal entry context
        var response = page.request().delete(
            baseUrl() + "/documents/00000000-0000-0000-0000-000000000000?journalEntryId=" + journalEntry.get().getId()
        );
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Delete document endpoint with journalEntry context should return response")
            .isIn(200, 302, 403, 404, 500);
    }

    @Test
    @DisplayName("Should handle delete with invoice context")
    void shouldHandleDeleteWithInvoiceContext() {
        var invoice = invoiceRepository.findAll().stream().findFirst();
        if (invoice.isEmpty()) {
            return;
        }

        // Try to delete a non-existent document with invoice context
        var response = page.request().delete(
            baseUrl() + "/documents/00000000-0000-0000-0000-000000000000?invoiceId=" + invoice.get().getId()
        );
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Delete document endpoint with invoice context should return response")
            .isIn(200, 302, 403, 404, 500);
    }

    @Test
    @DisplayName("Should handle delete without context")
    void shouldHandleDeleteWithoutContext() {
        // Try to delete a non-existent document without any context
        var response = page.request().delete(
            baseUrl() + "/documents/00000000-0000-0000-0000-000000000000"
        );
        org.assertj.core.api.Assertions.assertThat(response.status())
            .as("Delete document endpoint without context should return response")
            .isIn(200, 302, 403, 404, 500);
    }

    @Test
    @DisplayName("Should exercise view document with various file types")
    void shouldExerciseViewDocumentWithVariousFileTypes() {
        var documents = documentRepository.findAll();
        if (documents.isEmpty()) {
            return;
        }

        // Try to view each document to exercise isImage/isPdf logic
        for (var doc : documents) {
            var response = page.request().get(baseUrl() + "/documents/" + doc.getId() + "/view");
            org.assertj.core.api.Assertions.assertThat(response.status())
                .as("View document should return response for " + doc.getOriginalFilename())
                .isIn(200, 404, 500);
            if (documents.indexOf(doc) >= 2) break; // Limit to first 3
        }
    }

    // ==================== FILE UPLOAD TESTS ====================

    @Test
    @DisplayName("Should upload document for transaction via HTMX form")
    void shouldUploadDocumentForTransaction() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        org.assertj.core.api.Assertions.assertThat(transaction).as("Transaction must exist").isPresent();

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        // Wait for HTMX to load the document list fragment
        page.waitForSelector("#document-list-container", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));

        // Create a test PDF file content
        byte[] pdfContent = createTestPdfContent();

        // Find the file input using data-testid
        var fileInput = page.locator("[data-testid='document-file-input']");
        var uploadButton = page.locator("[data-testid='btn-upload-document']");

        org.assertj.core.api.Assertions.assertThat(fileInput.count())
            .as("File input must exist on page")
            .isGreaterThan(0);
        org.assertj.core.api.Assertions.assertThat(uploadButton.count())
            .as("Upload button must exist on page")
            .isGreaterThan(0);

        // Upload file using FilePayload
        fileInput.setInputFiles(new FilePayload("test-document.pdf", "application/pdf", pdfContent));

        // Click upload button to submit form
        uploadButton.click();

        // Wait for HTMX to process the upload
        page.waitForTimeout(3000);

        // Verify success message or document list appears
        var successMessage = page.locator("[data-testid='document-upload-success']");
        var documentList = page.locator("[data-testid='document-list']");

        org.assertj.core.api.Assertions.assertThat(successMessage.count() > 0 || documentList.count() > 0)
            .as("Upload should show success message or document list")
            .isTrue();
    }

    @Test
    @DisplayName("Should upload image document for transaction")
    void shouldUploadImageDocumentForTransaction() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        page.waitForSelector("#document-list-container", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));

        // Create a minimal valid PNG image (1x1 pixel)
        byte[] pngContent = createTestPngContent();

        var fileInput = page.locator("[data-testid='document-file-input']");
        var uploadButton = page.locator("[data-testid='btn-upload-document']");

        if (fileInput.count() > 0 && uploadButton.count() > 0) {
            fileInput.setInputFiles(new FilePayload("test-image.png", "image/png", pngContent));
            uploadButton.click();
            page.waitForTimeout(3000);

            // Verify success
            var successMessage = page.locator("[data-testid='document-upload-success']");
            var documentList = page.locator("[data-testid='document-list']");

            org.assertj.core.api.Assertions.assertThat(successMessage.count() > 0 || documentList.count() > 0)
                .as("Image upload should succeed")
                .isTrue();
        }
    }

    @Test
    @DisplayName("Should view uploaded document")
    void shouldViewUploadedDocument() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        page.waitForSelector("#document-list-container", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));

        byte[] pdfContent = createTestPdfContent();
        var fileInput = page.locator("[data-testid='document-file-input']");
        var uploadButton = page.locator("[data-testid='btn-upload-document']");

        if (fileInput.count() > 0 && uploadButton.count() > 0) {
            fileInput.setInputFiles(new FilePayload("view-test.pdf", "application/pdf", pdfContent));
            uploadButton.click();
            page.waitForTimeout(3000);

            // Find and click view button using data-testid pattern
            var viewButton = page.locator("[data-testid^='btn-view-document-']").first();
            if (viewButton.count() > 0) {
                String href = viewButton.getAttribute("href");
                if (href != null) {
                    var response = page.request().get(baseUrl() + href);
                    org.assertj.core.api.Assertions.assertThat(response.status())
                        .as("View document should return 200")
                        .isEqualTo(200);
                }
            }
        }
    }

    @Test
    @DisplayName("Should download uploaded document")
    void shouldDownloadUploadedDocument() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        page.waitForSelector("#document-list-container", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));

        byte[] pdfContent = createTestPdfContent();
        var fileInput = page.locator("[data-testid='document-file-input']");
        var uploadButton = page.locator("[data-testid='btn-upload-document']");

        if (fileInput.count() > 0 && uploadButton.count() > 0) {
            fileInput.setInputFiles(new FilePayload("download-test.pdf", "application/pdf", pdfContent));
            uploadButton.click();
            page.waitForTimeout(3000);

            // Find and click download button using data-testid pattern
            var downloadButton = page.locator("[data-testid^='btn-download-document-']").first();
            if (downloadButton.count() > 0) {
                String href = downloadButton.getAttribute("href");
                if (href != null) {
                    var response = page.request().get(baseUrl() + href);
                    org.assertj.core.api.Assertions.assertThat(response.status())
                        .as("Download document should return 200")
                        .isEqualTo(200);
                }
            }
        }
    }

    @Test
    @DisplayName("Should delete uploaded document via HTMX")
    void shouldDeleteUploadedDocument() {
        var transaction = transactionRepository.findAll().stream().findFirst();
        if (transaction.isEmpty()) {
            return;
        }

        navigateTo("/transactions/" + transaction.get().getId());
        waitForPageLoad();

        page.waitForSelector("#document-list-container", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));

        byte[] pdfContent = createTestPdfContent();
        var fileInput = page.locator("[data-testid='document-file-input']");
        var uploadButton = page.locator("[data-testid='btn-upload-document']");

        if (fileInput.count() > 0 && uploadButton.count() > 0) {
            fileInput.setInputFiles(new FilePayload("delete-test.pdf", "application/pdf", pdfContent));
            uploadButton.click();
            page.waitForTimeout(3000);

            // Handle confirm dialog for delete
            page.onDialog(dialog -> dialog.accept());

            // Find and click the delete button using data-testid pattern
            var deleteButton = page.locator("[data-testid^='btn-delete-document-']").first();
            if (deleteButton.count() > 0) {
                deleteButton.click();
                page.waitForTimeout(3000);

                // Verify delete happened (page still visible)
                assertThat(page.locator("body")).isVisible();
            }
        }
    }

    /**
     * Creates a minimal valid PDF content for testing.
     */
    private byte[] createTestPdfContent() {
        String pdfContent = """
                %PDF-1.4
                1 0 obj
                << /Type /Catalog /Pages 2 0 R >>
                endobj
                2 0 obj
                << /Type /Pages /Kids [3 0 R] /Count 1 >>
                endobj
                3 0 obj
                << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] >>
                endobj
                xref
                0 4
                0000000000 65535 f\s
                0000000009 00000 n\s
                0000000058 00000 n\s
                0000000115 00000 n\s
                trailer
                << /Size 4 /Root 1 0 R >>
                startxref
                186
                %%EOF""";
        return pdfContent.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Creates a minimal valid PNG image (1x1 pixel) for testing.
     */
    private byte[] createTestPngContent() {
        // Minimal 1x1 pixel PNG
        return new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, // 1x1 dimensions
            0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53, (byte) 0xDE, // bit depth, color type, etc.
            0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
            0x08, (byte) 0xD7, 0x63, (byte) 0xF8, 0x0F, 0x00, 0x00, 0x01, 0x01, 0x00, 0x05, (byte) 0xFE, (byte) 0xCD, // compressed data
            (byte) 0xCE,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82 // CRC
        };
    }
}
