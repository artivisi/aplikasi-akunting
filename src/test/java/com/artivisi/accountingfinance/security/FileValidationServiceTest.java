package com.artivisi.accountingfinance.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FileValidationService Tests")
class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        fileValidationService = new FileValidationService();
    }

    @Nested
    @DisplayName("PDF Validation")
    class PdfValidationTests {

        @Test
        @DisplayName("Should accept valid PDF file")
        void shouldAcceptValidPdf() throws IOException {
            // PDF magic bytes: %PDF-
            byte[] pdfContent = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", pdfContent);

            boolean result = fileValidationService.validateMagicBytes(file, "application/pdf");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should reject fake PDF (exe disguised as PDF)")
        void shouldRejectFakePdf() throws IOException {
            // EXE magic bytes: MZ
            byte[] exeContent = new byte[]{0x4D, 0x5A, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "malicious.pdf", "application/pdf", exeContent);

            boolean result = fileValidationService.validateMagicBytes(file, "application/pdf");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should reject PDF with random bytes")
        void shouldRejectPdfWithRandomBytes() throws IOException {
            byte[] randomContent = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", randomContent);

            boolean result = fileValidationService.validateMagicBytes(file, "application/pdf");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Image Validation")
    class ImageValidationTests {

        @Test
        @DisplayName("Should accept valid JPEG file")
        void shouldAcceptValidJpeg() throws IOException {
            // JPEG magic bytes: FFD8FF
            byte[] jpegContent = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", jpegContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/jpeg");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept valid PNG file")
        void shouldAcceptValidPng() throws IOException {
            // PNG magic bytes: 89 50 4E 47 0D 0A 1A 0A
            byte[] pngContent = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.png", "image/png", pngContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/png");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept valid GIF87a file")
        void shouldAcceptValidGif87a() throws IOException {
            byte[] gifContent = "GIF87a".getBytes();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.gif", "image/gif", gifContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/gif");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept valid GIF89a file")
        void shouldAcceptValidGif89a() throws IOException {
            byte[] gifContent = "GIF89a".getBytes();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.gif", "image/gif", gifContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/gif");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should reject PHP disguised as image")
        void shouldRejectPhpAsImage() throws IOException {
            byte[] phpContent = "<?php echo 'hack'; ?>".getBytes();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "malicious.jpg", "image/jpeg", phpContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/jpeg");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should reject HTML disguised as image")
        void shouldRejectHtmlAsImage() throws IOException {
            byte[] htmlContent = "<html><script>alert('xss')</script></html>".getBytes();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "malicious.png", "image/png", htmlContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/png");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Office Document Validation")
    class OfficeDocumentValidationTests {

        @Test
        @DisplayName("Should accept valid XLSX file (ZIP-based)")
        void shouldAcceptValidXlsx() throws IOException {
            // XLSX (Office Open XML) starts with PK (ZIP signature)
            byte[] xlsxContent = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x00, 0x00, 0x00, 0x00};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    xlsxContent);

            boolean result = fileValidationService.validateMagicBytes(file,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept valid DOCX file (ZIP-based)")
        void shouldAcceptValidDocx() throws IOException {
            byte[] docxContent = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x00, 0x00, 0x00, 0x00};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    docxContent);

            boolean result = fileValidationService.validateMagicBytes(file,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should reject EXE disguised as XLSX")
        void shouldRejectExeAsXlsx() throws IOException {
            // EXE magic bytes: MZ
            byte[] exeContent = new byte[]{0x4D, 0x5A, (byte) 0x90, 0x00};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "malicious.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    exeContent);

            boolean result = fileValidationService.validateMagicBytes(file,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should return false for null file")
        void shouldReturnFalseForNullFile() throws IOException {
            boolean result = fileValidationService.validateMagicBytes((MockMultipartFile) null, "application/pdf");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for empty file")
        void shouldReturnFalseForEmptyFile() throws IOException {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "empty.pdf", "application/pdf", new byte[0]);

            boolean result = fileValidationService.validateMagicBytes(file, "application/pdf");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for null byte array")
        void shouldReturnFalseForNullByteArray() {
            boolean result = fileValidationService.validateMagicBytes((byte[]) null, "application/pdf");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for empty byte array")
        void shouldReturnFalseForEmptyByteArray() {
            boolean result = fileValidationService.validateMagicBytes(new byte[0], "application/pdf");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for unknown content type (allows with warning)")
        void shouldAllowUnknownContentType() throws IOException {
            byte[] content = "some content".getBytes();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.xyz", "application/x-unknown", content);

            // Unknown types are allowed but logged as warning
            boolean result = fileValidationService.validateMagicBytes(file, "application/x-unknown");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should handle file shorter than signature")
        void shouldHandleShortFile() throws IOException {
            // PNG signature is 8 bytes, file is only 3 bytes
            byte[] shortContent = new byte[]{(byte) 0x89, 0x50, 0x4E};
            MockMultipartFile file = new MockMultipartFile(
                    "file", "short.png", "image/png", shortContent);

            boolean result = fileValidationService.validateMagicBytes(file, "image/png");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Byte Array Validation")
    class ByteArrayValidationTests {

        @Test
        @DisplayName("Should validate PDF from byte array")
        void shouldValidatePdfFromByteArray() {
            byte[] pdfContent = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34};

            boolean result = fileValidationService.validateMagicBytes(pdfContent, "application/pdf");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should reject invalid content from byte array")
        void shouldRejectInvalidFromByteArray() {
            byte[] invalidContent = new byte[]{0x00, 0x01, 0x02, 0x03};

            boolean result = fileValidationService.validateMagicBytes(invalidContent, "application/pdf");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Log Sanitization")
    class LogSanitizationTests {

        @ParameterizedTest
        @CsvSource({
                "'normal.pdf', 'normal.pdf'",
                "'file\ninjection.pdf', 'file_injection.pdf'",
                "'file\rinjection.pdf', 'file_injection.pdf'",
                "'file\tinjection.pdf', 'file_injection.pdf'"
        })
        @DisplayName("Should sanitize filenames for logging")
        void shouldSanitizeFilenamesForLogging(String input, String expected) {
            String sanitized = FileValidationService.sanitizeForLog(input);

            assertThat(sanitized).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should truncate long filenames")
        void shouldTruncateLongFilenames() {
            String longFilename = "a".repeat(300);

            String sanitized = FileValidationService.sanitizeForLog(longFilename);

            assertThat(sanitized).hasSize(200 + "...[truncated]".length());
            assertThat(sanitized).endsWith("...[truncated]");
        }

        @Test
        @DisplayName("Should handle null input")
        void shouldHandleNullInput() {
            String sanitized = FileValidationService.sanitizeForLog(null);

            assertThat(sanitized).isEqualTo("null");
        }
    }
}
