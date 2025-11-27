package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Kode klien wajib diisi")
    @Size(max = 50, message = "Kode klien maksimal 50 karakter")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Nama klien wajib diisi")
    @Size(max = 255, message = "Nama klien maksimal 255 karakter")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255, message = "Nama kontak maksimal 255 karakter")
    @Column(name = "contact_person")
    private String contactPerson;

    @Email(message = "Format email tidak valid")
    @Size(max = 255, message = "Email maksimal 255 karakter")
    @Column(name = "email")
    private String email;

    @Size(max = 50, message = "Nomor telepon maksimal 50 karakter")
    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Tax identification fields (for Coretax integration)
    @Size(max = 20, message = "NPWP maksimal 20 karakter")
    @Column(name = "npwp", length = 20)
    private String npwp;

    @Size(max = 22, message = "NITKU maksimal 22 karakter")
    @Column(name = "nitku", length = 22)
    private String nitku;

    @Size(max = 16, message = "NIK maksimal 16 karakter")
    @Column(name = "nik", length = 16)
    private String nik;

    @Size(max = 10, message = "Tipe ID maksimal 10 karakter")
    @Column(name = "id_type", length = 10)
    private String idType = "TIN";

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "client")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Invoice> invoices = new ArrayList<>();

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
}
