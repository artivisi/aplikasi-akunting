package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.enums.AuditEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Security audit log entity for tracking security-relevant events.
 * Provides compliance with PCI-DSS Requirement 10 and GDPR Article 30.
 */
@Entity
@Table(name = "security_audit_logs", indexes = {
        @Index(name = "idx_security_audit_event_type", columnList = "event_type"),
        @Index(name = "idx_security_audit_username", columnList = "username"),
        @Index(name = "idx_security_audit_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
public class SecurityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private AuditEventType eventType;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Builder-style method for creating audit log entries.
     */
    public static SecurityAuditLog of(AuditEventType eventType, String username) {
        SecurityAuditLog log = new SecurityAuditLog();
        log.setEventType(eventType);
        log.setUsername(username);
        return log;
    }

    public SecurityAuditLog withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public SecurityAuditLog withUserAgent(String userAgent) {
        // Truncate user agent if too long
        this.userAgent = userAgent != null && userAgent.length() > 500
                ? userAgent.substring(0, 500)
                : userAgent;
        return this;
    }

    public SecurityAuditLog withDetails(String details) {
        this.details = details;
        return this;
    }

    public SecurityAuditLog withSuccess(boolean success) {
        this.success = success;
        return this;
    }
}
