package com.artivisi.accountingfinance.security;

import com.artivisi.accountingfinance.enums.AuditEventType;
import com.artivisi.accountingfinance.service.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Event listener for authentication events (success/failure/logout).
 * Integrates with LoginAttemptService for account lockout and SecurityAuditService for logging.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private final LoginAttemptService loginAttemptService;
    private final SecurityAuditService securityAuditService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        loginAttemptService.loginSucceeded(username);
        securityAuditService.logLogin(username, true, null);
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        loginAttemptService.loginFailed(username);

        int attempts = loginAttemptService.getFailedAttempts(username);
        String details = "Failed attempt " + attempts + " of 5";
        if (loginAttemptService.isBlocked(username)) {
            details += " - Account locked";
        }
        securityAuditService.logLogin(username, false, details);
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        securityAuditService.log(AuditEventType.LOGOUT, null);
        log.info("User logged out: {}", LogSanitizer.username(username));
    }
}
