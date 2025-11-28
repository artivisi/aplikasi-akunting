package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.SalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponentType;
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
public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, UUID> {

    Optional<SalaryComponent> findByCode(String code);

    boolean existsByCode(String code);

    List<SalaryComponent> findByActiveTrue();

    List<SalaryComponent> findByActiveTrueOrderByDisplayOrderAsc();

    List<SalaryComponent> findByComponentTypeAndActiveTrue(SalaryComponentType componentType);

    List<SalaryComponent> findByComponentTypeAndActiveTrueOrderByDisplayOrderAsc(SalaryComponentType componentType);

    @Query("SELECT s FROM SalaryComponent s WHERE " +
           "(LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:type IS NULL OR s.componentType = :type) " +
           "AND (:active IS NULL OR s.active = :active) " +
           "ORDER BY s.displayOrder ASC, s.code ASC")
    Page<SalaryComponent> findByFiltersAndSearch(
            @Param("search") String search,
            @Param("type") SalaryComponentType type,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query("SELECT s FROM SalaryComponent s WHERE " +
           "(:type IS NULL OR s.componentType = :type) " +
           "AND (:active IS NULL OR s.active = :active) " +
           "ORDER BY s.displayOrder ASC, s.code ASC")
    Page<SalaryComponent> findByFilters(
            @Param("type") SalaryComponentType type,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query("SELECT MAX(s.displayOrder) FROM SalaryComponent s")
    Integer findMaxDisplayOrder();

    @Query("SELECT s FROM SalaryComponent s WHERE s.bpjsCategory = :category AND s.active = true ORDER BY s.displayOrder")
    List<SalaryComponent> findByBpjsCategoryAndActiveTrue(@Param("category") String bpjsCategory);

    long countByActiveTrue();
}
