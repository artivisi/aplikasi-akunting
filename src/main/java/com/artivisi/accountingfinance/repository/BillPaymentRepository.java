package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BillPaymentRepository extends JpaRepository<BillPayment, UUID> {

    List<BillPayment> findByBillIdOrderByPaymentDateAsc(UUID billId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM BillPayment p WHERE p.bill.id = :billId")
    BigDecimal sumPaymentsByBillId(@Param("billId") UUID billId);

    @Query("SELECT p FROM BillPayment p JOIN FETCH p.bill b WHERE " +
            "b.vendor.id = :vendorId AND " +
            "p.paymentDate >= :dateFrom AND p.paymentDate <= :dateTo " +
            "ORDER BY p.paymentDate ASC")
    List<BillPayment> findByVendorIdAndDateRange(
            @Param("vendorId") UUID vendorId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM BillPayment p WHERE " +
            "p.bill.vendor.id = :vendorId AND " +
            "p.paymentDate < :date")
    BigDecimal sumPaymentsBeforeDate(
            @Param("vendorId") UUID vendorId,
            @Param("date") LocalDate date);
}
