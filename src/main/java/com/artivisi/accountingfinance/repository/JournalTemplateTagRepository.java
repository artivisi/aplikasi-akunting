package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.JournalTemplateTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JournalTemplateTagRepository extends JpaRepository<JournalTemplateTag, UUID> {

    List<JournalTemplateTag> findByJournalTemplateId(UUID templateId);

    @Query("SELECT DISTINCT t.tag FROM JournalTemplateTag t ORDER BY t.tag")
    List<String> findDistinctTags();

    @Query("SELECT DISTINCT t.tag FROM JournalTemplateTag t WHERE LOWER(t.tag) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY t.tag")
    List<String> searchTags(@Param("query") String query);

    @Query("SELECT t.journalTemplate.id FROM JournalTemplateTag t WHERE t.tag = :tag")
    List<UUID> findTemplateIdsByTag(@Param("tag") String tag);

    void deleteByJournalTemplateIdAndTag(UUID templateId, String tag);

    boolean existsByJournalTemplateIdAndTag(UUID templateId, String tag);
}
