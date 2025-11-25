package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByCode(String code);

    boolean existsByCode(String code);

    List<Project> findByClientId(UUID clientId);

    List<Project> findByStatus(ProjectStatus status);

    Page<Project> findAllByOrderByCodeAsc(Pageable pageable);

    @Query("SELECT p FROM Project p WHERE " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:clientId IS NULL OR p.client.id = :clientId) AND " +
            "(LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY p.code ASC")
    Page<Project> findByFiltersAndSearch(
            @Param("status") ProjectStatus status,
            @Param("clientId") UUID clientId,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT p FROM Project p WHERE " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:clientId IS NULL OR p.client.id = :clientId) " +
            "ORDER BY p.code ASC")
    Page<Project> findByFilters(
            @Param("status") ProjectStatus status,
            @Param("clientId") UUID clientId,
            Pageable pageable);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.client WHERE p.id = :id")
    Optional<Project> findByIdWithClient(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.client " +
            "LEFT JOIN FETCH p.milestones " +
            "WHERE p.id = :id")
    Optional<Project> findByIdWithDetails(@Param("id") UUID id);

    long countByStatus(ProjectStatus status);

    long countByClientId(UUID clientId);
}
