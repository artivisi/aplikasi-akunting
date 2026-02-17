package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.ReconciliationItem;
import com.artivisi.accountingfinance.enums.StatementItemMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReconciliationItemRepository extends JpaRepository<ReconciliationItem, UUID> {

    List<ReconciliationItem> findByReconciliationIdOrderByMatchStatusAsc(UUID reconciliationId);

    @Query("SELECT ri FROM ReconciliationItem ri " +
           "LEFT JOIN FETCH ri.statementItem " +
           "LEFT JOIN FETCH ri.transaction " +
           "WHERE ri.reconciliation.id = :reconciliationId ORDER BY ri.matchStatus ASC")
    List<ReconciliationItem> findByReconciliationIdWithRelations(@Param("reconciliationId") UUID reconciliationId);

    List<ReconciliationItem> findByReconciliationIdAndMatchStatus(
            UUID reconciliationId, StatementItemMatchStatus matchStatus);

    Optional<ReconciliationItem> findByReconciliationIdAndStatementItemId(UUID reconciliationId, UUID statementItemId);

    Optional<ReconciliationItem> findByReconciliationIdAndTransactionId(UUID reconciliationId, UUID transactionId);

    long countByReconciliationIdAndMatchStatus(UUID reconciliationId, StatementItemMatchStatus matchStatus);

    void deleteByReconciliationId(UUID reconciliationId);
}
