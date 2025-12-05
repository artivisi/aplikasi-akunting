package com.artivisi.accountingfinance.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FileEncryptionService Tests")
class FileEncryptionServiceTest {

    private FileEncryptionService service;

    // Valid 32-byte key for AES-256 (Base64 encoded)
    private static final String VALID_KEY = Base64.getEncoder().encodeToString(
            "12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        service = new FileEncryptionService();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize successfully with valid 32-byte key")
        void shouldInitializeWithValidKey() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", VALID_KEY);
            service.init();

            assertThat(service.isEncryptionEnabled()).isTrue();
        }

        @Test
        @DisplayName("Should disable encryption when no key is configured")
        void shouldDisableEncryptionWhenNoKey() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", "");
            service.init();

            assertThat(service.isEncryptionEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should disable encryption when key is null")
        void shouldDisableEncryptionWhenKeyIsNull() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", null);
            service.init();

            assertThat(service.isEncryptionEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception for invalid key length")
        void shouldThrowExceptionForInvalidKeyLength() {
            String shortKey = Base64.getEncoder().encodeToString("short".getBytes());
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", shortKey);

            assertThatThrownBy(() -> service.init())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to initialize file encryption");
        }

        @Test
        @DisplayName("Should throw exception for invalid Base64 key")
        void shouldThrowExceptionForInvalidBase64Key() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", "not-valid-base64!!!");

            assertThatThrownBy(() -> service.init())
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Encryption Tests")
    class EncryptionTests {

        @BeforeEach
        void setUpEncryption() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", VALID_KEY);
            service.init();
        }

        @Test
        @DisplayName("Should encrypt and decrypt data correctly")
        void shouldEncryptAndDecryptCorrectly() {
            byte[] original = "Hello, World! This is a test message.".getBytes(StandardCharsets.UTF_8);

            byte[] encrypted = service.encrypt(original);
            byte[] decrypted = service.decrypt(encrypted);

            assertThat(decrypted).isEqualTo(original);
        }

        @Test
        @DisplayName("Should produce different ciphertext for same plaintext (due to random IV)")
        void shouldProduceDifferentCiphertextForSamePlaintext() {
            byte[] original = "Test data".getBytes(StandardCharsets.UTF_8);

            byte[] encrypted1 = service.encrypt(original);
            byte[] encrypted2 = service.encrypt(original);

            assertThat(encrypted1).isNotEqualTo(encrypted2);
        }

        @Test
        @DisplayName("Encrypted data should be larger than original due to overhead")
        void encryptedDataShouldBeLargerThanOriginal() {
            byte[] original = "Small data".getBytes(StandardCharsets.UTF_8);

            byte[] encrypted = service.encrypt(original);

            // Overhead: MAGIC(4) + VERSION(1) + IV(12) + AUTH_TAG(16) = 33 bytes
            assertThat(encrypted.length).isGreaterThan(original.length + 30);
        }

        @Test
        @DisplayName("Should encrypt large files")
        void shouldEncryptLargeFiles() {
            // 1 MB of data
            byte[] original = new byte[1024 * 1024];
            for (int i = 0; i < original.length; i++) {
                original[i] = (byte) (i % 256);
            }

            byte[] encrypted = service.encrypt(original);
            byte[] decrypted = service.decrypt(encrypted);

            assertThat(decrypted).isEqualTo(original);
        }

        @Test
        @DisplayName("Should return null/empty for null input")
        void shouldReturnNullForNullInput() {
            assertThat(service.encrypt((byte[]) null)).isNull();
            assertThat(service.decrypt((byte[]) null)).isNull();
        }

        @Test
        @DisplayName("Should return empty array for empty input")
        void shouldReturnEmptyForEmptyInput() {
            assertThat(service.encrypt(new byte[0])).isEmpty();
            assertThat(service.decrypt(new byte[0])).isEmpty();
        }

        @Test
        @DisplayName("Should encrypt input stream")
        void shouldEncryptInputStream() throws IOException {
            byte[] original = "Stream content test".getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(original);

            byte[] encrypted = service.encrypt((InputStream) inputStream);
            byte[] decrypted = service.decrypt(encrypted);

            assertThat(decrypted).isEqualTo(original);
        }

        @Test
        @DisplayName("Should decrypt to input stream")
        void shouldDecryptToInputStream() throws IOException {
            byte[] original = "Test for stream decryption".getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = service.encrypt(original);

            InputStream decryptedStream = service.decryptToStream(encrypted);
            byte[] decrypted = decryptedStream.readAllBytes();

            assertThat(decrypted).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("Encryption Detection Tests")
    class EncryptionDetectionTests {

        @BeforeEach
        void setUpEncryption() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", VALID_KEY);
            service.init();
        }

        @Test
        @DisplayName("Should detect encrypted data")
        void shouldDetectEncryptedData() {
            byte[] original = "Test data".getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = service.encrypt(original);

            assertThat(service.isEncrypted(encrypted)).isTrue();
        }

        @Test
        @DisplayName("Should not detect non-encrypted data as encrypted")
        void shouldNotDetectNonEncryptedData() {
            byte[] plainData = "Plain text data".getBytes(StandardCharsets.UTF_8);

            assertThat(service.isEncrypted(plainData)).isFalse();
        }

        @Test
        @DisplayName("Should return false for null data")
        void shouldReturnFalseForNullData() {
            assertThat(service.isEncrypted(null)).isFalse();
        }

        @Test
        @DisplayName("Should return false for data too short")
        void shouldReturnFalseForShortData() {
            byte[] shortData = new byte[] {0x45, 0x4E, 0x43}; // Too short
            assertThat(service.isEncrypted(shortData)).isFalse();
        }

        @Test
        @DisplayName("Should decrypt non-encrypted data as passthrough")
        void shouldPassthroughNonEncryptedData() {
            byte[] plainData = "This is plain text, not encrypted".getBytes(StandardCharsets.UTF_8);

            byte[] result = service.decrypt(plainData);

            assertThat(result).isEqualTo(plainData);
        }
    }

    @Nested
    @DisplayName("Disabled Encryption Tests")
    class DisabledEncryptionTests {

        @BeforeEach
        void setUpDisabledEncryption() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", "");
            service.init();
        }

        @Test
        @DisplayName("Should pass through data when encryption is disabled")
        void shouldPassthroughWhenDisabled() {
            byte[] original = "Test data".getBytes(StandardCharsets.UTF_8);

            byte[] result = service.encrypt(original);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("Should throw exception when decrypting encrypted data without key")
        void shouldThrowExceptionWhenDecryptingWithoutKey() {
            // First, encrypt with a key
            FileEncryptionService encryptingService = new FileEncryptionService();
            ReflectionTestUtils.setField(encryptingService, "encryptionKeyBase64", VALID_KEY);
            encryptingService.init();

            byte[] encrypted = encryptingService.encrypt("Test".getBytes());

            // Now try to decrypt with disabled encryption
            assertThatThrownBy(() -> service.decrypt(encrypted))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("encryption key not configured");
        }
    }

    @Nested
    @DisplayName("Data Integrity Tests")
    class DataIntegrityTests {

        @BeforeEach
        void setUpEncryption() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", VALID_KEY);
            service.init();
        }

        @Test
        @DisplayName("Should fail decryption when ciphertext is tampered")
        void shouldFailWhenCiphertextTampered() {
            byte[] original = "Sensitive data".getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = service.encrypt(original);

            // Tamper with the ciphertext (not the header)
            encrypted[20] ^= 0xFF;

            assertThatThrownBy(() -> service.decrypt(encrypted))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to decrypt file");
        }

        @Test
        @DisplayName("Should fail decryption when IV is tampered")
        void shouldFailWhenIvTampered() {
            byte[] original = "Sensitive data".getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = service.encrypt(original);

            // Tamper with the IV (bytes 5-16)
            encrypted[6] ^= 0xFF;

            assertThatThrownBy(() -> service.decrypt(encrypted))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to decrypt file");
        }

        @Test
        @DisplayName("Should preserve binary data integrity")
        void shouldPreserveBinaryDataIntegrity() {
            // Test with all possible byte values
            byte[] original = new byte[256];
            for (int i = 0; i < 256; i++) {
                original[i] = (byte) i;
            }

            byte[] encrypted = service.encrypt(original);
            byte[] decrypted = service.decrypt(encrypted);

            assertThat(decrypted).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {

        @BeforeEach
        void setUpEncryption() {
            ReflectionTestUtils.setField(service, "encryptionKeyBase64", VALID_KEY);
            service.init();
        }

        @Test
        @DisplayName("Should return correct encryption overhead")
        void shouldReturnCorrectEncryptionOverhead() {
            // MAGIC(4) + VERSION(1) + IV(12) + AUTH_TAG(16) = 33 bytes
            assertThat(service.getEncryptionOverhead()).isEqualTo(33);
        }

        @Test
        @DisplayName("Actual overhead should match declared overhead")
        void actualOverheadShouldMatchDeclared() {
            byte[] original = new byte[100];
            byte[] encrypted = service.encrypt(original);

            int actualOverhead = encrypted.length - original.length;
            assertThat(actualOverhead).isEqualTo(service.getEncryptionOverhead());
        }
    }
}
