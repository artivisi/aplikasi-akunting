package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.enums.ReconciliationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "bank_reconciliations")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class BankReconciliation extends BaseEntity {

    @JsonIgnore
    @NotNull(message = "Rekening bank wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bank_account", nullable = false)
    private CompanyBankAccount bankAccount;

    @JsonIgnore
    @NotNull(message = "Bank statement wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bank_statement", nullable = false)
    private BankStatement bankStatement;

    @NotNull(message = "Tanggal awal periode wajib diisi")
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull(message = "Tanggal akhir periode wajib diisi")
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReconciliationStatus status = ReconciliationStatus.DRAFT;

    @Column(name = "book_balance", precision = 19, scale = 2)
    private BigDecimal bookBalance;

    @Column(name = "bank_balance", precision = 19, scale = 2)
    private BigDecimal bankBalance;

    @Column(name = "total_statement_items")
    private Integer totalStatementItems;

    @Column(name = "matched_count")
    private Integer matchedCount;

    @Column(name = "unmatched_bank_count")
    private Integer unmatchedBankCount;

    @Column(name = "unmatched_book_count")
    private Integer unmatchedBookCount;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Size(max = 100, message = "Completed by maksimal 100 karakter")
    @Column(name = "completed_by", length = 100)
    private String completedBy;

    @Size(max = 1000, message = "Catatan maksimal 1000 karakter")
    @Column(name = "notes", length = 1000)
    private String notes;

    public boolean isDraft() {
        return status == ReconciliationStatus.DRAFT;
    }

    public boolean isInProgress() {
        return status == ReconciliationStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == ReconciliationStatus.COMPLETED;
    }
}
