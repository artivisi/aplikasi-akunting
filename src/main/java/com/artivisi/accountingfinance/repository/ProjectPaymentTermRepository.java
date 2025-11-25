package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.ProjectPaymentTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectPaymentTermRepository extends JpaRepository<ProjectPaymentTerm, UUID> {

    List<ProjectPaymentTerm> findByProjectIdOrderBySequenceAsc(UUID projectId);

    Optional<ProjectPaymentTerm> findByProjectIdAndSequence(UUID projectId, Integer sequence);

    List<ProjectPaymentTerm> findByMilestoneId(UUID milestoneId);

    @Query("SELECT COALESCE(MAX(pt.sequence), 0) FROM ProjectPaymentTerm pt WHERE pt.project.id = :projectId")
    Integer findMaxSequenceByProjectId(@Param("projectId") UUID projectId);

    void deleteByProjectId(UUID projectId);
}
