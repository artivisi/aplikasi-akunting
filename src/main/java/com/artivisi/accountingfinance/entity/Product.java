package com.artivisi.accountingfinance.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Product master for inventory tracking.
 * Can be raw material, work-in-progress, or finished goods.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Kode produk wajib diisi")
    @Size(max = 50, message = "Kode produk maksimal 50 karakter")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Nama produk wajib diisi")
    @Size(max = 200, message = "Nama produk maksimal 200 karakter")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Size(max = 500, message = "Deskripsi maksimal 500 karakter")
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "Satuan wajib diisi")
    @Size(max = 20, message = "Satuan maksimal 20 karakter")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category")
    private ProductCategory category;

    @NotNull(message = "Metode perhitungan biaya wajib diisi")
    @Enumerated(EnumType.STRING)
    @Column(name = "costing_method", nullable = false, length = 20)
    private CostingMethod costingMethod = CostingMethod.WEIGHTED_AVERAGE;

    // Inventory tracking
    @Column(name = "track_inventory", nullable = false)
    private boolean trackInventory = true;

    @Column(name = "minimum_stock", nullable = false, precision = 15, scale = 4)
    private BigDecimal minimumStock = BigDecimal.ZERO;

    // Pricing (for sales)
    @Column(name = "selling_price", precision = 19, scale = 2)
    private BigDecimal sellingPrice;

    // Account mappings for journal generation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventory_account")
    private ChartOfAccount inventoryAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cogs_account")
    private ChartOfAccount cogsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sales_account")
    private ChartOfAccount salesAccount;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
