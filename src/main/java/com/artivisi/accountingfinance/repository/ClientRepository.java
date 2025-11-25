package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByCode(String code);

    boolean existsByCode(String code);

    List<Client> findByActiveTrue();

    Page<Client> findAllByOrderByNameAsc(Pageable pageable);

    Page<Client> findByActiveTrueOrderByNameAsc(Pageable pageable);

    @Query("SELECT c FROM Client c WHERE " +
            "(:active IS NULL OR c.active = :active) AND " +
            "(LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.contactPerson) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY c.name ASC")
    Page<Client> findByFiltersAndSearch(
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT c FROM Client c WHERE " +
            "(:active IS NULL OR c.active = :active) " +
            "ORDER BY c.name ASC")
    Page<Client> findByFilters(@Param("active") Boolean active, Pageable pageable);

    long countByActiveTrue();

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.projects WHERE c.id = :id")
    Optional<Client> findByIdWithProjects(@Param("id") UUID id);
}
