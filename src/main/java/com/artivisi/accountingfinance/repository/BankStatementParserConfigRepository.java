package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.BankStatementParserConfig;
import com.artivisi.accountingfinance.enums.BankStatementParserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankStatementParserConfigRepository extends JpaRepository<BankStatementParserConfig, UUID> {
    List<BankStatementParserConfig> findByActiveTrueOrderByConfigNameAsc();
    List<BankStatementParserConfig> findAllByOrderByConfigNameAsc();
    List<BankStatementParserConfig> findByBankTypeAndActiveTrue(BankStatementParserType bankType);
    boolean existsByConfigName(String configName);
}
