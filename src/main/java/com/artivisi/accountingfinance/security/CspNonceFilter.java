package com.artivisi.accountingfinance.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates a unique CSP nonce for each HTTP request and writes CSP headers.
 *
 * This filter runs before the Spring Security filter chain to ensure CSP headers
 * are set for ALL responses, including error responses that might bypass the
 * normal security filter chain. This addresses CWE-693 (Protection Mechanism Failure).
 *
 * The nonce is stored as a request attribute and used in:
 * 1. Content-Security-Policy header (written directly by this filter)
 * 2. Inline script/style tags (via Thymeleaf templates)
 *
 * This eliminates the need for 'unsafe-inline' and 'unsafe-eval' in CSP,
 * significantly improving XSS protection.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CspNonceFilter extends OncePerRequestFilter {

    public static final String CSP_NONCE_ATTRIBUTE = "cspNonce";
    private static final String CSP_HEADER = "Content-Security-Policy";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int NONCE_LENGTH = 16; // 128 bits

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        // Generate cryptographically secure random nonce
        byte[] nonceBytes = new byte[NONCE_LENGTH];
        RANDOM.nextBytes(nonceBytes);
        String nonce = Base64.getEncoder().encodeToString(nonceBytes);

        // Store nonce in request attribute for use by templates
        request.setAttribute(CSP_NONCE_ATTRIBUTE, nonce);

        // Write CSP header BEFORE processing the request
        // This ensures CSP is set even if an error occurs downstream
        writeCspHeader(response, nonce);

        filterChain.doFilter(request, response);
    }

    /**
     * Write the Content-Security-Policy header with the given nonce.
     * This is done directly in the filter to ensure it's always set,
     * even for error responses that might bypass Spring Security.
     */
    private void writeCspHeader(HttpServletResponse response, String nonce) {
        // CSP with nonce-based scripts and styles - strict protection
        // Alpine CSP build + Alpine.data() components eliminate need for Function() constructor
        // See: https://alpinejs.dev/advanced/csp
        //
        // Both script-src and style-src use nonce-based protection:
        // - Dynamic styles use nonce'd <style> elements instead of style="" attributes
        // - All inline scripts require matching nonce
        // - No unsafe-inline or unsafe-eval allowed
        //
        // Style hashes for Alpine's internal inline styles:
        // - sha256-bsV5JivYxvGywDAZ22EZJKBFip65Ng9xoJVLbBg7bdo= = "display: none;" (x-cloak, x-show)
        // - sha256-ou12T4Lu3K6jhM7FOB2jdcFVyGsRVXgY4K7kE4tesk0= = "overflow: hidden;" (x-collapse)
        // - sha256-faU7yAF8NxuMTNEwVmBz+VcYeIoBQ2EMHW3WaVxCvnk= = HTMX indicator styles
        String cspPolicy = String.format(
            "default-src 'self'; " +
            "script-src 'self' 'nonce-%s' https://cdn.jsdelivr.net https://unpkg.com; " +
            "style-src 'self' 'nonce-%s' 'sha256-bsV5JivYxvGywDAZ22EZJKBFip65Ng9xoJVLbBg7bdo=' 'sha256-ou12T4Lu3K6jhM7FOB2jdcFVyGsRVXgY4K7kE4tesk0=' 'sha256-faU7yAF8NxuMTNEwVmBz+VcYeIoBQ2EMHW3WaVxCvnk=' https://cdn.jsdelivr.net https://fonts.googleapis.com; " +
            "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
            "img-src 'self' data: blob:; " +
            "connect-src 'self'; " +
            "frame-ancestors 'none'; " +
            "form-action 'self'; " +
            "base-uri 'self'",
            nonce, nonce
        );

        response.setHeader(CSP_HEADER, cspPolicy);
    }

    /**
     * This filter should run for all dispatcher types including ERROR dispatch.
     * This ensures CSP headers are set even for error responses.
     */
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false; // Run for error dispatch too
    }

    /**
     * This filter should run for all requests, including async dispatch.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false; // Run for async dispatch too
    }
}
