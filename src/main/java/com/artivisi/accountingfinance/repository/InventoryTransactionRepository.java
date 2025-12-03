package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.InventoryTransaction;
import com.artivisi.accountingfinance.entity.InventoryTransactionType;
import com.artivisi.accountingfinance.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product p " +
           "WHERE (:productId IS NULL OR t.product.id = :productId) " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:startDate IS NULL OR t.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    Page<InventoryTransaction> findByFilters(
            @Param("productId") UUID productId,
            @Param("transactionType") InventoryTransactionType transactionType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product " +
           "WHERE t.product.id = :productId " +
           "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    List<InventoryTransaction> findByProductId(@Param("productId") UUID productId);

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product " +
           "WHERE t.product.id = :productId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate, t.createdAt")
    List<InventoryTransaction> findByProductAndDateRange(
            @Param("productId") UUID productId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM InventoryTransaction t WHERE t.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product " +
           "WHERE t.id = :id")
    InventoryTransaction findByIdWithProduct(@Param("id") UUID id);

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product p " +
           "LEFT JOIN FETCH p.category " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate, t.createdAt")
    List<InventoryTransaction> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product p " +
           "LEFT JOIN FETCH p.category " +
           "WHERE t.product.id = :productId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate, t.createdAt")
    List<InventoryTransaction> findByProductIdAndDateRange(
            @Param("productId") UUID productId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM InventoryTransaction t " +
           "LEFT JOIN FETCH t.product p " +
           "LEFT JOIN FETCH p.category " +
           "WHERE p.category.id = :categoryId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate, t.createdAt")
    List<InventoryTransaction> findByCategoryIdAndDateRange(
            @Param("categoryId") UUID categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
