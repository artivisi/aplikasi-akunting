package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telegram_user_links")
@Getter
@Setter
@NoArgsConstructor
public class TelegramUserLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "telegram_user_id", unique = true)
    private Long telegramUserId;

    @Size(max = 100, message = "Telegram username must not exceed 100 characters")
    @Column(name = "telegram_username", length = 100)
    private String telegramUsername;

    @Size(max = 255, message = "Telegram first name must not exceed 255 characters")
    @Column(name = "telegram_first_name", length = 255)
    private String telegramFirstName;

    @Size(max = 10, message = "Verification code must not exceed 10 characters")
    @Column(name = "verification_code", length = 10)
    private String verificationCode;

    @Column(name = "verification_expires_at")
    private LocalDateTime verificationExpiresAt;

    @Column(name = "linked_at")
    private LocalDateTime linkedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isLinked() {
        return linkedAt != null && isActive;
    }

    public boolean isVerificationExpired() {
        return verificationExpiresAt != null && LocalDateTime.now().isAfter(verificationExpiresAt);
    }
}
