package com.artivisi.accountingfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "analysis_reports")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class AnalysisReport extends BaseEntity {

    @NotBlank(message = "Judul laporan wajib diisi")
    @Size(max = 255, message = "Judul maksimal 255 karakter")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Tipe laporan wajib diisi")
    @Size(max = 50, message = "Tipe laporan maksimal 50 karakter")
    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Size(max = 50, message = "Industri maksimal 50 karakter")
    @Column(name = "industry", length = 50)
    private String industry;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Size(max = 50, message = "AI source maksimal 50 karakter")
    @Column(name = "ai_source", length = 50)
    private String aiSource;

    @Size(max = 100, message = "AI model maksimal 100 karakter")
    @Column(name = "ai_model", length = 100)
    private String aiModel;

    @Column(name = "executive_summary", columnDefinition = "TEXT")
    private String executiveSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metrics", columnDefinition = "jsonb")
    private List<Map<String, String>> metrics;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "findings", columnDefinition = "jsonb")
    private List<Map<String, String>> findings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommendations", columnDefinition = "jsonb")
    private List<Map<String, String>> recommendations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "risks", columnDefinition = "jsonb")
    private List<Map<String, String>> risks;
}
