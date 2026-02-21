package com.artivisi.accountingfinance.dto;

import java.time.LocalDate;
import java.util.List;

public record AgingReport(
        String reportType,
        LocalDate asOfDate,
        List<AgingRow> rows,
        AgingBucket totals
) {
}
