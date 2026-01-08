package com.artivisi.accountingfinance.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

/**
 * Exception handler for web (HTML) controllers.
 * Renders error pages for browser requests (Accept: text/html).
 * Has higher priority than RestExceptionHandler.
 */
@ControllerAdvice
@Order(1)
@Slf4j
public class WebExceptionHandler {

    private static final String ATTR_TIMESTAMP = "timestamp";

    private boolean isHtmlRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public Object handleAccessDenied(RuntimeException ex, HttpServletRequest request,
                                     HttpServletResponse response, Model model) {
        if (!isHtmlRequest(request)) {
            // Let RestExceptionHandler handle non-HTML requests
            throw ex;
        }
        // Log full details for debugging, but don't expose to client
        log.warn("Access denied: {}", ex.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // Don't expose exception message - use generic message
        model.addAttribute(ATTR_TIMESTAMP, LocalDateTime.now());
        return "error/403";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Object handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request,
                                       HttpServletResponse response, Model model) {
        if (!isHtmlRequest(request)) {
            // Let RestExceptionHandler handle non-HTML requests
            throw ex;
        }
        // Log full details for debugging, but don't expose to client
        log.warn("Entity not found: {}", ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        // Don't expose exception message - use generic message
        model.addAttribute(ATTR_TIMESTAMP, LocalDateTime.now());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request,
                                        HttpServletResponse response, Model model) throws NoResourceFoundException {
        if (!isHtmlRequest(request)) {
            // Let RestExceptionHandler handle non-HTML requests
            throw ex;
        }
        log.debug("Resource not found: {}", ex.getResourcePath());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        // Don't expose file paths in error messages for security
        model.addAttribute(ATTR_TIMESTAMP, LocalDateTime.now());
        return "error/404";
    }
}
