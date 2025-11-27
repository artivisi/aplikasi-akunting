package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.TaxTransactionDetail;
import com.artivisi.accountingfinance.enums.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxTransactionDetailRepository extends JpaRepository<TaxTransactionDetail, UUID> {

    Optional<TaxTransactionDetail> findByTransactionId(UUID transactionId);

    List<TaxTransactionDetail> findByTaxType(TaxType taxType);

    @Query("SELECT t FROM TaxTransactionDetail t " +
            "JOIN t.transaction trx " +
            "WHERE t.taxType = :taxType " +
            "AND trx.transactionDate BETWEEN :startDate AND :endDate " +
            "AND trx.status = 'POSTED' " +
            "ORDER BY trx.transactionDate ASC")
    List<TaxTransactionDetail> findByTaxTypeAndDateRange(
            @Param("taxType") TaxType taxType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM TaxTransactionDetail t " +
            "JOIN t.transaction trx " +
            "WHERE t.taxType IN :taxTypes " +
            "AND trx.transactionDate BETWEEN :startDate AND :endDate " +
            "AND trx.status = 'POSTED' " +
            "ORDER BY trx.transactionDate ASC")
    List<TaxTransactionDetail> findByTaxTypesAndDateRange(
            @Param("taxTypes") List<TaxType> taxTypes,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // For e-Faktur Keluaran (PPN from sales)
    @Query("SELECT t FROM TaxTransactionDetail t " +
            "JOIN t.transaction trx " +
            "WHERE t.taxType = 'PPN_KELUARAN' " +
            "AND trx.transactionDate BETWEEN :startDate AND :endDate " +
            "AND trx.status = 'POSTED' " +
            "ORDER BY t.fakturDate ASC, t.fakturNumber ASC")
    List<TaxTransactionDetail> findEFakturKeluaranByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // For e-Faktur Masukan (PPN from purchases)
    @Query("SELECT t FROM TaxTransactionDetail t " +
            "JOIN t.transaction trx " +
            "WHERE t.taxType = 'PPN_MASUKAN' " +
            "AND trx.transactionDate BETWEEN :startDate AND :endDate " +
            "AND trx.status = 'POSTED' " +
            "ORDER BY t.fakturDate ASC, t.fakturNumber ASC")
    List<TaxTransactionDetail> findEFakturMasukanByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // For e-Bupot Unifikasi (PPh withholding)
    @Query("SELECT t FROM TaxTransactionDetail t " +
            "JOIN t.transaction trx " +
            "WHERE t.taxType IN ('PPH_21', 'PPH_23', 'PPH_42') " +
            "AND trx.transactionDate BETWEEN :startDate AND :endDate " +
            "AND trx.status = 'POSTED' " +
            "ORDER BY trx.transactionDate ASC, t.bupotNumber ASC")
    List<TaxTransactionDetail> findEBupotUnifikasiByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    boolean existsByFakturNumber(String fakturNumber);

    boolean existsByBupotNumber(String bupotNumber);
}
