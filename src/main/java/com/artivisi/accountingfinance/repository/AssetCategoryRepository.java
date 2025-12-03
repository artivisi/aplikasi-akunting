package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.AssetCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetCategoryRepository extends JpaRepository<AssetCategory, UUID> {

    Optional<AssetCategory> findByCode(String code);

    boolean existsByCode(String code);

    List<AssetCategory> findByActiveTrue();

    @Query("SELECT c FROM AssetCategory c WHERE c.active = true ORDER BY c.code")
    List<AssetCategory> findAllActive();

    @Query("SELECT c FROM AssetCategory c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:active IS NULL OR c.active = :active) " +
           "ORDER BY c.code")
    Page<AssetCategory> findBySearch(
            @Param("search") String search,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query("SELECT c FROM AssetCategory c WHERE " +
           "(:active IS NULL OR c.active = :active) " +
           "ORDER BY c.code")
    Page<AssetCategory> findByFilters(
            @Param("active") Boolean active,
            Pageable pageable);
}
