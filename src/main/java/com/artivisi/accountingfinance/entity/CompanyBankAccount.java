package com.artivisi.accountingfinance.entity;

import com.artivisi.accountingfinance.security.EncryptedStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_bank_accounts")
@Getter
@Setter
@NoArgsConstructor
public class CompanyBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Nama bank wajib diisi")
    @Size(max = 100, message = "Nama bank maksimal 100 karakter")
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Size(max = 100, message = "Cabang bank maksimal 100 karakter")
    @Column(name = "bank_branch", length = 100)
    private String bankBranch;

    // Encrypted at rest (PII protection - financial data)
    @NotBlank(message = "Nomor rekening wajib diisi")
    @Size(max = 50, message = "Nomor rekening maksimal 50 karakter")
    @Column(name = "account_number", nullable = false, length = 255)  // Extended for encrypted data
    @Convert(converter = EncryptedStringConverter.class)
    private String accountNumber;

    @NotBlank(message = "Nama pemilik rekening wajib diisi")
    @Size(max = 255, message = "Nama pemilik rekening maksimal 255 karakter")
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Size(max = 10, message = "Kode mata uang maksimal 10 karakter")
    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode = "IDR";

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account")
    private ChartOfAccount glAccount;

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

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean isDefaultAccount() {
        return Boolean.TRUE.equals(isDefault);
    }
}
