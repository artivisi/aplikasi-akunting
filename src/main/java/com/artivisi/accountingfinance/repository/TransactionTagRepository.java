package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.TransactionTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionTagRepository extends JpaRepository<TransactionTag, UUID> {

    List<TransactionTag> findByTransactionId(UUID transactionId);

    void deleteByTransactionId(UUID transactionId);

    long countByTagId(UUID tagId);

    long countByTagTagTypeId(UUID tagTypeId);
}
