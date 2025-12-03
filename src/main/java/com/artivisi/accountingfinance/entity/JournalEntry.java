package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.enums.JournalEntryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class JournalEntry extends BaseEntity {

    @NotBlank(message = "Journal number is required")
    @Size(max = 50, message = "Journal number must not exceed 50 characters")
    @Column(name = "journal_number", nullable = false, unique = true, length = 50)
    private String journalNumber;

    @NotNull(message = "Journal date is required")
    @Column(name = "journal_date", nullable = false)
    private LocalDate journalDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JournalEntryStatus status = JournalEntryStatus.DRAFT;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "voided_at")
    private LocalDateTime voidedAt;

    @Size(max = 500, message = "Void reason must not exceed 500 characters")
    @Column(name = "void_reason", length = 500)
    private String voidReason;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transaction")
    private Transaction transaction;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_project")
    private Project project;

    @JsonIgnore
    @NotNull(message = "Account is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    private ChartOfAccount account;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @NotNull(message = "Debit amount is required")
    @DecimalMin(value = "0.00", message = "Debit amount cannot be negative")
    @Column(name = "debit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @NotNull(message = "Credit amount is required")
    @DecimalMin(value = "0.00", message = "Credit amount cannot be negative")
    @Column(name = "credit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "is_reversal", nullable = false)
    private Boolean isReversal = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reversed_entry")
    private JournalEntry reversedEntry;

    public boolean isDebitEntry() {
        return debitAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isCreditEntry() {
        return creditAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getAmount() {
        return isDebitEntry() ? debitAmount : creditAmount;
    }

    public boolean isDraft() {
        return status == JournalEntryStatus.DRAFT;
    }

    public boolean isPosted() {
        return status == JournalEntryStatus.POSTED;
    }

    public boolean isVoid() {
        return status == JournalEntryStatus.VOID;
    }

    // ========== Computed Getters (delegate to Transaction when available) ==========

    /**
     * Get the effective journal date.
     * For Transaction-backed entries, returns the Transaction's date.
     * For legacy entries, returns the local journalDate.
     */
    public LocalDate getEffectiveJournalDate() {
        if (transaction != null) {
            return transaction.getTransactionDate();
        }
        return journalDate;
    }

    /**
     * Get the effective description.
     * For Transaction-backed entries, returns the Transaction's description.
     * For reversal entries, prefixes with "Reversal: ".
     * For legacy entries, returns the local description.
     */
    public String getEffectiveDescription() {
        if (transaction != null) {
            if (Boolean.TRUE.equals(isReversal)) {
                return "Reversal: " + transaction.getDescription();
            }
            return transaction.getDescription();
        }
        return description;
    }

    /**
     * Get the effective reference number.
     * For Transaction-backed entries, returns the Transaction's reference.
     * For legacy entries, returns the local referenceNumber.
     */
    public String getEffectiveReferenceNumber() {
        if (transaction != null) {
            return transaction.getReferenceNumber();
        }
        return referenceNumber;
    }

    /**
     * Check if this entry has a parent Transaction.
     */
    public boolean hasTransaction() {
        return transaction != null;
    }
}
