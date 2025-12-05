package com.artivisi.accountingfinance.security;

import org.springframework.http.ContentDisposition;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for HTTP security headers.
 * Provides RFC 6266 compliant Content-Disposition header generation
 * to prevent Reflected File Download (RFD) attacks.
 */
public final class HttpSecurityUtil {

    private HttpSecurityUtil() {
        // Utility class
    }

    /**
     * Creates an RFC 6266 compliant Content-Disposition header for file downloads.
     * Uses UTF-8 encoding for non-ASCII filenames and proper escaping.
     *
     * @param filename the filename to use in the header
     * @return properly encoded Content-Disposition header value
     */
    public static String attachmentContentDisposition(String filename) {
        return ContentDisposition.attachment()
                .filename(sanitizeFilename(filename), StandardCharsets.UTF_8)
                .build()
                .toString();
    }

    /**
     * Creates an RFC 6266 compliant Content-Disposition header for inline display.
     * Uses UTF-8 encoding for non-ASCII filenames and proper escaping.
     *
     * @param filename the filename to use in the header
     * @return properly encoded Content-Disposition header value
     */
    public static String inlineContentDisposition(String filename) {
        return ContentDisposition.inline()
                .filename(sanitizeFilename(filename), StandardCharsets.UTF_8)
                .build()
                .toString();
    }

    /**
     * Sanitizes filename to remove potentially dangerous characters.
     * This prevents RFD attacks where filenames contain special characters
     * that could be interpreted as script or command separators.
     *
     * @param filename the original filename
     * @return sanitized filename safe for use in Content-Disposition header
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "download";
        }
        // Remove path separators and null bytes
        String sanitized = filename.replaceAll("[/\\\\]", "_")
                .replace("\0", "");

        // Remove or replace characters that could be problematic in filenames
        // Keep alphanumeric, underscore, hyphen, period, and common international characters
        sanitized = sanitized.replaceAll("[<>:\"|?*]", "_");

        // Ensure filename is not empty after sanitization
        if (sanitized.isEmpty() || sanitized.matches("^\\.+$")) {
            return "download";
        }

        // Limit length to prevent issues with long filenames
        if (sanitized.length() > 200) {
            int lastDot = sanitized.lastIndexOf('.');
            if (lastDot > 0 && lastDot > sanitized.length() - 20) {
                // Preserve extension
                String extension = sanitized.substring(lastDot);
                sanitized = sanitized.substring(0, 200 - extension.length()) + extension;
            } else {
                sanitized = sanitized.substring(0, 200);
            }
        }

        return sanitized;
    }
}
