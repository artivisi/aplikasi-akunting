package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.SecurityAuditLog;
import com.artivisi.accountingfinance.enums.AuditEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {

    Page<SecurityAuditLog> findByEventType(AuditEventType eventType, Pageable pageable);

    Page<SecurityAuditLog> findByUsername(String username, Pageable pageable);

    Page<SecurityAuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT s FROM SecurityAuditLog s WHERE " +
            "(:eventType IS NULL OR s.eventType = :eventType) AND " +
            "(:username IS NULL OR s.username LIKE %:username%) AND " +
            "(:startDate IS NULL OR s.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR s.timestamp <= :endDate) " +
            "ORDER BY s.timestamp DESC")
    Page<SecurityAuditLog> search(
            @Param("eventType") AuditEventType eventType,
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<SecurityAuditLog> findByUsernameAndEventTypeAndTimestampAfter(
            String username, AuditEventType eventType, LocalDateTime after);

    long countByTimestampAfter(LocalDateTime after);
}
