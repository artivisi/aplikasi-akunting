package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.InvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, UUID> {

    List<InvoicePayment> findByInvoiceIdOrderByPaymentDateAsc(UUID invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM InvoicePayment p WHERE p.invoice.id = :invoiceId")
    BigDecimal sumPaymentsByInvoiceId(@Param("invoiceId") UUID invoiceId);

    @Query("SELECT p FROM InvoicePayment p JOIN FETCH p.invoice i WHERE " +
            "i.client.id = :clientId AND " +
            "p.paymentDate >= :dateFrom AND p.paymentDate <= :dateTo " +
            "ORDER BY p.paymentDate ASC")
    List<InvoicePayment> findByClientIdAndDateRange(
            @Param("clientId") UUID clientId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM InvoicePayment p WHERE " +
            "p.invoice.client.id = :clientId AND " +
            "p.paymentDate < :date")
    BigDecimal sumPaymentsBeforeDate(
            @Param("clientId") UUID clientId,
            @Param("date") LocalDate date);
}
