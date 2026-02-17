package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.BankReconciliation;
import com.artivisi.accountingfinance.enums.ReconciliationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankReconciliationRepository extends JpaRepository<BankReconciliation, UUID> {

    @Query("SELECT br FROM BankReconciliation br LEFT JOIN FETCH br.bankAccount LEFT JOIN FETCH br.bankStatement " +
           "ORDER BY br.periodEnd DESC, br.createdAt DESC")
    List<BankReconciliation> findAllWithRelations();

    @Query("SELECT br FROM BankReconciliation br LEFT JOIN FETCH br.bankAccount LEFT JOIN FETCH br.bankStatement " +
           "WHERE br.id = :id")
    Optional<BankReconciliation> findByIdWithRelations(@Param("id") UUID id);

    List<BankReconciliation> findByBankAccountIdAndStatus(UUID bankAccountId, ReconciliationStatus status);

    List<BankReconciliation> findByBankStatementId(UUID bankStatementId);
}
