package com.artivisi.accountingfinance.dto;

import com.artivisi.accountingfinance.entity.BillLine;

import java.math.BigDecimal;
import java.util.UUID;

public record BillLineResponse(
    UUID id,
    String description,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal taxRate,
    BigDecimal taxAmount,
    BigDecimal amount,
    String expenseAccountCode,
    String expenseAccountName
) {
    public static BillLineResponse from(BillLine line) {
        return new BillLineResponse(
                line.getId(),
                line.getDescription(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getTaxRate(),
                line.getTaxAmount(),
                line.getAmount(),
                line.getExpenseAccount() != null ? line.getExpenseAccount().getAccountCode() : null,
                line.getExpenseAccount() != null ? line.getExpenseAccount().getAccountName() : null
        );
    }
}
