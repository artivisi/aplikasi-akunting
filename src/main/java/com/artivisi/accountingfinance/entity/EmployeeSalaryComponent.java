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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_salary_components",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_employee_component",
                columnNames = {"employee_id", "salary_component_id"}))
@Getter
@Setter
@NoArgsConstructor
public class EmployeeSalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Karyawan wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull(message = "Komponen gaji wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_component_id", nullable = false)
    private SalaryComponent salaryComponent;

    // Override percentage rate for this employee (null = use component default)
    @Column(name = "rate", precision = 10, scale = 4)
    private BigDecimal rate;

    // Override fixed amount for this employee (null = use component default)
    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    // Effective date - when this component becomes active for the employee
    @NotNull(message = "Tanggal efektif wajib diisi")
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    // End date - when this component ends (null = still active)
    @Column(name = "end_date")
    private LocalDate endDate;

    // Notes for this specific assignment
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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

    /**
     * Get the effective rate for calculation.
     * Returns employee-specific rate if set, otherwise component default rate.
     */
    public BigDecimal getEffectiveRate() {
        if (rate != null) {
            return rate;
        }
        return salaryComponent != null ? salaryComponent.getDefaultRate() : null;
    }

    /**
     * Get the effective amount for calculation.
     * Returns employee-specific amount if set, otherwise component default amount.
     */
    public BigDecimal getEffectiveAmount() {
        if (amount != null) {
            return amount;
        }
        return salaryComponent != null ? salaryComponent.getDefaultAmount() : null;
    }

    /**
     * Check if this component is currently active for the employee.
     */
    public boolean isCurrentlyActive(LocalDate asOfDate) {
        if (effectiveDate == null || effectiveDate.isAfter(asOfDate)) {
            return false;
        }
        return endDate == null || !endDate.isBefore(asOfDate);
    }
}
