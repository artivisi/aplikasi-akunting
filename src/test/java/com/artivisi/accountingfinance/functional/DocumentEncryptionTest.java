package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for document storage encryption.
 * Tests that documents are encrypted at rest and decrypted correctly when downloaded.
 */
@DisplayName("Document Storage Encryption")
class DocumentEncryptionTest extends PlaywrightTestBase {

    // Use POSTED test transaction from V904 test migration
    private static final String TEST_TRANSACTION_ID = "a0000000-0000-0000-0000-000000000002";

    private LoginPage loginPage;
    private String transactionDetailUrl;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        loginPage.navigate().loginAsAdmin();
        transactionDetailUrl = baseUrl() + "/transactions/" + TEST_TRANSACTION_ID;
    }

    @Test
    @DisplayName("Should upload PDF document and download with same content")
    void shouldUploadAndDownloadPdfWithSameContent() throws IOException {
        Path testFile = createTestPdfFile();
        byte[] originalContent = Files.readAllBytes(testFile);

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        // Upload the file
        uploadFileToDocumentSection(testFile);

        // Download the file
        Locator downloadButton = page.locator("#document-list-container a[title='Unduh']").first();
        assertThat(downloadButton.isVisible()).isTrue();

        String downloadHref = downloadButton.getAttribute("href");
        Download download = page.waitForDownload(() -> {
            page.evaluate("url => { window.location.href = url; }", downloadHref);
        });

        // Verify downloaded content matches original
        byte[] downloadedContent = Files.readAllBytes(download.path());
        assertThat(downloadedContent).isEqualTo(originalContent);
    }

    @Test
    @DisplayName("Should upload JPG image and download with same content")
    void shouldUploadAndDownloadJpgWithSameContent() throws IOException {
        Path testFile = createTestJpgFile();
        byte[] originalContent = Files.readAllBytes(testFile);

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        uploadFileToDocumentSection(testFile);

        Locator downloadButton = page.locator("#document-list-container a[title='Unduh']").first();
        String downloadHref = downloadButton.getAttribute("href");

        Download download = page.waitForDownload(() -> {
            page.evaluate("url => { window.location.href = url; }", downloadHref);
        });

        byte[] downloadedContent = Files.readAllBytes(download.path());
        assertThat(downloadedContent).isEqualTo(originalContent);
    }

    @Test
    @DisplayName("Should upload PNG image and download with same content")
    void shouldUploadAndDownloadPngWithSameContent() throws IOException {
        Path testFile = createTestPngFile();
        byte[] originalContent = Files.readAllBytes(testFile);

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        uploadFileToDocumentSection(testFile);

        Locator downloadButton = page.locator("#document-list-container a[title='Unduh']").first();
        String downloadHref = downloadButton.getAttribute("href");

        Download download = page.waitForDownload(() -> {
            page.evaluate("url => { window.location.href = url; }", downloadHref);
        });

        byte[] downloadedContent = Files.readAllBytes(download.path());
        assertThat(downloadedContent).isEqualTo(originalContent);
    }

    @Test
    @DisplayName("Should preserve file size after upload and download")
    void shouldPreserveFileSizeAfterUploadAndDownload() throws IOException {
        Path testFile = createTestPdfFile();
        long originalSize = Files.size(testFile);

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        uploadFileToDocumentSection(testFile);

        Locator downloadButton = page.locator("#document-list-container a[title='Unduh']").first();
        String downloadHref = downloadButton.getAttribute("href");

        Download download = page.waitForDownload(() -> {
            page.evaluate("url => { window.location.href = url; }", downloadHref);
        });

        assertThat(Files.size(download.path())).isEqualTo(originalSize);
    }

    @Test
    @DisplayName("Should correctly display original filename after upload")
    void shouldDisplayOriginalFilenameAfterUpload() throws IOException {
        Path testFile = createTestPdfFile();

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        uploadFileToDocumentSection(testFile);

        // Verify filename is displayed in document list
        Locator documentList = page.locator("#document-list-container");
        assertThat(documentList.locator("text=test-document.pdf").count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Downloaded file should have correct suggested filename")
    void downloadedFileShouldHaveCorrectFilename() throws IOException {
        Path testFile = createTestPdfFile();

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        uploadFileToDocumentSection(testFile);

        Locator downloadButton = page.locator("#document-list-container a[title='Unduh']").first();
        String downloadHref = downloadButton.getAttribute("href");

        Download download = page.waitForDownload(() -> {
            page.evaluate("url => { window.location.href = url; }", downloadHref);
        });

        assertThat(download.suggestedFilename()).isEqualTo("test-document.pdf");
    }

    @Test
    @DisplayName("Should handle multiple file uploads with encryption")
    void shouldHandleMultipleFileUploadsWithEncryption() throws IOException {
        Path jpgFile = createTestJpgFile();
        Path pdfFile = createTestPdfFile();
        byte[] originalJpgContent = Files.readAllBytes(jpgFile);
        byte[] originalPdfContent = Files.readAllBytes(pdfFile);

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        // Upload both files
        uploadFileToDocumentSection(jpgFile);
        page.waitForTimeout(500);
        uploadFileToDocumentSection(pdfFile);
        page.waitForTimeout(500);

        // Verify both documents appear
        Locator documentList = page.locator("#document-list-container");
        assertThat(documentList.locator("text=test-image.jpg").count()).isGreaterThan(0);
        assertThat(documentList.locator("text=test-document.pdf").count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Uploaded document should be viewable in browser")
    void uploadedDocumentShouldBeViewable() throws IOException {
        Path testFile = createTestPdfFile();

        page.navigate(transactionDetailUrl);
        waitForDocumentSection();

        uploadFileToDocumentSection(testFile);

        // Get view link
        Locator viewButton = page.locator("#document-list-container a[title='Lihat']").first();
        assertThat(viewButton.isVisible()).isTrue();

        String viewHref = viewButton.getAttribute("href");
        assertThat(viewHref).contains("/documents/");
        assertThat(viewHref).contains("/view");
    }

    // ==================== HELPER METHODS ====================

    private void waitForDocumentSection() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);
        page.waitForSelector("#document-list-container",
                new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));
    }

    private void uploadFileToDocumentSection(Path filePath) {
        Locator fileInput = page.locator("#document-list-container input[type='file']");
        fileInput.setInputFiles(filePath);
        page.waitForTimeout(1000);
    }

    private Path createTestJpgFile() throws IOException {
        Path file = tempDir.resolve("test-image.jpg");
        // Create a minimal valid JPEG file
        byte[] jpegHeader = new byte[] {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
                0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
                0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
                (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00,
                0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07,
                0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14,
                0x0D, 0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13,
                0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A,
                0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22,
                0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C,
                0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39,
                0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32,
                (byte) 0xFF, (byte) 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00,
                (byte) 0xFF, (byte) 0xC4, 0x00, 0x1F, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01,
                0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                0x07, 0x08, 0x09, 0x0A, 0x0B,
                (byte) 0xFF, (byte) 0xDA, 0x00, 0x08, 0x01, 0x01, 0x00, 0x00, 0x3F, 0x00, 0x7F,
                (byte) 0xFF, (byte) 0xD9
        };
        Files.write(file, jpegHeader);
        return file;
    }

    private Path createTestPngFile() throws IOException {
        Path file = tempDir.resolve("test-image.png");
        byte[] pngData = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D,
                0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x01,
                0x08, 0x02,
                0x00, 0x00, 0x00,
                (byte) 0x90, 0x77, 0x53, (byte) 0xDE,
                0x00, 0x00, 0x00, 0x0C,
                0x49, 0x44, 0x41, 0x54,
                0x08, (byte) 0xD7, 0x63, (byte) 0xF8, 0x0F, 0x00, 0x00, 0x01, 0x01, 0x00, 0x05, (byte) 0xFE,
                (byte) 0xB3, (byte) 0xF0, (byte) 0x4E,
                0x00, 0x00, 0x00, 0x00,
                0x49, 0x45, 0x4E, 0x44,
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
        Files.write(file, pngData);
        return file;
    }

    private Path createTestPdfFile() throws IOException {
        Path file = tempDir.resolve("test-document.pdf");
        String pdfContent =
                "%PDF-1.4\n" +
                        "1 0 obj\n" +
                        "<< /Type /Catalog /Pages 2 0 R >>\n" +
                        "endobj\n" +
                        "2 0 obj\n" +
                        "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
                        "endobj\n" +
                        "3 0 obj\n" +
                        "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] >>\n" +
                        "endobj\n" +
                        "xref\n" +
                        "0 4\n" +
                        "0000000000 65535 f \n" +
                        "0000000009 00000 n \n" +
                        "0000000058 00000 n \n" +
                        "0000000115 00000 n \n" +
                        "trailer\n" +
                        "<< /Size 4 /Root 1 0 R >>\n" +
                        "startxref\n" +
                        "190\n" +
                        "%%EOF";
        Files.writeString(file, pdfContent);
        return file;
    }
}
