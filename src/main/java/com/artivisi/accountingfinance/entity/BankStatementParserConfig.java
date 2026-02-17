package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.enums.BankStatementParserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "bank_statement_parser_configs")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class BankStatementParserConfig extends BaseEntity {

    @NotNull(message = "Tipe bank wajib diisi")
    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type", nullable = false, length = 20)
    private BankStatementParserType bankType;

    @NotBlank(message = "Nama konfigurasi wajib diisi")
    @Size(max = 100, message = "Nama konfigurasi maksimal 100 karakter")
    @Column(name = "config_name", nullable = false, length = 100)
    private String configName;

    @Size(max = 500, message = "Deskripsi maksimal 500 karakter")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Kolom tanggal wajib diisi")
    @Min(value = 0, message = "Kolom tanggal minimal 0")
    @Column(name = "date_column", nullable = false)
    private Integer dateColumn;

    @NotNull(message = "Kolom deskripsi wajib diisi")
    @Min(value = 0, message = "Kolom deskripsi minimal 0")
    @Column(name = "description_column", nullable = false)
    private Integer descriptionColumn;

    @Min(value = 0, message = "Kolom debit minimal 0")
    @Column(name = "debit_column")
    private Integer debitColumn;

    @Min(value = 0, message = "Kolom kredit minimal 0")
    @Column(name = "credit_column")
    private Integer creditColumn;

    @Min(value = 0, message = "Kolom saldo minimal 0")
    @Column(name = "balance_column")
    private Integer balanceColumn;

    @NotBlank(message = "Format tanggal wajib diisi")
    @Size(max = 50, message = "Format tanggal maksimal 50 karakter")
    @Column(name = "date_format", nullable = false, length = 50)
    private String dateFormat;

    @NotBlank(message = "Delimiter wajib diisi")
    @Size(max = 5, message = "Delimiter maksimal 5 karakter")
    @Column(name = "delimiter", nullable = false, length = 5)
    private String delimiter = ",";

    @NotNull(message = "Jumlah baris header wajib diisi")
    @Min(value = 0, message = "Jumlah baris header minimal 0")
    @Column(name = "skip_header_rows", nullable = false)
    private Integer skipHeaderRows = 1;

    @Size(max = 20, message = "Encoding maksimal 20 karakter")
    @Column(name = "encoding", length = 20)
    private String encoding = "UTF-8";

    @Size(max = 5, message = "Pemisah desimal maksimal 5 karakter")
    @Column(name = "decimal_separator", length = 5)
    private String decimalSeparator = ".";

    @Size(max = 5, message = "Pemisah ribuan maksimal 5 karakter")
    @Column(name = "thousand_separator", length = 5)
    private String thousandSeparator = ",";

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public boolean isSystemConfig() {
        return Boolean.TRUE.equals(isSystem);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
