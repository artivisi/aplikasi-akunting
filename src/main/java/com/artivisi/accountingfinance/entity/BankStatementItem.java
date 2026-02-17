package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.enums.MatchType;
import com.artivisi.accountingfinance.enums.StatementItemMatchStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_statement_items")
@Getter
@Setter
@NoArgsConstructor
public class BankStatementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @NotNull(message = "Bank statement wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bank_statement", nullable = false)
    private BankStatement bankStatement;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @NotNull(message = "Tanggal transaksi wajib diisi")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "debit_amount", precision = 19, scale = 2)
    private BigDecimal debitAmount;

    @Column(name = "credit_amount", precision = 19, scale = 2)
    private BigDecimal creditAmount;

    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "raw_line", columnDefinition = "TEXT")
    private String rawLine;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 20)
    private StatementItemMatchStatus matchStatus = StatementItemMatchStatus.UNMATCHED;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", length = 20)
    private MatchType matchType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matched_transaction")
    private Transaction matchedTransaction;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    @Column(name = "matched_by", length = 100)
    private String matchedBy;

    public BigDecimal getNetAmount() {
        BigDecimal debit = debitAmount != null ? debitAmount : BigDecimal.ZERO;
        BigDecimal credit = creditAmount != null ? creditAmount : BigDecimal.ZERO;
        return credit.subtract(debit);
    }
}
