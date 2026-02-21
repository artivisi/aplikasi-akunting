package com.artivisi.accountingfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatementEntry(
        LocalDate date,
        String type,
        String referenceNumber,
        String description,
        BigDecimal invoiceAmount,
        BigDecimal paymentAmount,
        BigDecimal runningBalance
) {
}
