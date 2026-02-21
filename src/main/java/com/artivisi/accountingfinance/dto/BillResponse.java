package com.artivisi.accountingfinance.dto;

import com.artivisi.accountingfinance.entity.Bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BillResponse(
    UUID id,
    String billNumber,
    String vendorName,
    String vendorCode,
    UUID vendorId,
    String vendorInvoiceNumber,
    LocalDate billDate,
    LocalDate dueDate,
    BigDecimal amount,
    BigDecimal taxAmount,
    String status,
    String notes,
    LocalDateTime approvedAt,
    String approvedBy,
    LocalDateTime paidAt,
    LocalDateTime createdAt,
    List<BillLineResponse> lines
) {
    public static BillResponse from(Bill bill) {
        List<BillLineResponse> lineResponses = bill.getLines().stream()
                .map(BillLineResponse::from)
                .toList();

        return new BillResponse(
                bill.getId(),
                bill.getBillNumber(),
                bill.getVendor() != null ? bill.getVendor().getName() : null,
                bill.getVendor() != null ? bill.getVendor().getCode() : null,
                bill.getVendor() != null ? bill.getVendor().getId() : null,
                bill.getVendorInvoiceNumber(),
                bill.getBillDate(),
                bill.getDueDate(),
                bill.getAmount(),
                bill.getTaxAmount(),
                bill.getStatus().name(),
                bill.getNotes(),
                bill.getApprovedAt(),
                bill.getApprovedBy(),
                bill.getPaidAt(),
                bill.getCreatedAt(),
                lineResponses
        );
    }
}
