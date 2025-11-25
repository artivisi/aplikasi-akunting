package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.ProjectMilestone;
import com.artivisi.accountingfinance.enums.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, UUID> {

    List<ProjectMilestone> findByProjectIdOrderBySequenceAsc(UUID projectId);

    List<ProjectMilestone> findByProjectIdAndStatus(UUID projectId, MilestoneStatus status);

    Optional<ProjectMilestone> findByProjectIdAndSequence(UUID projectId, Integer sequence);

    @Query("SELECT COALESCE(MAX(m.sequence), 0) FROM ProjectMilestone m WHERE m.project.id = :projectId")
    Integer findMaxSequenceByProjectId(@Param("projectId") UUID projectId);

    long countByProjectIdAndStatus(UUID projectId, MilestoneStatus status);

    void deleteByProjectId(UUID projectId);
}
