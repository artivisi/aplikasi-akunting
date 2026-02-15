package com.artivisi.accountingfinance.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        log.warn("Access denied: {}", ex.getMessage());
        if (!isHtmlRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new RestExceptionHandler.ErrorResponse(
                            HttpStatus.FORBIDDEN.value(), "Forbidden",
                            "Akses ditolak.", LocalDateTime.now()));
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute(ATTR_TIMESTAMP, LocalDateTime.now());
        return "error/403";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Object handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request,
                                       HttpServletResponse response, Model model) {
        log.warn("Entity not found: {}", ex.getMessage());
        if (!isHtmlRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestExceptionHandler.ErrorResponse(
                            HttpStatus.NOT_FOUND.value(), "Not Found",
                            "Resource tidak ditemukan.", LocalDateTime.now()));
        }
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute(ATTR_TIMESTAMP, LocalDateTime.now());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request,
                                        HttpServletResponse response, Model model) {
        log.debug("Resource not found: {}", ex.getResourcePath());
        if (!isHtmlRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestExceptionHandler.ErrorResponse(
                            HttpStatus.NOT_FOUND.value(), "Not Found",
                            "Resource tidak ditemukan.", LocalDateTime.now()));
        }
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute(ATTR_TIMESTAMP, LocalDateTime.now());
        return "error/404";
    }
}
