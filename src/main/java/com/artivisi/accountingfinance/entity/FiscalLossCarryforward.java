package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks fiscal losses that can be carried forward and deducted from
 * future taxable income, per UU PPh Pasal 6 ayat 2 (max 5 years).
 */
@Entity
@Table(name = "fiscal_loss_carryforwards")
@Getter
@Setter
public class FiscalLossCarryforward {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Min(2000)
    @Max(2100)
    @Column(name = "origin_year", nullable = false, unique = true)
    private Integer originYear;

    @NotNull
    @Positive
    @Column(name = "original_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal originalAmount;

    @NotNull
    @Column(name = "used_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal usedAmount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @NotNull
    @Column(name = "expiry_year", nullable = false)
    private Integer expiryYear;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        expiryYear = originYear + 5;
        remainingAmount = originalAmount;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Whether this loss has expired (past 5-year window).
     */
    public boolean isExpired(int currentYear) {
        return currentYear > expiryYear;
    }

    /**
     * Whether this loss still has remaining balance to use.
     */
    public boolean hasRemaining() {
        return remainingAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Use (deduct) an amount from this loss carryforward.
     * Returns the actual amount used (may be less if remaining < requested).
     */
    public BigDecimal use(BigDecimal amount) {
        BigDecimal actualUse = amount.min(remainingAmount);
        usedAmount = usedAmount.add(actualUse);
        remainingAmount = remainingAmount.subtract(actualUse);
        return actualUse;
    }
}
