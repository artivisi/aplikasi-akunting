package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankStatementRepository extends JpaRepository<BankStatement, UUID> {

    @Query("SELECT bs FROM BankStatement bs LEFT JOIN FETCH bs.bankAccount LEFT JOIN FETCH bs.parserConfig " +
           "ORDER BY bs.statementPeriodEnd DESC, bs.importedAt DESC")
    List<BankStatement> findAllWithRelations();

    @Query("SELECT bs FROM BankStatement bs LEFT JOIN FETCH bs.bankAccount LEFT JOIN FETCH bs.parserConfig " +
           "WHERE bs.bankAccount.id = :bankAccountId ORDER BY bs.statementPeriodEnd DESC")
    List<BankStatement> findByBankAccountIdWithRelations(@Param("bankAccountId") UUID bankAccountId);

    @Query("SELECT bs FROM BankStatement bs LEFT JOIN FETCH bs.bankAccount LEFT JOIN FETCH bs.parserConfig " +
           "WHERE bs.id = :id")
    Optional<BankStatement> findByIdWithRelations(@Param("id") UUID id);
}
