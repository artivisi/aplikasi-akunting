package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.security.CspNonceFilter;
import com.artivisi.accountingfinance.security.CspNonceHeaderWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Custom error controller that ensures CSP headers are set for all error responses.
 * This addresses CWE-693 (Protection Mechanism Failure) by ensuring Content-Security-Policy
 * headers are applied to error pages that might otherwise bypass the security filter chain.
 */
@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int NONCE_LENGTH = 16;
    private final CspNonceHeaderWriter cspHeaderWriter = new CspNonceHeaderWriter();

    // nosemgrep: java.spring.security.unrestricted-request-mapping.unrestricted-request-mapping
    // Error controller must accept all HTTP methods to display errors for failed POST/PUT/DELETE requests.
    // This is safe because it only reads error attributes and displays them - no state changes.
    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        // Ensure CSP headers are set
        ensureCspHeaders(request, response);

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        // Check if JSON response is requested
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            return handleJsonError(statusCode);
        }

        // HTML response
        model.addAttribute("timestamp", LocalDateTime.now());

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "error/404";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "error/403";
        } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "error/400";
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return "error/500";
        }
    }

    private ResponseEntity<ErrorResponse> handleJsonError(int statusCode) {
        String error;
        String message;

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            error = "Not Found";
            message = "The requested resource was not found.";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            error = "Forbidden";
            message = "Access denied.";
        } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            error = "Bad Request";
            message = "The request was invalid.";
        } else {
            error = "Internal Server Error";
            message = "An unexpected error occurred.";
        }

        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(statusCode, error, message, LocalDateTime.now()));
    }

    /**
     * Ensure CSP headers are set for error responses.
     * Error responses might bypass the normal security filter chain,
     * so we need to manually set the CSP header.
     */
    private void ensureCspHeaders(HttpServletRequest request, HttpServletResponse response) {
        // Check if nonce already exists (set by CspNonceFilter)
        String nonce = (String) request.getAttribute(CspNonceFilter.CSP_NONCE_ATTRIBUTE);

        if (nonce == null) {
            // Generate nonce if not present (error bypassed filter)
            byte[] nonceBytes = new byte[NONCE_LENGTH];
            RANDOM.nextBytes(nonceBytes);
            nonce = Base64.getEncoder().encodeToString(nonceBytes);
            request.setAttribute(CspNonceFilter.CSP_NONCE_ATTRIBUTE, nonce);
        }

        // Write CSP headers
        cspHeaderWriter.writeHeaders(request, response);
    }

    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}
}
