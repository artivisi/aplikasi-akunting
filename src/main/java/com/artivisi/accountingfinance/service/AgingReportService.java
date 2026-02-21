package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.dto.AgingBucket;
import com.artivisi.accountingfinance.dto.AgingReport;
import com.artivisi.accountingfinance.dto.AgingRow;
import com.artivisi.accountingfinance.entity.Bill;
import com.artivisi.accountingfinance.entity.Invoice;
import com.artivisi.accountingfinance.repository.BillRepository;
import com.artivisi.accountingfinance.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgingReportService {

    private final InvoiceRepository invoiceRepository;
    private final BillRepository billRepository;

    public AgingReport generateReceivablesAging(LocalDate asOfDate) {
        List<Invoice> outstanding = invoiceRepository.findOutstandingInvoices();

        // Group by client
        Map<UUID, List<Invoice>> byClient = new LinkedHashMap<>();
        for (Invoice inv : outstanding) {
            byClient.computeIfAbsent(inv.getClient().getId(), k -> new ArrayList<>()).add(inv);
        }

        List<AgingRow> rows = new ArrayList<>();
        AgingBucket totals = AgingBucket.zero();

        for (Map.Entry<UUID, List<Invoice>> entry : byClient.entrySet()) {
            List<Invoice> invoices = entry.getValue();
            Invoice first = invoices.getFirst();

            AgingBucket bucket = AgingBucket.zero();
            for (Invoice inv : invoices) {
                BigDecimal amount = inv.getBalanceDue();
                if (amount.compareTo(BigDecimal.ZERO) <= 0) continue;
                bucket = addToBucket(bucket, inv.getDueDate(), asOfDate, amount);
            }

            rows.add(new AgingRow(
                    first.getClient().getId(),
                    first.getClient().getCode(),
                    first.getClient().getName(),
                    bucket
            ));
            totals = totals.add(bucket);
        }

        return new AgingReport("RECEIVABLES", asOfDate, rows, totals);
    }

    public AgingReport generatePayablesAging(LocalDate asOfDate) {
        List<Bill> outstanding = billRepository.findOutstandingBills();

        // Group by vendor
        Map<UUID, List<Bill>> byVendor = new LinkedHashMap<>();
        for (Bill bill : outstanding) {
            byVendor.computeIfAbsent(bill.getVendor().getId(), k -> new ArrayList<>()).add(bill);
        }

        List<AgingRow> rows = new ArrayList<>();
        AgingBucket totals = AgingBucket.zero();

        for (Map.Entry<UUID, List<Bill>> entry : byVendor.entrySet()) {
            List<Bill> bills = entry.getValue();
            Bill first = bills.getFirst();

            AgingBucket bucket = AgingBucket.zero();
            for (Bill bill : bills) {
                BigDecimal amount = bill.getBalanceDue();
                if (amount.compareTo(BigDecimal.ZERO) <= 0) continue;
                bucket = addToBucket(bucket, bill.getDueDate(), asOfDate, amount);
            }

            rows.add(new AgingRow(
                    first.getVendor().getId(),
                    first.getVendor().getCode(),
                    first.getVendor().getName(),
                    bucket
            ));
            totals = totals.add(bucket);
        }

        return new AgingReport("PAYABLES", asOfDate, rows, totals);
    }

    private AgingBucket addToBucket(AgingBucket existing, LocalDate dueDate, LocalDate asOfDate, BigDecimal amount) {
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, asOfDate);

        if (daysOverdue <= 0) {
            // Not yet due — current
            return new AgingBucket(
                    existing.current().add(amount),
                    existing.days1to30(),
                    existing.days31to60(),
                    existing.days61to90(),
                    existing.over90(),
                    existing.total().add(amount)
            );
        } else if (daysOverdue <= 30) {
            return new AgingBucket(
                    existing.current(),
                    existing.days1to30().add(amount),
                    existing.days31to60(),
                    existing.days61to90(),
                    existing.over90(),
                    existing.total().add(amount)
            );
        } else if (daysOverdue <= 60) {
            return new AgingBucket(
                    existing.current(),
                    existing.days1to30(),
                    existing.days31to60().add(amount),
                    existing.days61to90(),
                    existing.over90(),
                    existing.total().add(amount)
            );
        } else if (daysOverdue <= 90) {
            return new AgingBucket(
                    existing.current(),
                    existing.days1to30(),
                    existing.days31to60(),
                    existing.days61to90().add(amount),
                    existing.over90(),
                    existing.total().add(amount)
            );
        } else {
            return new AgingBucket(
                    existing.current(),
                    existing.days1to30(),
                    existing.days31to60(),
                    existing.days61to90(),
                    existing.over90().add(amount),
                    existing.total().add(amount)
            );
        }
    }
}
