package com.artivisi.accountingfinance.controller.api;

import com.artivisi.accountingfinance.dto.CreateTransactionRequest;
import com.artivisi.accountingfinance.dto.TransactionResponse;
import com.artivisi.accountingfinance.enums.AuditEventType;
import com.artivisi.accountingfinance.service.SecurityAuditService;
import com.artivisi.accountingfinance.service.TransactionApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API for direct transaction posting (bypassing draft workflow).
 * Used by AI assistants after user approval in client-side consultation flow.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionApiController {

    private final TransactionApiService transactionApiService;
    private final SecurityAuditService securityAuditService;

    /**
     * Create and post transaction directly (bypass draft workflow).
     * POST /api/transactions
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        String username = getCurrentUsername();
        log.info("API: Create transaction directly - merchant={}, template={}, source={}, user={}",
                request.merchant(), request.templateId(), request.source(), username);

        auditApiCall(Map.of(
                "merchant", request.merchant(),
                "amount", request.amount().toString(),
                "source", request.source(),
                "templateId", request.templateId().toString(),
                "userApproved", request.userApproved().toString()
        ));

        TransactionResponse response = transactionApiService.createTransactionDirect(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get current authenticated username.
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "API";
    }

    /**
     * Audit API calls.
     */
    private void auditApiCall(Map<String, String> details) {
        String detailsStr = String.format("API call from %s: %s",
                details.getOrDefault("source", "unknown"),
                details.toString());
        securityAuditService.log(AuditEventType.API_CALL, detailsStr, true);
    }
}
