package com.artivisi.accountingfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AccountStatement(
        String entityType,
        String entityCode,
        String entityName,
        LocalDate dateFrom,
        LocalDate dateTo,
        BigDecimal openingBalance,
        List<StatementEntry> entries,
        BigDecimal closingBalance
) {
}
