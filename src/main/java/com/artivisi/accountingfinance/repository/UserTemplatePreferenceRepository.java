package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.UserTemplatePreference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTemplatePreferenceRepository extends JpaRepository<UserTemplatePreference, UUID> {

    Optional<UserTemplatePreference> findByUserIdAndJournalTemplateId(UUID userId, UUID templateId);

    @Query("SELECT p FROM UserTemplatePreference p " +
           "JOIN FETCH p.journalTemplate t " +
           "WHERE p.user.id = :userId AND p.isFavorite = true AND t.active = true " +
           "ORDER BY t.templateName")
    List<UserTemplatePreference> findFavoritesByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM UserTemplatePreference p " +
           "JOIN FETCH p.journalTemplate t " +
           "WHERE p.user.id = :userId AND p.lastUsedAt IS NOT NULL AND t.active = true " +
           "ORDER BY p.lastUsedAt DESC")
    List<UserTemplatePreference> findRecentlyUsedByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT p FROM UserTemplatePreference p " +
           "JOIN FETCH p.journalTemplate t " +
           "WHERE p.user.id = :userId AND p.useCount > 0 AND t.active = true " +
           "ORDER BY p.useCount DESC")
    List<UserTemplatePreference> findMostUsedByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT p.journalTemplate.id FROM UserTemplatePreference p " +
           "WHERE p.user.id = :userId AND p.isFavorite = true")
    List<UUID> findFavoriteTemplateIdsByUserId(@Param("userId") UUID userId);

    void deleteByUserIdAndJournalTemplateId(UUID userId, UUID templateId);
}
