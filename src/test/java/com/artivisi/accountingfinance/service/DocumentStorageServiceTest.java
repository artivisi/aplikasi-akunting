package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for DocumentStorageService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("DocumentStorageService Integration Tests")
class DocumentStorageServiceTest {

    @Autowired
    private DocumentStorageService documentStorageService;

    @Nested
    @DisplayName("File Validation Operations")
    class FileValidationTests {

        @Test
        @DisplayName("Should reject file exceeding max size")
        void shouldRejectFileExceedingMaxSize() {
            // Create a file larger than 10MB (default max size)
            byte[] largeContent = new byte[11 * 1024 * 1024]; // 11 MB
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "large-file.pdf",
                    "application/pdf",
                    largeContent
            );

            assertThatThrownBy(() -> documentStorageService.store(file))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should reject unsupported file type")
        void shouldRejectUnsupportedFileType() {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "malicious.exe",
                    "application/x-msdownload",
                    "executable content".getBytes()
            );

            assertThatThrownBy(() -> documentStorageService.store(file))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should reject empty file")
        void shouldRejectEmptyFile() {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "empty.pdf",
                    "application/pdf",
                    new byte[0]
            );

            assertThatThrownBy(() -> documentStorageService.store(file))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Load Operations")
    class LoadOperationsTests {

        @Test
        @DisplayName("Should throw exception when loading non-existent file")
        void shouldThrowExceptionWhenLoadingNonExistentFile() {
            assertThatThrownBy(() -> documentStorageService.loadAsResource("non-existent-path/file.pdf"))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Should not throw when deleting non-existent file")
        void shouldNotThrowWhenDeletingNonExistentFile() throws IOException {
            // Should not throw, just log warning
            documentStorageService.delete("non-existent-path/file.pdf");
        }
    }

    @Nested
    @DisplayName("Security Operations")
    class SecurityOperationsTests {

        @Test
        @DisplayName("Should reject path traversal in filename")
        void shouldRejectPathTraversalInFilename() {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "../../../etc/passwd",
                    "application/pdf",
                    "malicious content".getBytes()
            );

            assertThatThrownBy(() -> documentStorageService.store(file))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should reject null original filename")
        void shouldRejectNullOriginalFilename() {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    null,
                    "application/pdf",
                    "content".getBytes()
            );

            assertThatThrownBy(() -> documentStorageService.store(file))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Checksum Operations")
    class ChecksumOperationsTests {

        @Test
        @DisplayName("Should calculate checksum from bytes")
        void shouldCalculateChecksumFromBytes() {
            byte[] content = "test content for checksum".getBytes();
            String checksum = documentStorageService.calculateChecksumFromBytes(content);

            assertThat(checksum).isNotNull();
            assertThat(checksum).hasSize(64); // SHA-256 produces 64 hex chars
        }

        @Test
        @DisplayName("Should produce same checksum for same content")
        void shouldProduceSameChecksumForSameContent() {
            byte[] content = "identical content".getBytes();

            String checksum1 = documentStorageService.calculateChecksumFromBytes(content);
            String checksum2 = documentStorageService.calculateChecksumFromBytes(content);

            assertThat(checksum1).isEqualTo(checksum2);
        }

        @Test
        @DisplayName("Should produce different checksum for different content")
        void shouldProduceDifferentChecksumForDifferentContent() {
            String checksum1 = documentStorageService.calculateChecksumFromBytes("content1".getBytes());
            String checksum2 = documentStorageService.calculateChecksumFromBytes("content2".getBytes());

            assertThat(checksum1).isNotEqualTo(checksum2);
        }
    }

    @Nested
    @DisplayName("Utility Operations")
    class UtilityOperationsTests {

        @Test
        @DisplayName("Should check if file exists - false for non-existent")
        void shouldCheckIfFileExistsFalseForNonExistent() {
            boolean exists = documentStorageService.exists("non-existent-path/file.pdf");
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Should get root location")
        void shouldGetRootLocation() {
            var rootLocation = documentStorageService.getRootLocation();
            assertThat(rootLocation).isNotNull();
        }

        @Test
        @DisplayName("Should check encryption status")
        void shouldCheckEncryptionStatus() {
            boolean enabled = documentStorageService.isEncryptionEnabled();
            // Just verify it returns a value without throwing
            assertThat(enabled).isIn(true, false);
        }
    }
}
