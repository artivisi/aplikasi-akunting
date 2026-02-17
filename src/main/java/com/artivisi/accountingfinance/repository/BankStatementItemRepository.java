package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.BankStatementItem;
import com.artivisi.accountingfinance.enums.StatementItemMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankStatementItemRepository extends JpaRepository<BankStatementItem, UUID> {

    List<BankStatementItem> findByBankStatementIdOrderByLineNumberAsc(UUID bankStatementId);

    List<BankStatementItem> findByBankStatementIdAndMatchStatusOrderByLineNumberAsc(
            UUID bankStatementId, StatementItemMatchStatus matchStatus);

    @Query("SELECT bsi FROM BankStatementItem bsi LEFT JOIN FETCH bsi.matchedTransaction " +
           "WHERE bsi.bankStatement.id = :bankStatementId ORDER BY bsi.lineNumber ASC")
    List<BankStatementItem> findByBankStatementIdWithTransaction(@Param("bankStatementId") UUID bankStatementId);

    long countByBankStatementIdAndMatchStatus(UUID bankStatementId, StatementItemMatchStatus matchStatus);

    void deleteByBankStatementId(UUID bankStatementId);
}
