package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, UUID> {

    List<AnalysisReport> findAllByOrderByCreatedAtDesc();
}
