package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "bank_statements")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class BankStatement extends BaseEntity {

    @JsonIgnore
    @NotNull(message = "Rekening bank wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bank_account", nullable = false)
    private CompanyBankAccount bankAccount;

    @JsonIgnore
    @NotNull(message = "Konfigurasi parser wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parser_config", nullable = false)
    private BankStatementParserConfig parserConfig;

    @NotNull(message = "Tanggal awal periode wajib diisi")
    @Column(name = "statement_period_start", nullable = false)
    private LocalDate statementPeriodStart;

    @NotNull(message = "Tanggal akhir periode wajib diisi")
    @Column(name = "statement_period_end", nullable = false)
    private LocalDate statementPeriodEnd;

    @Column(name = "opening_balance", precision = 19, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "closing_balance", precision = 19, scale = 2)
    private BigDecimal closingBalance;

    @NotBlank(message = "Nama file wajib diisi")
    @Size(max = 500, message = "Nama file maksimal 500 karakter")
    @Column(name = "original_filename", nullable = false, length = 500)
    private String originalFilename;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "total_debit", precision = 19, scale = 2)
    private BigDecimal totalDebit;

    @Column(name = "total_credit", precision = 19, scale = 2)
    private BigDecimal totalCredit;

    @Column(name = "imported_at")
    private LocalDateTime importedAt;

    @Size(max = 100, message = "Imported by maksimal 100 karakter")
    @Column(name = "imported_by", length = 100)
    private String importedBy;
}
